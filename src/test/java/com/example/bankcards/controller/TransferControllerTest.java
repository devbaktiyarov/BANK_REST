package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(controllers = TransferController.class)
@Import({TestSecurityConfig.class, com.example.bankcards.exception.GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class TransferControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean TransferService transferService;


    private User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        return user;
    }

    @Test
    @DisplayName("POST /v1/api/transfers/own returns 200 and transfer data")
    void transferBetweenOwnCards_success() throws Exception {
        var principal = buildUser();
        var auth = new UsernamePasswordAuthenticationToken(
                principal, principal.getPassword(), principal.getAuthorities());

        var responseDto = new TransferDto(10L, 1001L, 2002L,
                new BigDecimal("150.00"), LocalDateTime.of(2024,1,1,12,0));

        Mockito.when(transferService.transferBetweenOwnCards(any(TransferRequestDto.class), eq(principal)))
               .thenReturn(responseDto);

        var requestDto = new TransferRequestDto(1001L, 2002L, new BigDecimal("150.00"));

        mockMvc.perform(post("/v1/api/transfers/own")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(authentication(auth)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /v1/api/transfers/own with invalid body returns 400")
    void transferBetweenOwnCards_validationError() throws Exception {
        var principal = buildUser();
        var auth = new UsernamePasswordAuthenticationToken(
                principal, principal.getPassword(), principal.getAuthorities());

                Map<String,Object> m = new HashMap<>();
                m.put("fromCardId", 1001L);
                m.put("toCardId", -5L);
                m.put("amount", null);
                String invalidJson = objectMapper.writeValueAsString(m);
                
                    
        mockMvc.perform(post("/v1/api/transfers/own")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
                .with(authentication(auth)))
            .andDo(print())
            .andExpect(handler().handlerType(TransferController.class))
            .andExpect(handler().methodName("transferBetweenOwnCards"))
            .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(transferService);
    }
}
