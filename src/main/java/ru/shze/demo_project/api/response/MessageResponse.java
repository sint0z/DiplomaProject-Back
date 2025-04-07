package ru.shze.demo_project.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.Date;

@AllArgsConstructor
@Data
public class MessageResponse {
    private String message;
    private String dateMessage;
}

