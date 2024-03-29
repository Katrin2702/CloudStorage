package com.example.cloudstorage;

import com.example.cloudstorage.exception.CloudServiceFileException;
import com.example.cloudstorage.model.File;
import com.example.cloudstorage.model.FileStatus;
import com.example.cloudstorage.repository.FileLocalRepository;
import com.example.cloudstorage.repository.FileRepository;
import com.example.cloudstorage.security.JwtTokenProvider;
import com.example.cloudstorage.service.CloudService;

import com.example.cloudstorage.service.Impl.CloudServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
class CloudServiceTest {

    private static CloudService cloudService;

    private static final String token = "token 123456";
    private static final String user = "user";
    private static final FileStatus active = FileStatus.ACTIVE;
    private static final Date now = new Date(System.currentTimeMillis());
    private static final File file = new File(1L, now, now, active, "test", user, "path", 100L);
    private static final List<File> files = List.of(
            new File(1L, now, now, active, "name1", user, "path", 100L),
            new File(2L, now, now, active, "name2", user, "path", 150L)
    );

    @BeforeAll
    public static void init() {
        var fileRepository = Mockito.mock(FileRepository.class);
        var jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
        var fileLocalRepository = Mockito.mock(FileLocalRepository.class);
        Mockito.when(fileRepository.findByUserNameAndNameAndStatus(user, active))
                .thenReturn(Optional.of(file));
        Mockito.when(jwtTokenProvider.getUserName(token)).thenReturn(user);
        Mockito.when(fileRepository.findByUserNameAndStatus(user, active))
                .thenReturn(files);
        cloudService = new CloudServiceImpl(fileRepository, jwtTokenProvider, fileLocalRepository);
    }

    @Test
    void whenGetFilesThenReturnsList() {
        var filesDTO = cloudService.getFiles(token, 10);
        assertEquals(filesDTO.size(), 2);
        assertEquals("name1", filesDTO.get(0).getFilename());
        assertEquals("name2", filesDTO.get(1).getFilename());
    }

    @Test
    void whenGetFileThenReturnFileWithRightProp() {
        var javaFile = cloudService.getFile(token, "test");
        assertEquals("test", javaFile.getName());
        assertEquals("path\\test", javaFile.getPath());
    }

    @Test
    void whenRenameFileThenExceptionThrows() {
        Throwable thrown = assertThrows(CloudServiceFileException.class,
                () -> cloudService.renameFile(token, "noname", "newname"));
        assertNotNull(thrown.getMessage());
    }


}