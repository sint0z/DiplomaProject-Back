package ru.shze.demo_project.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@AllArgsConstructor
@Data
public class ScheduleResponse {
    Long id;
    String name;
    int hourStart;
    int minuteStart;
    List<Integer> days;
    Long soundId;
    String sound;
}
