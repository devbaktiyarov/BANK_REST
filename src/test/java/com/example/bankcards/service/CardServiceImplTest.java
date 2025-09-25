package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreationRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardAlreadyExistsException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
    }

    @Test
    void createCard_success() {
        CardCreationRequestDto request = new CardCreationRequestDto(
                "4111111111111111", "John Doe", YearMonth.of(2030, 12), 1L
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.findByCardNumberAndIsDeletedFalse("4111111111111111")).thenReturn(Optional.empty());

        Card saved = new Card();
        saved.setId(10L);
        when(cardRepository.save(any(Card.class))).thenReturn(saved);

        Long id = cardService.createCard(request);
        assertEquals(10L, id);

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(captor.capture());
        Card toSave = captor.getValue();
        assertEquals("4111111111111111", toSave.getCardNumber());
        assertEquals(CardStatus.ACTIVE, toSave.getStatus());
        assertEquals(BigDecimal.ZERO, toSave.getBalance());
        assertEquals(user, toSave.getUser());
    }

    @Test
    void createCard_cardExists_throws() {
        CardCreationRequestDto request = new CardCreationRequestDto(
                "4111111111111111", "John Doe", YearMonth.of(2030, 12), 1L
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.findByCardNumberAndIsDeletedFalse("4111111111111111"))
                .thenReturn(Optional.of(new Card()));

        assertThrows(CardAlreadyExistsException.class, () -> cardService.createCard(request));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void getCardById_masksNumber() {
        Card card = buildCard(5L, "4111111111111111", user, new BigDecimal("100.00"), CardStatus.ACTIVE, false);
        when(cardRepository.findByIdAndIsDeletedFalse(5L)).thenReturn(Optional.of(card));

        CardDto dto = cardService.getCardById(5L);
        assertEquals(5L, dto.id());
        assertEquals("**** **** **** 1111", dto.cardNumber());
        assertEquals(new BigDecimal("100.00"), dto.balance());
        assertEquals(user.getId(), dto.userId());
    }

    @Test
    void getCardById_notFound_throws() {
        when(cardRepository.findByIdAndIsDeletedFalse(5L)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> cardService.getCardById(5L));
    }

    @Test
    void setStatus_updatesAndSaves() {
        Card card = buildCard(7L, "4111111111111111", user, BigDecimal.ZERO, CardStatus.ACTIVE, false);
        when(cardRepository.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(card));

        cardService.setStatus(7L, CardStatus.BLOCKED);
        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void deleteCard_marksDeleted() {
        Card card = buildCard(7L, "4111111111111111", user, BigDecimal.ZERO, CardStatus.ACTIVE, false);
        when(cardRepository.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(card));

        cardService.deleteCard(7L);
        assertTrue(card.getIsDeleted());
        verify(cardRepository).save(card);
    }

    @Test
    void getUsersAllCards_pagesAndMasks() {
        Card card = buildCard(2L, "4111111111111111", user, BigDecimal.ZERO, CardStatus.ACTIVE, false);
        when(cardRepository.findAllByUserIdAndIsDeletedFalse(eq(1L), any()))
                .thenReturn(new PageImpl<>(List.of(card)));

        Page<CardDto> page = cardService.getUsersAllCards(1L, PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
        assertEquals("**** **** **** 1111", page.getContent().get(0).cardNumber());
    }

    @Test
    void addAmountToCard_increasesBalance() {
        Card card = buildCard(3L, "4111111111111111", user, new BigDecimal("10.00"), CardStatus.ACTIVE, false);
        when(cardRepository.findByIdAndIsDeletedFalse(3L)).thenReturn(Optional.of(card));

        cardService.addAmountToCard(3L, new BigDecimal("5.50"));

        assertEquals(new BigDecimal("15.50"), card.getBalance());
        verify(cardRepository).save(card);
    }

    private static Card buildCard(Long id, String number, User owner, BigDecimal balance, CardStatus status, boolean deleted) {
        Card c = new Card();
        c.setId(id);
        c.setCardNumber(number);
        c.setOwnerName("John Doe");
        c.setExpiry(YearMonth.of(2030, 12));
        c.setStatus(status);
        c.setBalance(balance);
        c.setUser(owner);
        c.setIsDeleted(deleted);
        return c;
    }
}


