package com.cardsit.assessment.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Request body for {@code POST /api/v1/cards}.
 *
 * <p>Validated by Jakarta Bean Validation when the controller uses {@code @Valid}.
 * Failed constraints surface as HTTP 400 via {@code GlobalExceptionHandler}.
 */
public class CreateCardRequest {

    @NotBlank(message = "Card number is required")
    @Size(min = 13, max = 19, message = "Card number must be 13-19 digits")
    @Pattern(regexp = "^[0-9]+$", message = "Card number must contain digits only")
    private String cardNumber;

    @NotBlank(message = "Cardholder name is required")
    @Size(max = 100)
    private String cardholderName;

    @NotBlank(message = "Card type is required")
    @Pattern(regexp = "DEBIT|CREDIT|PREPAID", message = "Card type must be DEBIT, CREDIT or PREPAID")
    private String cardType;

    @DecimalMin(value = "0.0", inclusive = true, message = "Credit limit cannot be negative")
    private BigDecimal creditLimit;

    /** Optional; entity defaults to MYR if omitted. Must be a 3-letter ISO code when present. */
    @Size(min = 3, max = 3, message = "Currency must be 3-letter ISO code")
    private String currency;

    public CreateCardRequest() {
    }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardholderName() { return cardholderName; }
    public void setCardholderName(String cardholderName) { this.cardholderName = cardholderName; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
