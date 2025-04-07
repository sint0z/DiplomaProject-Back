package ru.shze.demo_project.model;


import com.fasterxml.jackson.annotation.*;
import jakarta.annotation.Generated;
import lombok.Data;
import ru.shze.demo_project.api.request.ScheduleRequest;
import ru.shze.demo_project.api.response.ScheduleResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "template",
        "sounds",
})
@Generated("jsonschema2pojo")
public class JSONTemplateModel {
    @JsonProperty("template")
    List<ScheduleRequest> template;
    @JsonProperty("sounds")
    Map<Long, String> sounds;
}

