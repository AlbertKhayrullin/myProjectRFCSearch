package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.attribute.ContextAttributes;
import org.example.handler.Handler;
import org.jdbi.v3.core.Jdbi;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Map;

public class Servlet extends HttpServlet {

    private Jdbi jdbi;
    private Map<String, Handler> routes;

    @Override
    public void init() {
        jdbi = (Jdbi) getServletContext().getAttribute("jdbi");
        final ApplicationContext context = (ApplicationContext) getServletContext()
                .getAttribute(ContextAttributes.CONTEXT_ATTR);
        routes = (Map<String, Handler>) context.getBean("routes");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String uri = req.getRequestURI();
        final Handler handler = routes.get(uri);
        if (handler != null) {
            try {
                handler.handle(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
