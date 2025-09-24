package com.example.bankcards.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.bankcards.entity.Card;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByIdAndIsDeletedFalse(Long id);
    Page<Card> findAllByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
    Optional<Card> findByCardNumberAndIsDeletedFalse(String cardNumber);
}
