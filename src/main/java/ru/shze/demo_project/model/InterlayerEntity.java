package ru.shze.demo_project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

enum PlaybackType {
    SYNCHRONOUS,
    ASYNCHRONOUS
}

@Data
@NoArgsConstructor
@Entity
@Table(name = "sound_playlist")
public class InterlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Basic
    @Column(name = "orders")
    private Integer order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private SoundEntity soundEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private SoundPlayListEntity playList;

}
