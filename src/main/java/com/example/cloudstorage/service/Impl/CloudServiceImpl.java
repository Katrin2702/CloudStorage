package com.example.cloudstorage.service.Impl;

import com.example.cloudstorage.dto.FileDTO;
import com.example.cloudstorage.exception.CloudServiceFileException;
import com.example.cloudstorage.exception.CloudServiceNotFoundException;
import com.example.cloudstorage.model.File;
import com.example.cloudstorage.model.FileStatus;
import com.example.cloudstorage.repository.FileLocalRepository;
import com.example.cloudstorage.repository.FileRepository;
import com.example.cloudstorage.security.JwtTokenProvider;
import com.example.cloudstorage.service.CloudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Slf4j
@Primary
public class CloudServiceImpl implements CloudService {

    @Value("${data.files.path}")
    private String path;
    private final static String FULL_PATH = "%s\\%s\\";

    private final FileRepository repository;
    private final JwtTokenProvider tokenProvider;
    private final FileLocalRepository localRepository;


    @Autowired
    public CloudServiceImpl(FileRepository repository, JwtTokenProvider tokenProvider, FileLocalRepository localRepository) {
        this.repository = repository;
        this.tokenProvider = tokenProvider;
        this.localRepository = localRepository;
    }

    @PostConstruct
    private void init() {
        var checkPath = Paths.get(path);
        if (!Files.exists(checkPath)) {
            var file = new java.io.File(path);
            file.mkdir();
            log.info("[{}] path was created.", checkPath);
        }
    }

    @Override
    public List<FileDTO> getFiles(String token, int limit) {
        log.info("Getting files by token =[{}] and limit=[{}]", token, limit);
        var userName = getUserName(token);
        var files = repository.findByUserNameAndStatus(userName, FileStatus.ACTIVE);
        return files.stream()
                .limit(limit)
                .map(this::convertFromFile)
                .collect(Collectors.toList());
    }

    @Override
    public java.io.File getFile(String token, String fileName) {
        log.info("Getting file by token=[{}] and filename=[{}]", token, fileName);
        var userName = getUserName(token);
        var fullPath = repository.findByUserNameAndNameAndStatus(userName, fileName, FileStatus.ACTIVE)
                .orElseThrow(() -> new CloudServiceNotFoundException(format("File with name=[%s] not found.", fileName)))
                .getPath();
        return new java.io.File(fullPath + "//" + fileName);
    }

    @Override
    public void renameFile(String token, String fileName, String newName) {
        log.info("Renaming file by token=[{}] and filename=[{}] and new name=[{}]", token, fileName, newName);
        var userName = getUserName(token);
        var file = repository.findByUserNameAndNameAndStatus(userName, fileName, FileStatus.ACTIVE)
                .orElseThrow(() -> new CloudServiceNotFoundException(format("File with name=[%s] not found.", fileName)));
        if (localRepository.renameFile(fileName, file.getPath(), newName)) {
            file.setName(newName);
            repository.save(file);
        } else {
            throw new CloudServiceFileException("Failed to rename file");
        }
    }

    @Override
    public void uploadFile(MultipartFile multipartFile, String token, String fileName) {
        log.info("Uploading file by token=[{}] and filename=[{}]", token, fileName);
        var userName = getUserName(token);
        var fullPath = format(FULL_PATH, path, userName);
        try {
            if (localRepository.saveFile(multipartFile, fileName, fullPath)) {
                var now = new Date(System.currentTimeMillis());
                var file = File.builder()
                        .name(fileName)
                        .path(fullPath)
                        .userName(userName)
                        .size(multipartFile.getBytes().length)
                        .created(now)
                        .updated(now)
                        .status(FileStatus.ACTIVE)
                        .build();
                repository.save(file);
            } else {
                throw new CloudServiceFileException("Failed to upload file");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new CloudServiceFileException("Failed to upload file");
        }
    }

    @Override
    public void deleteFile(String token, String fileName) {
        log.info("Deleting file by token=[{}] and filename=[{}]", token, fileName);
        var userName = getUserName(token);
        var fullPath = format(FULL_PATH, path, userName);
        if (localRepository.deleteFile(fileName, fullPath)) {
            var file = repository.findByUserNameAndNameAndStatus(userName, fileName, FileStatus.ACTIVE)
                    .orElseThrow(() -> new CloudServiceNotFoundException(format("File with name=[%s] not found.", fileName)));
            file.setStatus(FileStatus.DELETED);
            repository.save(file);
        }
    }

    private String getUserName(String token) {
        return tokenProvider.getUserName(token);
    }

    private FileDTO convertFromFile(File file) {
        return FileDTO.builder()
                .filename(file.getName())
                .size(file.getSize())
                .build();
    }
}
