package com.cardsit.assessment.service;

import com.cardsit.assessment.dto.request.CreateCardRequest;
import com.cardsit.assessment.dto.request.UpdateCardRequest;
import com.cardsit.assessment.dto.response.CardResponse;
import com.cardsit.assessment.dto.response.PageResponse;
import com.cardsit.assessment.entity.Card;
import com.cardsit.assessment.exception.BusinessException;
import com.cardsit.assessment.exception.ResourceNotFoundException;
import com.cardsit.assessment.repository.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Core business service for card lifecycle operations.
 *
 * <p>Owns create / update / read logic and maps between entities and DTOs.
 * Controllers should not talk to the repository directly — go through this service.
 *
 * <p><b>Transaction notes:</b>
 * <ul>
 *   <li>Writes use {@code @Transactional} so the whole operation commits or rolls back.</li>
 *   <li>Reads use {@code @Transactional(readOnly = true)} for a consistent snapshot
 *       and to hint the DB / Hibernate that no flush is needed.</li>
 * </ul>
 */
@Service
public class CardService {

    private static final Logger log = LoggerFactory.getLogger(CardService.class);

    /** Fixed page size required by the assessment (10 records per page). */
    private static final int PAGE_SIZE = 10;

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Creates a new card and persists it to MSSQL.
     *
     * <p>Rejects duplicate card numbers with {@link BusinessException}.
     * New cards default to status {@code ACTIVE}; currency defaults are applied
     * on the entity via {@code @PrePersist} if not supplied.
     *
     * @param request validated create payload
     * @return masked card response for the client
     */
    @Transactional
    public CardResponse createCard(CreateCardRequest request) {
        log.info("Creating card for cardholder={}", request.getCardholderName());

        // Uniqueness check before insert to return a clear business error
        if (cardRepository.existsByCardNumber(request.getCardNumber())) {
            throw new BusinessException("Card number already exists");
        }

        Card card = Card.builder()
                .cardNumber(request.getCardNumber())
                .cardholderName(request.getCardholderName())
                .cardType(request.getCardType())
                .creditLimit(request.getCreditLimit())
                .currency(request.getCurrency())
                .status("ACTIVE")
                .build();

        Card saved = cardRepository.save(card);
        log.info("Card created successfully id={}", saved.getId());
        return CardResponse.fromEntity(saved);
    }

    /**
     * Updates an existing card. Only non-null request fields are applied
     * (partial update / PATCH-style behaviour over PUT).
     *
     * <p>Card number and card type are intentionally not updatable here —
     * change that policy carefully if product requirements evolve.
     *
     * @param id      card primary key
     * @param request optional fields to change
     * @return updated masked card response
     * @throws ResourceNotFoundException if the card id does not exist
     */
    @Transactional
    public CardResponse updateCard(Long id, UpdateCardRequest request) {
        log.info("Updating card id={}", id);

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));

        // Apply only provided fields — null means "leave unchanged"
        if (request.getCardholderName() != null) {
            card.setCardholderName(request.getCardholderName());
        }
        if (request.getStatus() != null) {
            card.setStatus(request.getStatus());
        }
        if (request.getCreditLimit() != null) {
            card.setCreditLimit(request.getCreditLimit());
        }
        if (request.getCurrency() != null) {
            card.setCurrency(request.getCurrency());
        }

        Card updated = cardRepository.save(card);
        log.info("Card updated successfully id={}", updated.getId());
        return CardResponse.fromEntity(updated);
    }

    /**
     * Fetches a single card by primary key.
     *
     * @param id card primary key
     * @return masked card response
     * @throws ResourceNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public CardResponse getCardById(Long id) {
        log.info("Fetching card id={}", id);

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));

        return CardResponse.fromEntity(card);
    }

    /**
     * Returns a paginated list of cards (always 10 per page), optionally filtered by status.
     *
     * @param page   zero-based page index
     * @param status optional status filter (case-insensitive); blank/null = all cards
     * @return page metadata + masked card content
     */
    @Transactional(readOnly = true)
    public PageResponse<CardResponse> getCards(int page, String status) {
        log.info("Fetching cards page={}, size={}, status={}", page, PAGE_SIZE, status);

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").ascending());

        Page<Card> cardPage;
        if (status != null && !status.isBlank()) {
            cardPage = cardRepository.findByStatus(status.toUpperCase(), pageable);
        } else {
            cardPage = cardRepository.findAll(pageable);
        }

        Page<CardResponse> responsePage = cardPage.map(CardResponse::fromEntity);
        return PageResponse.fromPage(responsePage);
    }
}
