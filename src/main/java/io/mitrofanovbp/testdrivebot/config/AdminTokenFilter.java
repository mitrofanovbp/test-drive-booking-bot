package io.mitrofanovbp.testdrivebot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mitrofanovbp.testdrivebot.dto.ApiError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Simple header-based admin auth filter.
 * Requires header X-Admin-Token to match configured app.admin-token.
 */
public class AdminTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AdminTokenFilter.class);

    private final String expectedToken;
    private final ObjectMapper objectMapper;

    public AdminTokenFilter(String expectedToken, ObjectMapper objectMapper) {
        this.expectedToken = expectedToken;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/admin/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = request.getHeader("X-Admin-Token");
        if (token == null || expectedToken == null || !expectedToken.equals(token)) {
            if (log.isDebugEnabled()) {
                log.debug("Admin token missing or invalid for path {}", request.getRequestURI());
            }
            ApiError err = new ApiError();
            err.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
            err.setStatus(HttpStatus.UNAUTHORIZED.value());
            err.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
            err.setMessage("Missing or invalid X-Admin-Token");
            err.setPath(request.getRequestURI());
            byte[] payload = objectMapper.writeValueAsString(err).getBytes(StandardCharsets.UTF_8);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getOutputStream().write(payload);
            return;
        }
        chain.doFilter(request, response);
    }
}
