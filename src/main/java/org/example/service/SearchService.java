package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.TaskEntity;
import org.example.repository.ResultRepository;
import org.example.repository.TaskRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.example.repository.TaskRepository.NOT_DONE;

@RequiredArgsConstructor
@Repository
public class SearchService implements InitializingBean, DisposableBean {

    public static final String DONE = "done";
    public static final String PROCESSED = "processed";
    private final Jdbi jdbi;
    private Thread worker;
    private final ResultRepository resultRepository;
    private final TaskRepository taskRepository;

    private final ExecutorService fixedTreadPool = Executors.newFixedThreadPool(64);

    @Override
    public void afterPropertiesSet() {
        worker = new Thread(() -> {
            try {
                searchAndSaveProcess();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
                return;
            }
        });
        worker.start();
    }

    @Override
    public void destroy() throws Exception {
        if (worker != null && worker.isAlive()) {
            worker.interrupt();
        }
    }

    public void searchAndSaveProcess() throws InterruptedException, IOException {

        final List<Path> paths = Files.walk(Path.of("RFCs8501-latest"))
                .map(Path::toAbsolutePath)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());


        Optional<TaskEntity> optionalBroken = taskRepository.getOptionalBroken();

        while (!optionalBroken.isEmpty()) {
            TaskEntity taskBroken = optionalBroken.get();
            resultRepository.deleteResult(taskBroken);
            taskRepository.switchStatus(taskBroken, NOT_DONE);

            optionalBroken = taskRepository.getOptionalBroken();
        }

        while (!Thread.currentThread().isInterrupted()) {

            Optional<TaskEntity> optional = taskRepository.getOptional();

            if (optional.isEmpty()) {
                Thread.sleep(1000);
                continue;
            }

            TaskEntity task = optional.get();
            taskRepository.switchStatus(task, PROCESSED);

            fixedTreadPool.execute(() -> {

                String phrase = task.getPhrase().toLowerCase();

                for (Path path : paths) {
                    int numberLine = 0;
                    try {
                        for (String line : Files.readAllLines(path)) {
                            numberLine++;
                            if (!line.toLowerCase().contains(phrase)) {
                                continue;
                            }
                            // сохранение найденных строк в базе данных

                            resultRepository.saveResult(task, path, numberLine, line);
                        }
                    } catch (IOException e) {

                        throw new RuntimeException(e);
                    }
                }
                taskRepository.switchStatus(task, DONE);
            });
        }
    }
}
