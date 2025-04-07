package ru.shze.demo_project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.shze.demo_project.service.service_interface.FileStorageManager;

import javax.sound.sampled.*;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

enum Directory {
    SOUND("sounds/"),
    LAYOUT("layout/");
    private final String type;

    Directory(String type) {
        this.type = type;
    }
    public String getType() {
        return this.type;
    }
}

@Service
public class FileStorageManagerImp implements FileStorageManager {
    private final String DIRECTORY_PATH = "data/";

    @Override
    public void init() {
        for (int i = 0; i < Directory.values().length; i++) {
            Path root = Paths.get(DIRECTORY_PATH + Directory.values()[i].getType());
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public String store(MultipartFile file) throws IOException, UnsupportedAudioFileException {
        String[] filePaths = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        String generatedSoundName = "";
        if (filePaths.length > 1) {
            generatedSoundName = UUID.randomUUID().toString() + ".wav";
        }
        return store(file, generatedSoundName);
    }

    public String store(MultipartFile file, String uuidName) throws IOException, UnsupportedAudioFileException {
        Path path = Paths.get(DIRECTORY_PATH + Directory.SOUND.getType(), uuidName);
        InputStream bufferedIn = new ByteArrayInputStream(file.getBytes());
        AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(bufferedIn);
        AudioFormat sourceFormat = mp3Stream.getFormat();

        AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                sourceFormat.getSampleRate(), 16,
                sourceFormat.getChannels(),
                sourceFormat.getChannels() * 2,
                sourceFormat.getSampleRate(),
                false);

        AudioInputStream converted = AudioSystem.getAudioInputStream(convertFormat, mp3Stream);

        AudioSystem.write(converted, AudioFileFormat.Type.WAVE,new File(path.toString()));
        return path.toString();
    }
    @Override
    public Resource load(String path) {
        try {
            Path file = Paths.get(path);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("По данному пути не существует файла");
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка: " + e.getMessage());
        }
    }
    public Resource JSONFileGeneration(Object data) {
        Path path = Paths.get(DIRECTORY_PATH+ Directory.LAYOUT.getType(), "layout-" + new Date().getTime() + ".json");

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path.toString()), data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return load(path.toString());
    }
    public Resource zipFileCreator(List<Resource> files) {
        Path path = Paths.get(DIRECTORY_PATH + Directory.LAYOUT, "sound-schedule-" + new Date().getTime() + ".zip");
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(path.toFile()))) {

            for (Resource file : files) {
                try (FileInputStream fis = new FileInputStream(file.getFile().getPath())) {
                    zout.putNextEntry(new ZipEntry(Objects.requireNonNull(file.getFilename())));
                    byte[] buffer = new byte[8000];
                    int count;
                    while ((count = fis.read(buffer)) != -1) {
                        zout.write(buffer, 0, count);
                    }

                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return load(path.toString());

    }
    public boolean delete(String path) {
            try {
                Path file = Paths.get(path);
                return Files.deleteIfExists(file);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка: " + e.getMessage());
            }
    }
    public void deleteAll(Directory directory) throws IOException {
        FileUtils.cleanDirectory(Paths
                .get(DIRECTORY_PATH + directory.getType()).toFile());
    }
}
