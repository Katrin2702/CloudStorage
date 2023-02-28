package com.example.cloudstorage.repository;

import com.example.cloudstorage.model.File;
import com.example.cloudstorage.model.FileStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends CrudRepository<File, Long> {
    List<File> findByUserNameAndStatus(String username, FileStatus status);

    Optional<File> findByUserNameAndNameAndStatus(String username, String name, FileStatus status);
}
