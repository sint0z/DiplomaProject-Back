package ru.shze.demo_project.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthorizationData {
    private String login;
    private String password;
}
