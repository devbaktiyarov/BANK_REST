package com.example.bankcards.service;

import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TransferServiceImpl implements TransferService {

    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;


    @Override
    @Transactional
    public TransferDto transferBetweenOwnCards(TransferRequestDto request, User user) {
        if (request.amount() == null || request.amount().signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (request.fromCardId().equals(request.toCardId())) {
            throw new IllegalArgumentException("Different cards required");
        }

        Card from = cardRepository.findById(request.fromCardId())
                .orElseThrow(() -> new IllegalArgumentException("From card not found"));
        Card to = cardRepository.findById(request.toCardId())
                .orElseThrow(() -> new IllegalArgumentException("To card not found"));

        Long userId = user.getId();

        if (!from.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Source card does not belong to user");
        }
        if (!to.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Destination card does not belong to user");
        }

        if (from.getStatus() != CardStatus.ACTIVE || to.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Cards must be active");
        }

        if (from.getBalance().compareTo(request.amount()) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));

        Transfer transfer = new Transfer();
        transfer.setFrom(from);
        transfer.setTo(to);
        transfer.setAmount(request.amount());

        Transfer savedTransfer = transferRepository.save(transfer);

        return new TransferDto(
                savedTransfer.getId(),
                savedTransfer.getFrom().getId(),
                savedTransfer.getTo().getId(),
                savedTransfer.getAmount(),
                savedTransfer.getCreatedAt()
        );
    }
}
