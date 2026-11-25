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

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;
    private final ExternalApiService externalApiService;

    public CardController(CardService cardService, ExternalApiService externalApiService) {
        this.cardService = cardService;
        this.externalApiService = externalApiService;
    }

    /** INSERT — POST /api/v1/cards */
    @PostMapping
    public ResponseEntity<ApiResponse<CardResponse>> createCard(
            @Valid @RequestBody CreateCardRequest request) {
        CardResponse created = cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Card created successfully", created));
    }

    /** UPDATE — PUT /api/v1/cards/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CardResponse>> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCardRequest request) {
        CardResponse updated = cardService.updateCard(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Card updated successfully", updated));
    }

    /** GET by id — GET /api/v1/cards/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CardResponse>> getCardById(@PathVariable Long id) {
        CardResponse card = cardService.getCardById(id);
        return ResponseEntity.ok(ApiResponse.ok("Card retrieved successfully", card));
    }

    /** GET with Pagination (10/page) — GET /api/v1/cards?page=0 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CardResponse>>> getCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status) {
        PageResponse<CardResponse> cards = cardService.getCards(page, status);
        return ResponseEntity.ok(ApiResponse.ok("Cards retrieved successfully", cards));
    }

    /**
     * Nested 3rd-party API call
     * Client → GET /api/v1/cards/{id}/enrich?externalUserId=1 → JSONPlaceholder
     */
    @GetMapping("/{id}/enrich")
    public ResponseEntity<ApiResponse<Map<String, Object>>> enrichCardWithExternalProfile(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Long externalUserId) {
        Map<String, Object> result = externalApiService.getCardWithExternalProfile(id, externalUserId);
        return ResponseEntity.ok(ApiResponse.ok("Card enriched with external profile", result));
    }
}
