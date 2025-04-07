package ru.shze.demo_project.service;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import ru.shze.demo_project.api.response.FileDTO;
import ru.shze.demo_project.api.response.MessageResponse;
import ru.shze.demo_project.api.request.SoundRequest;
import ru.shze.demo_project.api.response.SoundResponse;
import ru.shze.demo_project.model.SoundEntity;
import ru.shze.demo_project.repository.SoundRepository;
import ru.shze.demo_project.service.service_interface.SoundServer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@CrossOrigin
public class SoundServiceImp implements SoundServer {
    private FileStorageManagerImp fileManager;
    private SoundRepository repository;

    @Autowired
    public SoundServiceImp(FileStorageManagerImp fileManager, SoundRepository repository) {
        this.fileManager = fileManager;
        this.repository = repository;
    }
    @Override
    public SoundResponse getById(Integer id) {
        return repository
                .findById(id.longValue())
                .map(s -> new SoundResponse(s.getId(), s.getNameSound(), s.getLocalPath(),
                        s.getDateCreation(), s.getDateUpdate()))
                .orElseThrow();
    }
    @Nullable
    @Override
    public List<SoundResponse> getAllSound() {
        return repository
                .findAll()
                .stream()
                .filter(s -> s.getId() > 0)
                .map(s -> new SoundResponse(s.getId(), s.getNameSound(), s.getLocalPath(),
                        s.getDateCreation(), s.getDateUpdate()))
                .collect(Collectors.toList());
    }
    @Override
    public MessageResponse addSound(SoundRequest soundRequest, @Nonnull MultipartFile file) throws IOException {
        if (!repository.existsByNameSound(soundRequest.getName())) {
            String path = null;
            try {
                path = fileManager.store(file);
            } catch (IOException | UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            }
            SoundEntity soundEntity = new SoundEntity();
            soundEntity.setId( generateUniqueId(soundRequest.getName(),path));
            soundEntity.setNameSound(soundRequest.getName());
            soundEntity.setDateCreation(soundRequest.getDateCreate());
            soundEntity.setLocalPath(path);
            repository.save(soundEntity);
            return new MessageResponse("Данные о: " + soundRequest.getName() + " добавлены",
                    new Date().toString());
        }

        return new MessageResponse("Запись с данными существует в системе",
                new Date().toString());
    }
    private Long generateUniqueId(final String name, final String path)
    {
            int id = name.hashCode() + path.hashCode();
            int length = String.valueOf(id).length();
            int Max_Length = 5;
            if(String.valueOf(id).length()>Max_Length)
            {
                id = (int) (id /Math.pow(10.0,length - Max_Length ));
            }

            return (long) Math.abs(id);
    }
    @Override
    public MessageResponse deleteSound(Long id) {
        SoundEntity s = repository
                .findById(id)
                .orElseThrow();
        repository.delete(s);

        if (s.getLocalPath() != null) {
            fileManager.delete(s.getLocalPath());
        }
        return new MessageResponse("Данные о: " + s.getNameSound() + " удалены", new Date().toString());
    }
    @Override
    public MessageResponse updateSound(Long id, SoundRequest soundRequest, MultipartFile file) throws IOException {
        if (id == null) {
            return new MessageResponse("Данные о: " + soundRequest.getName() + " не существуют", new Date().toString());
        }

        Optional<SoundEntity> entity = repository.findById(id);
        if (entity.isPresent()) {

            if (file == null || file.isEmpty()) {
                editSound(entity, soundRequest, null);
            } else {
                String path = null;
                try {
                    if (entity.get().getLocalPath() != null) {
                        String fileName = entity
                                .orElseThrow()
                                .getLocalPath();
                        fileManager.delete(entity.get().getLocalPath());
                        path = fileManager.store(Objects.requireNonNull(file), FilenameUtils.getName(fileName));
                    } else {
                        path = fileManager.store(file);
                    }
                } catch (IOException | UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                }
                editSound(entity, soundRequest, path);
            }
        }
        return new MessageResponse("Данные о: " + soundRequest.getName() + " изменены в системе", new Date().toString());
    }
    private void editSound(Optional<SoundEntity> entity, SoundRequest request, String path) throws IOException {
        SoundEntity sound = entity.orElseThrow();
        sound.setNameSound(request.getName());
        sound.setDateUpdate(LocalDate.now());
        if (path != null) {
            sound.setLocalPath(path);
        }
        repository.save(sound);
    }
    public Resource getSoundFile(Long soundId) {
        SoundEntity sound = repository.findById(soundId).orElseThrow();
        return fileManager.load(sound.getLocalPath());

    }
}
