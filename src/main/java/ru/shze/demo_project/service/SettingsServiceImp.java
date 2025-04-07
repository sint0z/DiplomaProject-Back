package ru.shze.demo_project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import ru.shze.demo_project.api.request.ScheduleRequest;
import ru.shze.demo_project.api.response.MessageResponse;
import ru.shze.demo_project.model.*;
import ru.shze.demo_project.repository.ScheduleRepository;
import ru.shze.demo_project.repository.SoundRepository;
import ru.shze.demo_project.service.service_interface.ScheduleService;
import ru.shze.demo_project.service.service_interface.SettingsService;
import ru.shze.demo_project.utils.SoundPlayer;
import ru.shze.demo_project.utils.VoiceManager;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@CrossOrigin
public class SettingsServiceImp implements SettingsService {
    private final SoundPlayer player;
    private final ScheduleServiceImp scheduleService;
    private final SoundRepository soundRepository;
    private final ScheduleRepository scheduleManager;
    private final FileStorageManagerImp fileManager;
    private final ObjectMapper mapper;

    @Autowired
    public SettingsServiceImp(SoundPlayer player, ScheduleServiceImp scheduleService,
                              SoundRepository soundRepository, ScheduleRepository scheduleManager,
                              FileStorageManagerImp fileManager, ObjectMapper mapper) {
        this.player = player;
        this.scheduleService = scheduleService;
        this.soundRepository = soundRepository;
        this.scheduleManager = scheduleManager;
        this.fileManager = fileManager;
        this.mapper = mapper;
    }

    @Override
    public Resource sendingLayoutData() throws IOException {
        fileManager.deleteAll(Directory.LAYOUT);
        List<ScheduleEntity> scheduleList = this.scheduleManager.findAll();
        List<SoundEntity> sounds = scheduleList.stream()
                .map(ScheduleEntity::getSound)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<ScheduleRequest> models = scheduleList
                .stream()
                .map(s -> {
                    ScheduleRequest model = new ScheduleRequest();
                    model.setName(s.getName());
                    model.setDays(s.getDays()
                            .stream()
                            .map(ScheduleDayEntity::getDayIndex)
                            .map(Integer::toUnsignedLong)
                            .sorted()
                            .toList());
                    model.setHourStart(s.getHourStart());
                    model.setMinuteStart(s.getMinuteStart());

                    SoundEntity soundE = s.getSound();
                    model.setSoundId(soundE == null ? -1L : soundE.getId());
                    return model;
                })
                .toList();

        JSONTemplateModel template = new JSONTemplateModel();
        template.setTemplate(models);
        template.setSounds(sounds.stream().collect(Collectors.toMap(SoundEntity::getId, SoundEntity::getNameSound)));

        List<Resource> files = new ArrayList<>(sounds.stream()
                .filter(s -> s.getLocalPath() != null)
                .map(s -> fileManager.load(s.getLocalPath()))
                .toList());

        files.add(fileManager.JSONFileGeneration(template));

        return fileManager.zipFileCreator(files);
    }

    @Override
    public MessageResponse gettingLayout(MultipartFile file) throws IOException {

        JSONTemplateModel modelList = mapper.readValue(file.getBytes(), JSONTemplateModel.class);

        List<SoundEntity> sounds = modelList.getSounds()
                .entrySet()
                .stream()
                .filter(e -> !soundRepository.existsById(e.getKey()))
                .map(e -> new SoundEntity(e.getKey(), e.getValue(), LocalDate.now(),
                        null, null, null, null))
                .toList();

        soundRepository.saveAll(sounds);

        List<ScheduleRequest> template = modelList.getTemplate().stream().toList();
        template.forEach(scheduleService::createSchedule);

        return new MessageResponse("Данные добавлены в систему", new Date().toString());
    }

    public MessageResponse alarmManager(MultipartFile file) throws IOException, UnsupportedAudioFileException {
        soundRepository.findById(-1L).ifPresent(soundEntity -> fileManager.delete(soundEntity.getLocalPath()));
        String path = fileManager.store(file);

        SoundEntity e = new SoundEntity();
        e.setNameSound("System-Sound");
        e.setLocalPath(path);
        e.setId(-1L);

        soundRepository.save(e);
        return new MessageResponse("Дополнительное оповещение добавлено", new Date().toString());
    }

    public MessageResponse alarm() throws Exception {
        Optional<SoundEntity> sound = soundRepository.findById(-1L);

        if (sound.isPresent()) {
            String path = sound.get().getLocalPath();

            if(Files.exists(Path.of(path))) {
                try {
                    this.player.setProperty(path)
                            .setVolume(1)
                            .play()
                            .join();
                } finally {
                    this.player.close();
                }

                return new MessageResponse("Воспроизведение окончено", new Date().toString());
            }
        }

        return new MessageResponse("Звуковой файл отсутствует", new Date().toString());
    }

    public MessageResponse deleteAlarm() {
        var sound = soundRepository.findById(-1L);
        if (sound.isPresent()) {
            soundRepository.deleteById(-1L);
            fileManager.delete(sound
                    .get()
                    .getLocalPath());
            return new MessageResponse("Тревога удалена", new Date().toString());
        }
        return null;
    }

    public Boolean isCheckAlarm() {
        return soundRepository.findById(-1L).isPresent();
    }

    public void delete() {
        scheduleManager.deleteAll();
    }
}
