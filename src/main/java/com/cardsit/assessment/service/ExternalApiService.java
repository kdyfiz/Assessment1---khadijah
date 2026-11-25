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
 * Demonstrates nested API calling:
 * Client → Our API → 3rd-party API (JSONPlaceholder)
 */
@Service
public class ExternalApiService {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiService.class);

    private final RestTemplate restTemplate;
    private final CardService cardService;

    @Value("${external.api.base-url}")
    private String externalApiBaseUrl;

    public ExternalApiService(RestTemplate restTemplate, CardService cardService) {
        this.restTemplate = restTemplate;
        this.cardService = cardService;
    }

    /**
     * Nested flow:
     * 1. GET card from local DB (@Transactional read)
     * 2. Call 3rd-party API to enrich with external user profile
     * 3. Return combined response to client
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCardWithExternalProfile(Long cardId, Long externalUserId) {
        log.info("Nested API call: cardId={}, externalUserId={}", cardId, externalUserId);

        CardResponse card = cardService.getCardById(cardId);
        ExternalUserResponse externalUser = fetchExternalUser(externalUserId);

        Map<String, Object> result = new HashMap<>();
        result.put("card", card);
        result.put("externalProfile", externalUser);
        result.put("source",
                "Postman/Client → /api/v1/cards/{id}/enrich → JSONPlaceholder /users/{userId}");

        return result;
    }

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
