package ru.shze.demo_project.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.shze.demo_project.api.response.FileDTO;
import ru.shze.demo_project.api.response.MessageResponse;
import ru.shze.demo_project.api.request.SoundRequest;
import ru.shze.demo_project.api.response.SoundResponse;
import ru.shze.demo_project.service.SoundServiceImp;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/sound")
@CrossOrigin(origins = "http://localhost:4200")
public class SoundRestController {
    @Autowired
    private final SoundServiceImp soundServiceImp;

    @GetMapping(value = "/download/{id}",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id){
        Resource file = soundServiceImp.getSoundFile(id);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }


    @GetMapping(value = "/get/{id}" , produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SoundResponse> getSoundById(@PathVariable int id){
        SoundResponse request = soundServiceImp.getById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(request);
    }


    @PostMapping(value = "/create")
    public ResponseEntity<?> addSoundInSystem(@RequestPart(value = "properties") SoundRequest soundRequest,
                                              @RequestPart(value = "file") MultipartFile file) throws IOException {
        MessageResponse messageResponse =  soundServiceImp.addSound(soundRequest,file);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<MessageResponse> deleteSoundFromSystem(@PathVariable Long id){
        MessageResponse deleteInfo= soundServiceImp.deleteSound(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(deleteInfo);
    }

    @GetMapping(value = "/get", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SoundResponse>> getAllSound() {
           List<SoundResponse> listSound =  soundServiceImp.getAllSound();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(listSound);

    }

    @PutMapping(value = "/update/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> updateSoundInSystem(@PathVariable Long id,
                                                      @RequestPart(value = "properties") SoundRequest soundRequest,
                                                      @RequestPart(value = "file",required = false) MultipartFile file) throws IOException {
        MessageResponse updateInfo = soundServiceImp.updateSound(id ,soundRequest,file);
        return ResponseEntity.status(HttpStatus.OK).body(updateInfo);
    }
}
