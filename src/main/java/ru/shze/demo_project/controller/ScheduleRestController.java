package ru.shze.demo_project.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shze.demo_project.api.request.ScheduleRequest;
import ru.shze.demo_project.api.response.MessageResponse;
import ru.shze.demo_project.api.response.ScheduleResponse;
import ru.shze.demo_project.service.ScheduleServiceImp;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "http://localhost:4200")
public class ScheduleRestController {

    @Autowired
    private final ScheduleServiceImp scheduleService;

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(scheduleService.getAll());
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScheduleResponse> getScheduleById(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(scheduleService.getById(id));
    }


    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> createScheduleInSystem(@RequestBody ScheduleRequest request){
        System.out.println(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(scheduleService.createSchedule(request));
    }

    @DeleteMapping(value ="/delete/{id}")
    public ResponseEntity<MessageResponse> deleteScheduleFromSystem(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(scheduleService.deleteById(id));
    }

    @PutMapping(value ="/update/{id}")
    public ResponseEntity<MessageResponse> updateScheduleInSystem(@PathVariable Long id, @RequestBody ScheduleRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(scheduleService.updateSchedule(id,request));
    }
}
