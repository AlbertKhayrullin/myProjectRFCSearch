package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.entity.TokenEntity;
import org.example.entity.UserEntity;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final Jdbi jdbi;
    public UserEntity save(UserEntity entity) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                """
                                        INSERT INTO users(login, password) 
                                        VALUES (:login, :password)
                                        RETURNING id, login, password
                                        """
                        )
                        .bind("login", entity.getLogin())
                        .bind("password", entity.getPassword())
                        .mapToBean(UserEntity.class)
                        .one()
        );
    }
    public TokenEntity save(TokenEntity entity) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                """
                                        INSERT INTO tokens (value, user_id) VALUES (:value, :user_id)
                                        RETURNING value, user_id
                                        """
                        )
                        .bind("value", entity.getValue())
                        .bind("user_id", entity.getUserId())
                        .mapToBean(TokenEntity.class)
                        .one()
        );
    }
    public List<UserEntity> getAll() {
        return jdbi.withHandle(handle -> handle.createQuery(
                        // language=PostgreSQL
                        "SELECT id, login FROM users ORDER BY id"
                )
                .mapToBean(UserEntity.class)
                .list());
    }

    public Optional<UserEntity> findByLogin(String login) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                "SELECT id, login, password FROM users WHERE login = :login"
                        )
                        .bind("login", login)
                        .mapToBean(UserEntity.class)
                        .findOne()
        );
    }
    public Optional<UserEntity> findByToken(String token) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                """
                                        SELECT u.id, u.login, NULL
                                        FROM tokens t
                                        JOIN users u ON t.user_id = u.id
                                        WHERE t.value = :token
                                        """
                        )
                        .bind("token", token)
                        .mapToBean(UserEntity.class)
                        .findOne()
        );
    }
}
