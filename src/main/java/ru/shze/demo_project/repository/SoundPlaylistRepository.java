package ru.shze.demo_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shze.demo_project.model.InterlayerEntity;

@Repository
public interface SoundPlaylistRepository extends JpaRepository<InterlayerEntity,Long> {

}
