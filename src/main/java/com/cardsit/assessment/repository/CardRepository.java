package com.cardsit.assessment.repository;

import com.cardsit.assessment.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    Page<Card> findByStatus(String status, Pageable pageable);

    boolean existsByCardNumber(String cardNumber);
}
