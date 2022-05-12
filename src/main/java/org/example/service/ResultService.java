package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ResultRegisterResponseDTO;
import org.example.entity.ResultEntity;
import org.example.repository.ResultRepository;
import org.example.security.Authentication;
import org.example.security.ForbiddenException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ResultService {
    private final ResultRepository repository;

    public ResultRegisterResponseDTO save() throws IOException {
        throw new UnsupportedEncodingException();
    }

    public List<ResultEntity> getResult(Authentication auth) {
        if (auth.isAnonymous()) {
            throw new ForbiddenException();
        }
        return repository.getResult(auth.getName()).stream().map(o -> new ResultEntity(
                o.getId(),
                o.getTaskId(),
                o.getFile(),
                o.getNumberLine(),
                o.getLine()
        )).collect(Collectors.toList());
    }
}


