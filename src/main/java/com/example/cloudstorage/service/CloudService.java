package com.example.cloudstorage.service;

import com.example.cloudstorage.dto.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface CloudService {
    List<FileDTO> getFiles(String token, int limit);

    File getFile(String token, String fileName);

    void renameFile(String token, String fileName, String newName);

    void uploadFile(MultipartFile file, String token, String fileName);

    void deleteFile(String token, String fileName);
}
