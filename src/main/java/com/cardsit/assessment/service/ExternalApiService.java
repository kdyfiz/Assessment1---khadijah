package com.cardsit.assessment.service;

import com.cardsit.assessment.dto.response.CardResponse;
import com.cardsit.assessment.dto.response.ExternalUserResponse;
import com.cardsit.assessment.exception.BusinessException;
import com.cardsit.assessment.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles nested outbound HTTP calls to a 3rd-party API.
 *
 * <p><b>Assessment requirement:</b> demonstrate Client → Our API → External API.
 * Uses JSONPlaceholder ({@code https://jsonplaceholder.typicode.com}) as a free stub.
 *
 * <p>Base URL is configurable via {@code external.api.base-url} in {@code application.yml}.
 * HTTP client timeouts are set in {@code RestTemplateConfig}.
 */
@Service
public class ExternalApiService {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiService.class);

    private final RestTemplate restTemplate;
    private final CardService cardService;

    /** Injected from application.yml — change there if you swap the 3rd-party host. */
    @Value("${external.api.base-url}")
    private String externalApiBaseUrl;

    public ExternalApiService(RestTemplate restTemplate, CardService cardService) {
        this.restTemplate = restTemplate;
        this.cardService = cardService;
    }

    /**
     * Nested enrichment flow:
     * <ol>
     *   <li>Load the local card from MSSQL (via {@link CardService})</li>
     *   <li>Call JSONPlaceholder {@code GET /users/{id}}</li>
     *   <li>Return a combined map for the client</li>
     * </ol>
     *
     * @param cardId         local card primary key
     * @param externalUserId remote user id on JSONPlaceholder
     * @return map with keys {@code card}, {@code externalProfile}, {@code source}
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCardWithExternalProfile(Long cardId, Long externalUserId) {
        log.info("Nested API call: cardId={}, externalUserId={}", cardId, externalUserId);

        CardResponse card = cardService.getCardById(cardId);
        ExternalUserResponse externalUser = fetchExternalUser(externalUserId);

        Map<String, Object> result = new HashMap<>();
        result.put("card", card);
        result.put("externalProfile", externalUser);
        // Helps reviewers / handover see the call chain without digging into logs
        result.put("source",
                "Postman/Client → /api/v1/cards/{id}/enrich → JSONPlaceholder /users/{userId}");

        return result;
    }

    /**
     * Calls the 3rd-party user endpoint and maps the JSON into {@link ExternalUserResponse}.
     *
     * @param userId remote user id
     * @return populated external user DTO
     * @throws ResourceNotFoundException if the remote user is missing / empty
     * @throws BusinessException         if the HTTP call itself fails (timeout, network, etc.)
     */
    public ExternalUserResponse fetchExternalUser(Long userId) {
        String url = externalApiBaseUrl + "/users/" + userId;
        log.info("Calling 3rd-party API: {}", url);

        try {
            ExternalUserResponse response = restTemplate.getForObject(url, ExternalUserResponse.class);
            if (response == null || response.getId() == null) {
                throw new ResourceNotFoundException("External user not found with id: " + userId);
            }
            log.info("3rd-party API response received for userId={}", response.getId());
            return response;
        } catch (RestClientException ex) {
            log.error("3rd-party API call failed: {}", ex.getMessage());
            throw new BusinessException("Failed to call 3rd-party API: " + ex.getMessage());
        }
    }
}
