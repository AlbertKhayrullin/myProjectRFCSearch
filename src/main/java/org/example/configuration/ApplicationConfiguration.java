package org.example.configuration;

import com.google.gson.Gson;
//import org.example.controller.ResultController;
import org.example.controller.ResultController;
import org.example.controller.TaskController;
import org.example.controller.UserController;
import org.example.handler.Handler;
import org.jdbi.v3.core.Jdbi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public DataSource dataSource() throws NamingException {
        final InitialContext ctx = new InitialContext();
        return (DataSource) ctx.lookup("java:/comp/env/jdbc/db");
    }
    @Bean
    public Jdbi jdbi(final DataSource dataSource) {
        return Jdbi.create(dataSource);
    }
    @Bean
    public Gson gson() {
        return new Gson();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder();
    }
    @Bean
    public Map<String, Handler> routes(
            final UserController userController,
            final TaskController taskController,
            final ResultController resultController

    ) {
        final Map<String, Handler> routes = new HashMap<>();
        routes.put("/users.getAll", userController::getAll);
        routes.put("/users.register", userController::register);
        routes.put("/tasks.register", taskController::register);
        routes.put("/tasks.findMyTasks", taskController::findMyTasks);
        routes.put("/results.register", resultController::register);
        routes.put("/results.getMyResult", resultController::getResult);
        return Collections.unmodifiableMap(routes);
    }

}
