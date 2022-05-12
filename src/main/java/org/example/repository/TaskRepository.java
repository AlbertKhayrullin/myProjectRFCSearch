package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.entity.TaskEntity;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.example.service.SearchService.DONE;
import static org.example.service.SearchService.PROCESSED;

@RequiredArgsConstructor
@Repository
public class TaskRepository {
    public static final String NOT_DONE = "notDone";
    private final Jdbi jdbi;

    public TaskEntity save(TaskEntity task) {

        TaskEntity registeredTask = jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                """
                                        INSERT INTO tasks (user_id, phrase, status) VALUES (:user_id, :phrase, :status)
                                        RETURNING id, user_id, phrase, status
                                        """
                        )
                        .bind("id", task.getId())
                        .bind("user_id", task.getUserId())
                        .bind("phrase", task.getPhrase())
                        .bind("status", NOT_DONE)
                        .mapToBean(TaskEntity.class)
                        .one()
        );
        return registeredTask;
    }

    public Optional<TaskEntity> getOptionalBroken() {
        Optional<TaskEntity> optionalBroken = jdbi.withHandle(handle -> handle.createQuery(
                        // language=PostgreSQL
                        """
                                SELECT id, user_id, phrase, status
                                FROM tasks
                                WHERE status = :status
                                """)
                .bind("status", PROCESSED)
                .mapToBean(TaskEntity.class)
                .stream().findFirst()
        );
        return optionalBroken;
    }

    public Optional<TaskEntity> getOptional() {
        Optional<TaskEntity> optional = jdbi.withHandle(handle -> handle.createQuery(
                        // language=PostgreSQL
                        """
                                SELECT id, user_id, phrase, status
                                FROM tasks
                                WHERE status = :status
                                """)
                .bind("status", NOT_DONE)
                .mapToBean(TaskEntity.class)
                .stream().findFirst()
        );
        return optional;
    }

    public void switchStatus(TaskEntity task, String status) {
        jdbi.withHandle(handle -> handle.createUpdate(
                        // language=PostgreSQL
                        """
                                UPDATE tasks SET status = :status WHERE id = :id
                                RETURNING id, user_id, phrase, status
                                """)
                .bind("id", task.getId())
                .bind("status", status)
                .execute()
        );
    }

    public List<TaskEntity> findMyTasks(final String login) {
        return jdbi.withHandle(handle -> handle.createQuery(
                        // language=PostgreSQL
                        """
                                SELECT t.id, t.user_id, t.phrase, t.status 
                                FROM users u 
                                JOIN tasks t 
                                ON u.id = t.user_id 
                                WHERE u.login = :login 
                                AND t.status = :status
                                """)
                .bind("login", login)
                .bind("status", DONE)
                .mapToBean(TaskEntity.class)
                .list()
        );
    }
}
