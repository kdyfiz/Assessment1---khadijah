package com.cardsit.assessment.controller;

import com.cardsit.assessment.dto.request.CreateCardRequest;
import com.cardsit.assessment.dto.request.UpdateCardRequest;
import com.cardsit.assessment.dto.response.ApiResponse;
import com.cardsit.assessment.dto.response.CardResponse;
import com.cardsit.assessment.dto.response.PageResponse;
import com.cardsit.assessment.service.CardService;
import com.cardsit.assessment.service.ExternalApiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for card CRUD and enrichment endpoints.
 *
 * <p><b>Base path:</b> {@code /api/v1/cards}
 *
 * <p>This layer only handles HTTP concerns (routing, status codes, request validation).
 * Business logic lives in {@link CardService} / {@link ExternalApiService}.
 *
 * <p>All responses are wrapped in {@link ApiResponse} for a consistent client contract.
 * Validation failures and business errors are handled by {@code GlobalExceptionHandler}.
 */
@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;
    private final ExternalApiService externalApiService;

    public CardController(CardService cardService, ExternalApiService externalApiService) {
        this.cardService = cardService;
        this.externalApiService = externalApiService;
    }

    /**
     * Creates a new card (INSERT).
     *
     * <p>{@code @Valid} triggers Bean Validation on {@link CreateCardRequest}
     * before the service is called.
     *
     * @param request card details from the client body
     * @return HTTP 201 with the created card (card number masked)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CardResponse>> createCard(
            @Valid @RequestBody CreateCardRequest request) {
        CardResponse created = cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Card created successfully", created));
    }

    /**
     * Partially updates an existing card (UPDATE).
     * Only non-null fields on the request are applied in the service.
     *
     * @param id      path variable — primary key of the card
     * @param request fields to update (all optional)
     * @return HTTP 200 with the updated card
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CardResponse>> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCardRequest request) {
        CardResponse updated = cardService.updateCard(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Card updated successfully", updated));
    }

    /**
     * Retrieves a single card by id.
     *
     * @param id path variable — primary key of the card
     * @return HTTP 200 with the card, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CardResponse>> getCardById(@PathVariable Long id) {
        CardResponse card = cardService.getCardById(id);
        return ResponseEntity.ok(ApiResponse.ok("Card retrieved successfully", card));
    }

    /**
     * Lists cards with fixed page size of 10.
     *
     * @param page   zero-based page index (default {@code 0})
     * @param status optional filter, e.g. {@code ACTIVE} / {@code BLOCKED}
     * @return HTTP 200 with paginated card list
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CardResponse>>> getCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status) {
        PageResponse<CardResponse> cards = cardService.getCards(page, status);
        return ResponseEntity.ok(ApiResponse.ok("Cards retrieved successfully", cards));
    }

    /**
     * Nested 3rd-party enrichment endpoint.
     *
     * <p>Flow: Client → this API → local MSSQL (card) → JSONPlaceholder user profile
     * → combined JSON response.
     *
     * @param id              local card id
     * @param externalUserId  JSONPlaceholder user id (default {@code 1})
     * @return map containing {@code card}, {@code externalProfile}, and {@code source}
     */
    @GetMapping("/{id}/enrich")
    public ResponseEntity<ApiResponse<Map<String, Object>>> enrichCardWithExternalProfile(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Long externalUserId) {
        Map<String, Object> result = externalApiService.getCardWithExternalProfile(id, externalUserId);
        return ResponseEntity.ok(ApiResponse.ok("Card enriched with external profile", result));
    }
}
