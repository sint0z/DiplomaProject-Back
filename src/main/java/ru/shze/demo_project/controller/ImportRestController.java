package ru.shze.demo_project.controller;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.shze.demo_project.api.response.MessageResponse;
import ru.shze.demo_project.service.SettingsServiceImp;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "http://localhost:4200")
public class ImportRestController {
    SettingsServiceImp service;

    @Autowired
    public ImportRestController(SettingsServiceImp service) {
        this.service = service;
    }

    @GetMapping(value = "export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> exportLayout() throws IOException {
        Resource file = service.sendingLayoutData();
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PostMapping(value = "import")
    public ResponseEntity<?> importLayout(@RequestPart(value = "template") MultipartFile JSONFile) throws IOException {
        service.delete();
        service.gettingLayout(JSONFile);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponse("Данные обновлены", new Date().toString()));
    }

    @PostMapping(value = "send")
    public ResponseEntity<?> alarmSetting(@RequestPart(value = "file", required = false) MultipartFile file) throws IOException, UnsupportedAudioFileException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.service.alarmManager(file));
    }

    @DeleteMapping(value = "delete")
    public ResponseEntity<?> deleteAlarm() {
        MessageResponse message = service.deleteAlarm();
        return entity(message);
    }

    @GetMapping(value = "check")
    public ResponseEntity<Boolean> checkAlarm(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.isCheckAlarm());
    }

    @GetMapping(value = "alarm")
    public ResponseEntity<?> alarm() throws Exception {
        MessageResponse message = service.alarm();
        return entity(message);
    }

    private ResponseEntity<?> entity (MessageResponse message){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Objects.requireNonNullElseGet(message, () -> new MessageResponse("Нет звука", new Date().toString())));
    }
}
