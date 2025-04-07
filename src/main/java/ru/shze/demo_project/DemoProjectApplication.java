package ru.shze.demo_project;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;
import ru.shze.demo_project.service.FileStorageManagerImp;
import ru.shze.demo_project.service.ScheduleServiceImp;
import ru.shze.demo_project.utils.SoundPlayer;
import ru.shze.demo_project.utils.VoiceManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.Timer;


@SpringBootApplication
public class DemoProjectApplication implements CommandLineRunner {

    @Resource
    private FileStorageManagerImp fileStorageServiceImp;
    @Autowired
    private ScheduleServiceImp service;
    @Autowired
    private VoiceManager manager;

    public static void main(String[] args) {
        SpringApplication.run(DemoProjectApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        fileStorageServiceImp.init();

        var a = this.manager;
        a.setVoiceTimes(service.getAllEntity());
        a.play();
    }
}