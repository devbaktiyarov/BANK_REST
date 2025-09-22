package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreationRequestDto;
import com.example.bankcards.dto.CardDto;

public interface CardService {
    Long createCard(CardCreationRequestDto request);
    CardDto getCardById(Long id);    
}
