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

@Service
public class CardService {

    private static final Logger log = LoggerFactory.getLogger(CardService.class);
    private static final int PAGE_SIZE = 10;

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * INSERT - create a new card record.
     * @Transactional ensures atomic commit/rollback for the write.
     */
    @Transactional
    public CardResponse createCard(CreateCardRequest request) {
        log.info("Creating card for cardholder={}", request.getCardholderName());

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
     * UPDATE - update an existing card.
     * @Transactional ensures atomic commit/rollback for the write.
     */
    @Transactional
    public CardResponse updateCard(Long id, UpdateCardRequest request) {
        log.info("Updating card id={}", id);

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));

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
     * GET by id - read within a transactional boundary (read-only).
     * @Transactional(readOnly = true) for consistent read snapshot.
     */
    @Transactional(readOnly = true)
    public CardResponse getCardById(Long id) {
        log.info("Fetching card id={}", id);

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));

        return CardResponse.fromEntity(card);
    }

    /**
     * GET with pagination - 10 records per page.
     * @Transactional(readOnly = true) for consistent paginated read.
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
