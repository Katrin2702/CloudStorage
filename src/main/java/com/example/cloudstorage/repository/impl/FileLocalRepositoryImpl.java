package com.example.cloudstorage.repository.impl;

import com.example.cloudstorage.repository.FileLocalRepository;
import lombok.Cleanup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Repository
public class FileLocalRepositoryImpl implements FileLocalRepository {

    @Value("${data.files.remove.flag}")
    private boolean remove;

    @Override
    public boolean saveFile(MultipartFile multipartFile, String fileName, String path) throws IOException {
        var file = new File(path + fileName);
        if (file.exists() || multipartFile.isEmpty()) return false;

        var checkPath = Paths.get(path);
        if (!Files.exists(checkPath)) {
            var dir = new java.io.File(path);
            dir.mkdir();
        }

        byte[] bytes = multipartFile.getBytes();
        @Cleanup BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        stream.write(bytes);
        return true;
    }

    @Override
    public boolean deleteFile(String fileName, String path) {
        if (remove) {
            var file = new File(path + fileName);
            if (!file.exists()) return true;
            return file.delete();
        }
        return true;
    }

    @Override
    public boolean renameFile(String fileName, String path, String newName) {
        var file = new File(path + fileName);
        if (!file.exists()) return false;
        return file.renameTo(new File(path + "//" + newName));
    }
}