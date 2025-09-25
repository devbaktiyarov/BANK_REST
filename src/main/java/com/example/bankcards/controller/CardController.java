package com.example.bankcards.controller;

import com.example.bankcards.dto.CardAmountDto;
import com.example.bankcards.dto.CardCreationRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardStatusRequestDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.AuthorizationService;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/cards")
public class CardController {

    private final CardService cardService;
    private final AuthorizationService authorizationService;
    
    @PostMapping
    public ResponseEntity<Void> createCard(@AuthenticationPrincipal User currentUser, @RequestBody @Valid CardCreationRequestDto request) {
        authorizationService.authorizeAdmin(currentUser.getEmail());
        Long id = cardService.createCard(request);
        URI location = URI.create("/v1/api/cards/" + id);
        return ResponseEntity.created(location).build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCardById(@PathVariable Long id) {
        CardDto card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    @GetMapping
    public Page<CardDto> getAllCards(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        authorizationService.authorizeAdmin(currentUser.getEmail());
        return cardService.getAllCards(pageable);
    }


    @GetMapping("/my")
    public Page<CardDto> getMyCards(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        authorizationService.authorizeUser(currentUser.getEmail());
        return cardService.getUsersAllCards(currentUser.getId(), pageable);
    }

    @GetMapping("/my/{id}")
    public ResponseEntity<CardDto> getMyCardById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ) {
        authorizationService.authorizeUser(currentUser.getEmail());
        CardDto card = cardService.getCardByIdAndUserId(id, currentUser.getId());
        return ResponseEntity.ok(card);
    }


    @PostMapping("/{id}/status")
    public ResponseEntity<Void> setStatus(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @RequestBody @Valid CardStatusRequestDto request
    ) {
        authorizationService.authorizeAdmin(currentUser.getEmail());
        cardService.setStatus(id, request.status());
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ) {
        authorizationService.authorizeAdmin(currentUser.getEmail());
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/add")
    public ResponseEntity<Void> addAmountToCard(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @RequestBody @Valid CardAmountDto request
    ) {
        authorizationService.authorizeAdmin(currentUser.getEmail());
        cardService.addAmountToCard(id, request.amount());
        return ResponseEntity.noContent().build();
    }
}
