package ru.shze.demo_project.api.response;

import ch.qos.logback.core.util.Loader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SoundResponse {
    private Long id;
    private String nameSound;
    private String path;
    private LocalDate dateCreation;
    private LocalDate dateUpdate;
}
