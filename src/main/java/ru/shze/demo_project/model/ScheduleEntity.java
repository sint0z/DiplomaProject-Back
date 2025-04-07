package ru.shze.demo_project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "schedules")
public class ScheduleEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Basic
    @Column(name = "name_schedule")
    private String name;

    @Basic
    @Column(name = "hour_start")
    private Integer hourStart;

    @Column(name = "minute_start")
    private Integer minuteStart;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JsonManagedReference
    private List<ScheduleDayEntity> days = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    private SoundEntity sound;

    public static int compare (ScheduleEntity p1, ScheduleEntity p2){
        LocalTime time1 = LocalTime.of(p1.getHourStart(), p1.getMinuteStart());
        LocalTime time2 = LocalTime.of(p2.getHourStart(), p2.getMinuteStart());
          return  time1.compareTo(time2);
      }

}
