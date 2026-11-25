package com.cardsit.assessment.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity mapped to MSSQL table {@code cards}.
 *
 * <p>This is the persistence model — do not expose it directly on the API.
 * Controllers / clients receive {@code CardResponse} instead (with masked PAN).
 *
 * <p><b>Lifecycle callbacks:</b>
 * <ul>
 *   <li>{@link #onCreate()} — sets timestamps and defaults (ACTIVE / MYR) on insert</li>
 *   <li>{@link #onUpdate()} — refreshes {@code updatedAt} on every update</li>
 * </ul>
 */
@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Primary Account Number (PAN). Stored in full; masked only in API responses. */
    @Column(name = "card_number", nullable = false, unique = true, length = 19)
    private String cardNumber;

    @Column(name = "cardholder_name", nullable = false, length = 100)
    private String cardholderName;

    /** Allowed values at API layer: DEBIT | CREDIT | PREPAID. */
    @Column(name = "card_type", nullable = false, length = 20)
    private String cardType;

    /** Allowed values at API layer: ACTIVE | INACTIVE | BLOCKED | EXPIRED. */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    /** ISO 4217 currency code (e.g. MYR). Defaults to MYR on create if null. */
    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Card() {
    }

    public Card(Long id, String cardNumber, String cardholderName, String cardType,
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

    public static Builder builder() {
        return new Builder();
    }

    /** Runs before INSERT — stamps timestamps and applies safe defaults. */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "ACTIVE";
        }
        if (currency == null) {
            currency = "MYR";
        }
    }

    /** Runs before UPDATE — keeps {@code updatedAt} in sync. */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    /**
     * Manual builder (no Lombok) so the project stays dependency-light.
     * Prefer {@code Card.builder()...build()} when constructing new entities.
     */
    public static final class Builder {
        private Long id;
        private String cardNumber;
        private String cardholderName;
        private String cardType;
        private String status;
        private BigDecimal creditLimit;
        private String currency;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private Builder() {
        }

        public Builder id(Long id) { this.id = id; return this; }
        public Builder cardNumber(String cardNumber) { this.cardNumber = cardNumber; return this; }
        public Builder cardholderName(String cardholderName) { this.cardholderName = cardholderName; return this; }
        public Builder cardType(String cardType) { this.cardType = cardType; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder creditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; return this; }
        public Builder currency(String currency) { this.currency = currency; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Card build() {
            return new Card(id, cardNumber, cardholderName, cardType, status,
                    creditLimit, currency, createdAt, updatedAt);
        }
    }
}
