package ru.shze.demo_project.service.service_interface;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.shze.demo_project.api.response.MessageResponse;

import java.io.IOException;
import java.util.List;

public interface SettingsService {

  Resource sendingLayoutData() throws IOException;
  //MessageResponse gettingLayout(JSONTemplateModel modelList);

  MessageResponse gettingLayout(MultipartFile file) throws IOException;
}
