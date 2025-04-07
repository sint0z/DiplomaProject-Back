package ru.shze.demo_project.api.response;

import jakarta.annotation.Nullable;
import lombok.Data;
import org.springframework.core.io.Resource;
import ru.shze.demo_project.model.ScheduleEntity;

import java.util.List;

@Data
public class FileDTO {
    String nameFile;
    Resource file;
}
