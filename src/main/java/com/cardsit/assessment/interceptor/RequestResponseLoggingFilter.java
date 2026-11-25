package com.cardsit.assessment.interceptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Logs REQUEST and RESPONSE for every API call into the dedicated api-request-response.log file.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger API_AUDIT = LoggerFactory.getLogger("API_AUDIT");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long durationMs = System.currentTimeMillis() - start;

            String requestBody = getContent(wrappedRequest.getContentAsByteArray());
            String responseBody = getContent(wrappedResponse.getContentAsByteArray());

            API_AUDIT.info(
                    """
                    ========== API AUDIT [{}] ==========
                    REQUEST:
                      Method : {}
                      URI    : {}
                      Query  : {}
                      Body   : {}
                    RESPONSE:
                      Status : {}
                      Body   : {}
                      Time   : {} ms
                    =====================================
                    """,
                    correlationId,
                    wrappedRequest.getMethod(),
                    wrappedRequest.getRequestURI(),
                    wrappedRequest.getQueryString() != null ? wrappedRequest.getQueryString() : "",
                    truncate(requestBody),
                    wrappedResponse.getStatus(),
                    truncate(responseBody),
                    durationMs
            );

            wrappedResponse.copyBodyToResponse();
        }
    }

    private String getContent(byte[] content) {
        if (content == null || content.length == 0) {
            return "(empty)";
        }
        return new String(content, StandardCharsets.UTF_8);
    }

    private String truncate(String value) {
        if (value == null) {
            return "(empty)";
        }
        int max = 2000;
        return value.length() <= max ? value : value.substring(0, max) + "...[truncated]";
    }
}
