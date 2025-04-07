package ru.shze.demo_project.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@Data
public class SoundRequest {
    private String name;
    private LocalDate dateCreate;
    private LocalDate dateUpdate;;
}
