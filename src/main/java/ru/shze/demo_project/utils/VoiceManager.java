package ru.shze.demo_project.utils;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shze.demo_project.model.ScheduleDayEntity;
import ru.shze.demo_project.model.ScheduleEntity;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;

@NoArgsConstructor
@Service
public class VoiceManager {
    @Value("${prop.system_sound_path}")
    private String SYSTEM_PATH;
    private  SoundPlayer soundPlayer;
    private final List<ScheduleEntity> entities = new ArrayList<>();
    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean isPut = false;

    @Autowired
    public VoiceManager(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    public void play() {
        exec.scheduleAtFixedRate(this::checkAndPlaySound, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private void checkAndPlaySound() {
        int day = LocalDate.now().getDayOfWeek().getValue();
        if (!entities.isEmpty()) {
            entities.stream()
                    .filter(s -> s.getDays()
                            .stream()
                            .map(ScheduleDayEntity::getDayIndex)
                            .toList()
                            .contains(day))
                    .sorted(ScheduleEntity::compare)
                    .filter(s -> LocalTime.now().isBefore(LocalTime.of(s.getHourStart(), s.getMinuteStart())))
                    .findFirst()
                    .ifPresent(entity -> scheduleSound(entity));
        }
    }

    private void scheduleSound(ScheduleEntity entity) {
        if (isPut) return;

        String path = entity.getSound().getLocalPath();
        if (!path.isEmpty()) {
            LocalTime soundTime = LocalTime.of(entity.getHourStart(), entity.getMinuteStart());
            long delay = getDelayToSoundTime(soundTime);

            isPut = true;

            exec.schedule(() -> playSound(path), delay, TimeUnit.MILLISECONDS);
        }
    }

    private long getDelayToSoundTime(LocalTime soundTime) {
        LocalTime currentTime = LocalTime.now();
        if (currentTime.isAfter(soundTime)) {
            // Если текущее время позже запланированного, ждём до следующего дня
            return getDelayToNextDay(soundTime);
        } else {
            return Duration.between(currentTime, soundTime).toMillis();
        }
    }

    private long getDelayToNextDay(LocalTime soundTime) {
        // Расчёт задержки до следующего дня
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalTime midnight = LocalTime.MIN;
        LocalTime soundTimeTomorrow = soundTime;

        return Duration.between(midnight, soundTimeTomorrow).plusDays(1).toMillis();
    }

    private void playSound(String sound) {
        try {
            soundPlayer.setProperty(sound != null ? sound : SYSTEM_PATH)
                    .setVolume(1)
                    .play()
                    .join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            isPut = false;
        }
    }

    public void addVoiceTime(ScheduleEntity entity) {
        synchronized (entities) {
            if (entity.getId() == null) {
                boolean isAfter = entities.stream()
                        .anyMatch(s -> LocalTime.of(s.getHourStart(), s.getMinuteStart(), 0)
                                .isAfter(LocalTime.of(entity.getHourStart(), entity.getMinuteStart(), 0)));

                if (isAfter || isPut) {
                    exec.shutdownNow();
                    play();
                }
                entities.add(entity);
            } else {
                deleteSound(entity);
                entities.add(entity);
            }
        }
    }

    public void setVoiceTimes(List<ScheduleEntity> entities) {
        synchronized (this.entities) {
            this.entities.addAll(entities);
        }
    }

    public void deleteSound(ScheduleEntity entity) {
        deleteSound(entity.getId());
    }

    public void deleteSound(Long id) {
        synchronized (entities) {
            if (isPut) {
                exec.shutdownNow();
                play();
                isPut = false;
            }
            entities.removeIf(entity1 -> Objects.equals(entity1.getId(), id));
        }
    }
}
