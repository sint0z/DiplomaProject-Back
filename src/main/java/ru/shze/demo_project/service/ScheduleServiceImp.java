package ru.shze.demo_project.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import ru.shze.demo_project.api.request.ScheduleRequest;
import ru.shze.demo_project.api.response.ScheduleResponse;
import ru.shze.demo_project.api.response.MessageResponse;
import ru.shze.demo_project.model.ScheduleDayEntity;
import ru.shze.demo_project.model.ScheduleEntity;
import ru.shze.demo_project.repository.ScheduleDayRepository;
import ru.shze.demo_project.repository.ScheduleRepository;
import ru.shze.demo_project.repository.SoundRepository;
import ru.shze.demo_project.service.service_interface.ScheduleService;
import ru.shze.demo_project.utils.VoiceManager;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@CrossOrigin
public class ScheduleServiceImp implements ScheduleService {

    private final ScheduleDayRepository dayRepository;
    private final ScheduleRepository scheduleRepository;
    private final SoundRepository soundRepository;
    private String currentDate;

    private final VoiceManager manager;
    @Autowired
    public ScheduleServiceImp(ScheduleDayRepository dayRepository, ScheduleRepository scheduleRepository, SoundRepository soundRepository, VoiceManager manager) {
        this.dayRepository = dayRepository;
        this.scheduleRepository = scheduleRepository;
        this.soundRepository = soundRepository;
        this.manager = manager;
        this.currentDate = new Date().toString();
    }
    @Override
    public void deleteAll() {
        scheduleRepository.deleteAll();
    }
    @Override
    public List<ScheduleEntity> getAllEntity() {
        return scheduleRepository.findAll();
    }

    @Override
    public List<ScheduleResponse> getAll() {

        return scheduleRepository.findAll()
                .stream()
                .map(s -> new ScheduleResponse(s.getId(), s.getName(), s.getHourStart(), s.getMinuteStart(),
                        s.getDays()
                                .stream()
                                .map(ScheduleDayEntity::getDayIndex)
                                .collect(Collectors.toList()), s.getSound() == null? -1L:  s.getSound().getId(), s.getSound() != null? s.getSound().getNameSound(): "Системный звук"))
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleResponse getById(Long id) {
        return scheduleRepository.findById(id)
                .map(s -> new ScheduleResponse(s.getId().describeConstable().orElse(-1L), s.getName(), s.getHourStart(), s.getMinuteStart(),
                        s.getDays()
                                .stream()
                                .map(ScheduleDayEntity::getDayIndex)
                                .collect(Collectors.toList()),s.getSound() == null? -1L:  s.getSound().getId(), s.getSound() != null? s.getSound().getNameSound(): "Пустое название"))
                .orElseThrow();
    }
    @Transactional
    @Override
    public MessageResponse updateSchedule(Long id, ScheduleRequest request) {
       if(id == null){
           return new MessageResponse("Ошибка, ресурс с id: " + id + "не найте",new Date().toString());
       }

       if(scheduleRepository.findById(id).isPresent()) {
           dayRepository.deleteAllByScheduleId(id);
       }
           ScheduleEntity schedule = new ScheduleEntity();
           schedule.setId(id);
           addEditSchedule(request, schedule);

        return new MessageResponse("Расписание успешно обновлено",currentDate);
    }

    private void addEditSchedule(ScheduleRequest request, ScheduleEntity schedule) {
        schedule.setName(request.getName());
        schedule.setHourStart(request.getHourStart());
        schedule.setMinuteStart(request.getMinuteStart());


        List<ScheduleDayEntity> dayList = request.getDays()
                .stream()
                .map(d -> new ScheduleDayEntity(null,getIndexDay(d.intValue()),
                        d.intValue(),schedule))
                .toList();

        schedule.setDays(dayList);

        schedule.setSound(soundRepository.findById(request.getSoundId()).orElse(null));

        manager.addVoiceTime(schedule);

        scheduleRepository.save(schedule);
    }

    @Override
    public MessageResponse deleteById(Long id) {
        String name = scheduleRepository.findById(id)
                .orElseThrow()
                .getName();
        manager.deleteSound(id);
        scheduleRepository.deleteById(id);
        return new MessageResponse("Расписание: " + name + " удалено из системы", currentDate);
    }

    @Override
    public MessageResponse createSchedule(ScheduleRequest request) {
        ScheduleEntity schedule = new ScheduleEntity();
        addEditSchedule(request, schedule);

        return new MessageResponse("Расписание: " + request.getName() + " добавлено в систему",
                currentDate);
    }

    private String getIndexDay(Integer index) {
        return switch (index) {
            case 1 -> "ПН";
            case 2 -> "Вт";
            case 3 -> "СР";
            case 4 -> "ЧТ";
            case 5 -> "ПТ";
            case 6 -> "СБ";
            default -> "ВС";
        };
    }
}
