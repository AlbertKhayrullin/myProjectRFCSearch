package org.example.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.attribute.RequestAttributes;
import org.example.dto.TaskRegisterRequestDTO;
import org.example.dto.TaskRegisterResponseDTO;
import org.example.entity.TaskEntity;
import org.example.mime.ContentTypes;
import org.example.security.Authentication;
import org.example.service.TaskService;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class TaskController {
    private final TaskService service;
    private final Gson gson;
    public void register(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final Authentication auth = (Authentication) request.getAttribute(
                RequestAttributes.AUTH_ATTR
        );

        final TaskRegisterRequestDTO requestData = gson.fromJson(
                request.getReader(),
                TaskRegisterRequestDTO.class
        );


        final TaskRegisterResponseDTO responseData = service.register(requestData, auth);

        response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }

    public void findMyTasks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final Authentication auth = (Authentication) request.getAttribute(
                RequestAttributes.AUTH_ATTR
        );
        final List<TaskEntity> responseData = service.findDoneTasks(auth);

        response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }
}
