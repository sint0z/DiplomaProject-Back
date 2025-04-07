package ru.shze.demo_project.service.service_interface;

import ru.shze.demo_project.api.request.ScheduleRequest;
import ru.shze.demo_project.api.response.ScheduleResponse;
import ru.shze.demo_project.api.response.MessageResponse;
import ru.shze.demo_project.model.ScheduleEntity;

import java.util.List;

public interface ScheduleService {
    void deleteAll();
    List<ScheduleEntity> getAllEntity();
    List<ScheduleResponse> getAll();
    ScheduleResponse getById(Long id);
    MessageResponse updateSchedule(Long id, ScheduleRequest request);
    MessageResponse deleteById(Long id);
    MessageResponse createSchedule(ScheduleRequest request);
}
