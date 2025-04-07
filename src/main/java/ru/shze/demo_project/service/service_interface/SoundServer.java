package ru.shze.demo_project.service.service_interface;

import org.springframework.web.multipart.MultipartFile;
import ru.shze.demo_project.api.response.MessageResponse;
import ru.shze.demo_project.api.request.SoundRequest;
import ru.shze.demo_project.api.response.SoundResponse;

import java.io.IOException;
import java.util.List;

public interface SoundServer {
    SoundResponse getById(Integer id);
    List<SoundResponse> getAllSound();
    MessageResponse addSound(SoundRequest soundRequest,  MultipartFile file) throws IOException;
    MessageResponse deleteSound(Long id);
    MessageResponse updateSound(Long id,SoundRequest soundRequest,MultipartFile file) throws IOException;

}
