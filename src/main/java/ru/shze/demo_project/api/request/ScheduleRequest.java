package ru.shze.demo_project.api.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Generated;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "hourStart",
        "minuteStart",
        "days",
        "soundId",
        "sound"
})
@Generated("jsonschema2pojo")
public class ScheduleRequest {
    @JsonProperty("name")
    private String name;
    @JsonProperty("hourStart")
    private int hourStart;
    @JsonProperty("minuteStart")
    private int minuteStart;
    @JsonProperty("days")
    private List<Long> days;
    @JsonProperty("soundId")
    private Long soundId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

}
