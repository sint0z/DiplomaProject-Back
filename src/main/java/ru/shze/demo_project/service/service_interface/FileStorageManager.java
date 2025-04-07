package ru.shze.demo_project.service.service_interface;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public interface FileStorageManager {
    public void init();
    public String store(MultipartFile file) throws IOException, UnsupportedAudioFileException;
    Resource load(String hashName);
}
