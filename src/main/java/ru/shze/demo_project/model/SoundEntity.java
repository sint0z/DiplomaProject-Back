package ru.shze.demo_project.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sounds")
public class SoundEntity {
    @Id
    private Long id;

    @Column(name = "sound_name")
    private String nameSound;

    @Column(name = "creation_date")
    private LocalDate dateCreation;

    @Column(name = "update_date")
    private LocalDate dateUpdate;

    @Column(name = "path")
    private String localPath;

    @OneToMany(mappedBy = "sound")
    @JsonBackReference
    private List<ScheduleEntity> schedule;

    @OneToMany(mappedBy = "soundEntity",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<InterlayerEntity> interlayerEntity;


}
