package com.example.cloudstorage.controller;

import com.example.cloudstorage.dto.FileDTO;
import com.example.cloudstorage.dto.RenameFileDTO;
import com.example.cloudstorage.service.CloudService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/")
public class CloudController {

    private final CloudService cloudService;

    @GetMapping("/list")
    public List<FileDTO> getAllFiles(@RequestHeader("auth-token") String authToken,
                                     @RequestParam("limit") @Min(1) int limit) {

        return cloudService.getFiles(authToken, limit);
    }

    @PostMapping("/file")
    public ResponseEntity<?> upload(@RequestHeader("auth-token") String authToken,
                                    @RequestParam("filename") String fileName, MultipartFile file){
        cloudService.uploadFile(authToken, file, fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/file")
    @SneakyThrows
    public ResponseEntity<byte[]> download(@RequestHeader("auth-token") String authToken,
                                           @RequestParam("filename") String fileName) {
        var file = cloudService.getFile(authToken, fileName);
        Path path = Paths.get(file.getAbsolutePath());
        var bytes = Files.readAllBytes(path);
        var probeContentType = Files.probeContentType(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(file.getName()).build().toString())
                .contentType(probeContentType != null ? MediaType.valueOf(probeContentType) : MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> delete(@RequestHeader("auth-token") String authToken,
                                    @RequestParam("filename") String fileName) {
        cloudService.deleteFile(authToken, fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/file")
    public ResponseEntity<?> rename(@RequestHeader("auth-token") String authToken,
                                    @RequestParam("filename") String fileName,
                                    @RequestBody RenameFileDTO renameFile){
        cloudService.renameFile(authToken, fileName, renameFile.getNewName());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}