package ru.shze.demo_project.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Entity
@NoArgsConstructor
@Data
@Table( name = "schedule_day")
public class ScheduleDayEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_name")
    private String nameDay;

    @Column(name = "day_index")
    private Integer dayIndex;

    @ManyToOne(fetch = FetchType.EAGER  )
    @JsonBackReference
    private ScheduleEntity schedule;
}
