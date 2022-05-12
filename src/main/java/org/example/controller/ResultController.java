package org.example.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.attribute.RequestAttributes;
import org.example.dto.ResultRegisterRequestDTO;
import org.example.dto.ResultRegisterResponseDTO;
import org.example.entity.ResultEntity;
import org.example.entity.TaskEntity;
import org.example.mime.ContentTypes;

import org.example.security.Authentication;
import org.example.service.ResultService;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class ResultController {
    private final ResultService service;
    private final Gson gson;
    public void register(HttpServletRequest request, HttpServletResponse response) throws IOException {

                final ResultRegisterResponseDTO responseData = service.save();
                response.setContentType(ContentTypes.APPLICATION_JSON);
                response.getWriter().write(gson.toJson(responseData));
    }

    public void getResult(HttpServletRequest request, HttpServletResponse response) throws IOException {

            final Authentication auth = (Authentication) request.getAttribute(
                    RequestAttributes.AUTH_ATTR
            );
            final String login = request.getParameter("login");
            final List<ResultEntity> responseData = service.getResult(auth);

            response.setContentType(ContentTypes.APPLICATION_JSON);
            response.getWriter().write(gson.toJson(responseData));

    }
}




