package ru.shze.demo_project.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.shze.demo_project.model.SoundEntity;

import java.util.List;

@Repository
public interface SoundRepository extends JpaRepository<SoundEntity, Long>{
 boolean existsByNameSound(String nameSound);
}
