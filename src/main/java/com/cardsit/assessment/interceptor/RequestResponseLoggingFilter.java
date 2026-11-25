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
 * Servlet filter that audits REQUEST and RESPONSE for every {@code /api/**} call.
 *
 * <p><b>Assessment requirement:</b> log request &amp; response to a dedicated file.
 * Output goes to logger name {@code API_AUDIT}, which is routed to
 * {@code logs/api-request-response.log} via {@code logback-spring.xml}.
 *
 * <p><b>Handover notes:</b>
 * <ul>
 *   <li>Uses caching wrappers so the body can be read after the chain completes
 *       without consuming the original streams.</li>
 *   <li>Must call {@code copyBodyToResponse()} or the client gets an empty body.</li>
 *   <li>Bodies longer than 2000 chars are truncated to keep log files manageable.</li>
 * </ul>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    /** Dedicated logger — do not use the class logger if you want file separation. */
    private static final Logger API_AUDIT = LoggerFactory.getLogger("API_AUDIT");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Skip non-API traffic (actuator, static, error pages, etc.)
        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Short id to correlate request + response lines in the audit log
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

            // Required: write cached response bytes back to the real servlet response
            wrappedResponse.copyBodyToResponse();
        }
    }

    private String getContent(byte[] content) {
        if (content == null || content.length == 0) {
            return "(empty)";
        }
        return new String(content, StandardCharsets.UTF_8);
    }

    /** Caps log payload size so large responses do not blow up disk usage. */
    private String truncate(String value) {
        if (value == null) {
            return "(empty)";
        }
        int max = 2000;
        return value.length() <= max ? value : value.substring(0, max) + "...[truncated]";
    }
}
