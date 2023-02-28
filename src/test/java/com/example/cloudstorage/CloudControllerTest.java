package com.example.cloudstorage;

import com.example.cloudstorage.controller.CloudController;
import com.example.cloudstorage.dto.FileDTO;
import com.example.cloudstorage.security.JwtTokenProvider;
import com.example.cloudstorage.service.CloudService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CloudController.class)
public class CloudControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    CloudService cloudService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Value("classpath:files\\test.txt")
    static Resource resourceFile;

    private static final String token = "Bearer token";
    private static final String fileName = "test.txt";

    @Test
    @SneakyThrows
    public void whenGetFile_thenStatusOk() {
        var file = new ClassPathResource("files\\test.txt").getFile();
        when(cloudService.getFile(token, fileName)).thenReturn(file);

        mockMvc.perform(get("/file").header("auth-token", token).param("filename", fileName))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void whenGetList_thenStatusOk() {
        var list = List.of(
                FileDTO.builder().filename("name").size(100L).build()
        );
        when(cloudService.getFiles(token, 1)).thenReturn(list);
        mockMvc.perform(get("/list").header("auth-token", token).param("limit", "1"))
                .andExpect(status().isOk());
    }
}
