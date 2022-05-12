package org.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.attribute.ContextAttributes;
import org.example.attribute.RequestAttributes;
import org.example.security.Authentication;
import org.example.security.AuthenticationException;
import org.example.security.TokenAuthentication;
import org.example.service.UserService;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

public class BearerAuthenticationFilter extends HttpFilter {
    private UserService userService;

    @Override
    public void init() throws ServletException {

        final ApplicationContext context = (ApplicationContext) getServletContext()
                .getAttribute(ContextAttributes.CONTEXT_ATTR);
        userService = context.getBean(UserService.class);
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        final Authentication existing = (Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR);
        // early exit: если уже есть аутентификация и она не анонимная
        // значит уже кто-то до нас аутентифицировал пользователя
        if (existing != null && !existing.isAnonymous()) {
            chain.doFilter(req, res);
            return;
        }

        // проверяем, есть ли заголовок Authorization
        final String header = req.getHeader("Authorization");
        if (header == null) {
            chain.doFilter(req, res);
            return;
        }
        if (!header.startsWith("Bearer")) {
            chain.doFilter(req, res);
            return;
        }
        // Значит это точно Authorization: Bearer token
        try {
            final String token = header.substring("Bearer".length() + 1);

            final TokenAuthentication authentication = userService.authenticate(
                    token
            );
            req.setAttribute(
                    RequestAttributes.AUTH_ATTR,
                    authentication
            );
        } catch (AuthenticationException e) {
            res.sendError(401);
            return;
        }

        chain.doFilter(req, res);
    }
}
