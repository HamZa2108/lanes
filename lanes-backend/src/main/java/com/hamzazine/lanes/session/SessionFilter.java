package com.hamzazine.lanes.session;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Session-Id";

    private final SessionContext sessionContext;
    private final SessionService sessionService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String headerValue = request.getHeader(HEADER);
        UUID sessionId;
        try {
            sessionId = UUID.fromString(headerValue);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Missing or invalid X-Session-Id header\"}");
            return;
        }

        sessionContext.setSessionId(sessionId);
        sessionService.touchSession(sessionId);

        chain.doFilter(request, response);
    }
}