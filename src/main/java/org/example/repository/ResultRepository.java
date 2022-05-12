package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.entity.ResultEntity;
import org.example.entity.TaskEntity;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;

import static org.example.service.SearchService.DONE;

@RequiredArgsConstructor
@Repository
public class ResultRepository {
    private final Jdbi jdbi;

    public void saveResult(TaskEntity task, Path path, int numberLine, String line) {

        jdbi.withHandle(handle -> handle.createUpdate(
                        // language=PostgreSQL
                        """
                                INSERT INTO results (task_id, file, number_line, line)
                                VALUES (:task_id, :file, :number_line, :line)
                                RETURNING id, task_id, file, number_line, line
                                """)
                .bind("task_id", task.getId())
                .bind("file", path.getFileName().toString())
                .bind("number_line", numberLine)
                .bind("line", line)
                .execute());
    }

    public void deleteResult(TaskEntity taskBroken) {
        jdbi.withHandle(handle -> handle.createUpdate(
                // language=PostgreSQL
                """
                        DELETE FROM results 
                        WHERE task_id = :task_id
                        """)
                .bind("task_id", taskBroken.getId()).execute());

    }

    public List<ResultEntity> getResult(final String login) {
        return jdbi.withHandle(handle -> handle.createQuery(
                        // language=PostgreSQL
                        """
                                SELECT r.id, task_id, file, number_line, line 
                                FROM results r 
                                JOIN tasks t on t.id = r.task_id
                                JOIN users u on u.id = t.user_id
                                WHERE u.login = :login AND status = :status                        
                                """)
                .bind("login", login)
                .bind("status", DONE)
                .mapToBean(ResultEntity.class)
                .list());
    }


}
