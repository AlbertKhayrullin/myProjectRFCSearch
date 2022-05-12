package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.TaskRegisterRequestDTO;
import org.example.dto.TaskRegisterResponseDTO;
import org.example.entity.TaskEntity;
import org.example.repository.TaskRepository;
import org.example.security.Authentication;
import org.example.security.ForbiddenException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository repository;

    public TaskRegisterResponseDTO register(TaskRegisterRequestDTO requestData, final Authentication auth) throws IOException {

        if (auth.isAnonymous()) {
            throw new ForbiddenException();
        }

        final TaskEntity saved = repository.save(new TaskEntity(
                requestData.getId(),
                auth.getId(),
                requestData.getPhrase(),
                requestData.getStatus()
        ));
        return new TaskRegisterResponseDTO(
                saved.getId(),
                saved.getUserId(),
                saved.getPhrase(),
                saved.getStatus()
        );
    }

    public List<TaskEntity> findDoneTasks(final Authentication auth) {
        if (auth.isAnonymous()) {
            throw new ForbiddenException();
        }
        return repository.findMyTasks(auth.getName()).stream().map(o -> new TaskEntity(
                        o.getId(),
                        o.getUserId(),
                        o.getPhrase(),
                        o.getStatus()
                )).collect(Collectors.toList());
    }
}
