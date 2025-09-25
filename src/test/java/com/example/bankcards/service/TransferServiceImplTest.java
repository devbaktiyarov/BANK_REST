package com.example.bankcards.service;

import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    private User user;
    private Card from;
    private Card to;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        from = new Card();
        from.setId(10L);
        from.setUser(user);
        from.setStatus(CardStatus.ACTIVE);
        from.setBalance(new BigDecimal("100.00"));

        to = new Card();
        to.setId(20L);
        to.setUser(user);
        to.setStatus(CardStatus.ACTIVE);
        to.setBalance(new BigDecimal("10.00"));
    }

    @Test
    void transferBetweenOwnCards_success() {
        TransferRequestDto request = new TransferRequestDto(10L, 20L, new BigDecimal("25.00"));

        when(cardRepository.findById(10L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(20L)).thenReturn(Optional.of(to));

        Transfer persisted = new Transfer();
        persisted.setId(100L);
        persisted.setFrom(from);
        persisted.setTo(to);
        persisted.setAmount(new BigDecimal("25.00"));
        when(transferRepository.save(any(Transfer.class))).thenReturn(persisted);

        TransferDto dto = transferService.transferBetweenOwnCards(request, user);

        assertEquals(100L, dto.id());
        assertEquals(10L, dto.fromCardId());
        assertEquals(20L, dto.toCardId());
        assertEquals(new BigDecimal("25.00"), dto.amount());

        assertEquals(new BigDecimal("75.00"), from.getBalance());
        assertEquals(new BigDecimal("35.00"), to.getBalance());

        ArgumentCaptor<Transfer> captor = ArgumentCaptor.forClass(Transfer.class);
        verify(transferRepository).save(captor.capture());
        assertEquals(new BigDecimal("25.00"), captor.getValue().getAmount());
    }

    @Test
    void transferBetweenOwnCards_negativeAmount_throws() {
        TransferRequestDto request = new TransferRequestDto(10L, 20L, new BigDecimal("-1.00"));
        assertThrows(IllegalArgumentException.class, () -> transferService.transferBetweenOwnCards(request, user));
    }

    @Test
    void transferBetweenOwnCards_sameCard_throws() {
        TransferRequestDto request = new TransferRequestDto(10L, 10L, new BigDecimal("1.00"));
        assertThrows(IllegalArgumentException.class, () -> transferService.transferBetweenOwnCards(request, user));
    }

    @Test
    void transferBetweenOwnCards_cardNotFound_throws() {
        TransferRequestDto request = new TransferRequestDto(10L, 20L, new BigDecimal("1.00"));
        when(cardRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> transferService.transferBetweenOwnCards(request, user));
    }

    @Test
    void transferBetweenOwnCards_wrongOwner_throws() {
        TransferRequestDto request = new TransferRequestDto(10L, 20L, new BigDecimal("1.00"));
        when(cardRepository.findById(10L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(20L)).thenReturn(Optional.of(to));

        User other = new User();
        other.setId(2L);
        to.setUser(other);

        assertThrows(IllegalStateException.class, () -> transferService.transferBetweenOwnCards(request, user));
    }

    @Test
    void transferBetweenOwnCards_inactive_throws() {
        TransferRequestDto request = new TransferRequestDto(10L, 20L, new BigDecimal("1.00"));
        when(cardRepository.findById(10L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(20L)).thenReturn(Optional.of(to));

        from.setStatus(CardStatus.BLOCKED);

        assertThrows(IllegalStateException.class, () -> transferService.transferBetweenOwnCards(request, user));
    }

    @Test
    void transferBetweenOwnCards_insufficientFunds_throws() {
        TransferRequestDto request = new TransferRequestDto(10L, 20L, new BigDecimal("1000.00"));
        when(cardRepository.findById(10L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(20L)).thenReturn(Optional.of(to));

        assertThrows(IllegalStateException.class, () -> transferService.transferBetweenOwnCards(request, user));
    }
}


