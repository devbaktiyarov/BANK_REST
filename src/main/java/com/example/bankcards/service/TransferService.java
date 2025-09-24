package com.example.bankcards.service;

import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.User;

public interface TransferService {
    TransferDto transferBetweenOwnCards(TransferRequestDto request, User user);
}
