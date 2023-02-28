package com.example.cloudstorage.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

@Data
public class TokenDTO {

    @JsonProperty("auth-token")
    private String value;
}
