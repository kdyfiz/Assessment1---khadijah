package com.cardsit.assessment.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Request body for {@code PUT /api/v1/cards/{id}}.
 *
 * <p>All fields are optional — {@code CardService} only updates non-null values.
 * Card number / card type are not included (immutable after create in this design).
 */
public class UpdateCardRequest {

    @Size(max = 100)
    private String cardholderName;

    @Pattern(regexp = "ACTIVE|INACTIVE|BLOCKED|EXPIRED",
            message = "Status must be ACTIVE, INACTIVE, BLOCKED or EXPIRED")
    private String status;

    @DecimalMin(value = "0.0", inclusive = true, message = "Credit limit cannot be negative")
    private BigDecimal creditLimit;

    @Size(min = 3, max = 3, message = "Currency must be 3-letter ISO code")
    private String currency;

    public UpdateCardRequest() {
    }

    public String getCardholderName() { return cardholderName; }
    public void setCardholderName(String cardholderName) { this.cardholderName = cardholderName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
