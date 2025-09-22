package com.example.bankcards.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.bankcards.dto.CardCreationRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardMaskingUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CardServiceImpl implements CardService {
    
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Override
    public Long createCard(CardCreationRequestDto request) {

        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Card card = new Card();
        card.setCardNumber(request.cardNumber());
        card.setOwnerName(request.ownerName());
        card.setExpiry(request.expiry());
        card.setUser(user);
        card.setBalance(java.math.BigDecimal.ZERO);
        card.setStatus(CardStatus.ACTIVE);
        Card savedCard = cardRepository.save(card);
        return savedCard.getId();
    }


    @Override
    public CardDto getCardById(Long id) {
        Card card = findActiveCard(id);
        return new CardDto(
            card.getId(),
            CardMaskingUtil.mask(card.getCardNumber()),
            card.getOwnerName(),
            card.getExpiry().toString(),
            card.getStatus().name(),
            card.getUser().getId()
        );
    }


    @Override
    public void setStatus(Long id, CardStatus status) {
        Card card = findActiveCard(id);
        card.setStatus(status);
        cardRepository.save(card);
    }

    @Override
    public void deleteCard(Long id) {
        Card card = findActiveCard(id);
        card.setIsDeleted(true);
        cardRepository.save(card);
    }

    @Override
    public Page<CardDto> getAllCards(Long userId, Pageable pageable) {
        return cardRepository.findAllByUserIdAndIsDeletedFalse(userId, pageable)
                .map(card -> new CardDto(
                        card.getId(),
                        CardMaskingUtil.mask(card.getCardNumber()),
                        card.getOwnerName(),
                        card.getExpiry().toString(),
                        card.getStatus().name(),
                        card.getUser().getId()
                ));
    }



    private Card findActiveCard(Long id) {
        return cardRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + id));
    }
}
