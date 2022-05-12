package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserGetAllResponseDTO;
import org.example.dto.UserRegisterRequestDTO;
import org.example.dto.UserRegisterResponseDTO;
import org.example.entity.TokenEntity;
import org.example.entity.UserEntity;
import org.example.exception.UsernameAlreadyRegisteredException;
import org.example.repository.UserRepository;
import org.example.security.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    public List<UserGetAllResponseDTO> getAll() {
        return repository.getAll()
                .stream()
                // mapstruct
                .map(o -> new UserGetAllResponseDTO(o.getId(), o.getLogin()))
                .collect(Collectors.toList())
                ;
    }

    public UserRegisterResponseDTO register(UserRegisterRequestDTO requestData) {
        final String hashedPassword = passwordEncoder.encode(requestData.getPassword());
        // TODO: transaction
        repository
                .findByLogin(requestData.getLogin())
                .ifPresent(o -> {
                    throw new UsernameAlreadyRegisteredException(o.getLogin());
                });
        final UserEntity saved = repository.save(new UserEntity(
                0L,
                requestData.getLogin(),
                hashedPassword
        ));

        // генерация токена
        byte[] buffer = new byte[128];
        random.nextBytes(buffer);
        final String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(buffer);
        repository.save(new TokenEntity(saved.getId(), token));

        return new UserRegisterResponseDTO(
                saved.getId(),
                saved.getLogin(),
                token
        );
    }

    public LoginAuthentication authenticate(String login, String password) {

        final UserEntity entity = repository.findByLogin(login)
                .orElseThrow(NotFoundException::new);

        if (!passwordEncoder.matches(password, entity.getPassword())) {
            throw new CredentialsNotMatchesException();
        }

        return new LoginAuthentication(entity.getId(), login);
    }

    public TokenAuthentication authenticate(String token) {
        return repository.findByToken(token)
                .map(o -> new TokenAuthentication(o.getId(), o.getLogin()))
                .orElseThrow(TokenNotFoundException::new)
                ;
    }
}
