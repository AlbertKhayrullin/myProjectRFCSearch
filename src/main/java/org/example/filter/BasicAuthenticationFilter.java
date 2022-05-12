package org.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.attribute.ContextAttributes;
import org.example.attribute.RequestAttributes;
import org.example.exception.BadCredentialsFormatException;
import org.example.security.Authentication;
import org.example.security.AuthenticationException;
import org.example.security.LoginAuthentication;
import org.example.service.UserService;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthenticationFilter extends HttpFilter {
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

        if (existing != null && !existing.isAnonymous()) {
            chain.doFilter(req, res);
            return;
        }

        final String header = req.getHeader("Authorization");
        if (header == null) {
            chain.doFilter(req, res);
            return;
        }
        if (!header.startsWith("Basic")) {
            chain.doFilter(req, res);
            return;
        }

        try {

            final String encodedCredentials = header.substring("Basic".length() + 1);

            final String decodedCredentials = new String(
                    Base64.getDecoder().decode(encodedCredentials),
                    StandardCharsets.UTF_8
            );

            final String[] parts = decodedCredentials.split(":");
            if (parts.length != 2) {
                throw new BadCredentialsFormatException();
            }

            final String login = parts[0];
            final String password = parts[1];

            final LoginAuthentication authentication = userService.authenticate(
                    login,
                    password
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
