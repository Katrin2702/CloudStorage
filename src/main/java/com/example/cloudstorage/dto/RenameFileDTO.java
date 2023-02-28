package com.example.cloudstorage.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

@Data
public class RenameFileDTO {

    @JsonProperty("filename")
    private String newName;
}
