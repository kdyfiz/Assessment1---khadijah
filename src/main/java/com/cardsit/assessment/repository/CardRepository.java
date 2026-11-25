package com.cardsit.assessment.repository;

import com.cardsit.assessment.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Card} entity (table {@code cards}).
 *
 * <p>Method names follow Spring Data query derivation — no custom SQL needed
 * unless queries become more complex. Inherited {@link JpaRepository} methods
 * cover save / findById / findAll / delete.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    /** Lookup by unique card number (used for existence checks / future lookups). */
    Optional<Card> findByCardNumber(String cardNumber);

    /** Paginated list filtered by status (e.g. ACTIVE, BLOCKED). */
    Page<Card> findByStatus(String status, Pageable pageable);

    /** Fast uniqueness check before INSERT — avoids relying only on DB constraint errors. */
    boolean existsByCardNumber(String cardNumber);
}
