package com.example.bankcards.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.bankcards.dto.CardCreationRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.CardStatus;
import java.math.BigDecimal;

public interface CardService {
    Long createCard(CardCreationRequestDto request);
    CardDto getCardById(Long id);
    CardDto getCardByIdAndUserId(Long id, Long userId);  
    void setStatus(Long id, CardStatus status);
    void deleteCard(Long id);
    Page<CardDto> getUsersAllCards(Long userId, Pageable pageable);
    Page<CardDto> getAllCards(Pageable pageable);
    void addAmountToCard(Long cardId, BigDecimal amount);
}
