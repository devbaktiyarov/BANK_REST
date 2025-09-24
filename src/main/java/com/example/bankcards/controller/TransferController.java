package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/transfers")
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/own")
    public ResponseEntity<TransferDto> transferBetweenOwnCards(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid TransferRequestDto request
    ) {
        TransferDto transfer = transferService.transferBetweenOwnCards(request, user);
        return ResponseEntity.ok(transfer);
    }



}
