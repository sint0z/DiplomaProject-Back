package ru.shze.demo_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shze.demo_project.model.ScheduleDayEntity;
import ru.shze.demo_project.model.ScheduleEntity;

import java.util.List;

@Repository
public interface ScheduleDayRepository extends JpaRepository<ScheduleDayEntity, Long>{
    void deleteAllByScheduleId(Long schedule_id);
}
