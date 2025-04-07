package ru.shze.demo_project.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
@Table(name = "play_list")
public class SoundPlayListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_play_list")
    private int namePlaylist;

    @Column(name = "duration_play_list")
    private String durationPlayList;

    @Column(name = "type_async")
    private PlaybackType type;

   @OneToMany(mappedBy = "playList",fetch = FetchType.EAGER)
   @JsonIgnore
   private List<InterlayerEntity> interlayerEntity;

}


