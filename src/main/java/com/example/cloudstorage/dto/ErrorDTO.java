package com.example.cloudstorage.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDTO {
    private String message;
    private int id;
}