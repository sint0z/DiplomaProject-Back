package ru.shze.demo_project.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shze.demo_project.api.JwtToken;
import ru.shze.demo_project.api.request.AuthorizationData;
import ru.shze.demo_project.api.response.MessageResponse;
import ru.shze.demo_project.utils.JwtTokenUtils;

import java.util.Date;
import java.util.Objects;


@RestController
@RequestMapping("/api/security")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {
    @Autowired
    private JwtTokenUtils utils;

    @Value("${prop.login}")
    private String LOGIN;

    @Value("${prop.password}")
    private String PASSWORD;


    @PostMapping("/login")
    public ResponseEntity<?> isLogin(@RequestBody AuthorizationData data){
        if(Objects.equals(data.getPassword(), PASSWORD) && Objects.equals(data.getLogin(), LOGIN)) {
            final String token = utils.generateToken(data);
            return ResponseEntity
                    .ok()
                    .body(new JwtToken(token));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Отказано в доступе", new Date().toString()));
    }

}
