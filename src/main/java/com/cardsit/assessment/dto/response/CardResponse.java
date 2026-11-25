package com.cardsit.assessment.dto.response;

import com.cardsit.assessment.entity.Card;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CardResponse {

    private Long id;
    private String cardNumber;
    private String cardholderName;
    private String cardType;
    private String status;
    private BigDecimal creditLimit;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CardResponse() {
    }

    public CardResponse(Long id, String cardNumber, String cardholderName, String cardType,
                        String status, BigDecimal creditLimit, String currency,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.cardholderName = cardholderName;
        this.cardType = cardType;
        this.status = status;
        this.creditLimit = creditLimit;
        this.currency = currency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CardResponse fromEntity(Card card) {
        return new CardResponse(
                card.getId(),
                maskCardNumber(card.getCardNumber()),
                card.getCardholderName(),
                card.getCardType(),
                card.getStatus(),
                card.getCreditLimit(),
                card.getCurrency(),
                card.getCreatedAt(),
                card.getUpdatedAt()
        );
    }

    private static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardholderName() { return cardholderName; }
    public void setCardholderName(String cardholderName) { this.cardholderName = cardholderName; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
