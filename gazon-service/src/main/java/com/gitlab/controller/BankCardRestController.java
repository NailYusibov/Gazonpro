package com.gitlab.controller;

import com.gitlab.controllers.api.rest.BankCardRestApi;
import com.gitlab.dto.BankCardDto;
import com.gitlab.exception.handler.ForbiddenException;
import com.gitlab.model.BankCard;
import com.gitlab.model.User;
import com.gitlab.service.BankCardService;
import com.gitlab.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Validated
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BankCardRestController implements BankCardRestApi {

    private final BankCardService bankCardService;
    private final UserService userService;


    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BankCardDto>> getPage(Integer page, Integer size) {
        var bankCardPage = bankCardService.getPageDto(page, size);
        if (bankCardPage == null || bankCardPage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bankCardPage.getContent());
    }

    @Override
    public ResponseEntity<BankCardDto> get(Long cardId) {
        User user = userService.getAuthenticatedUser();
        if (user.getRolesSet().toString().contains("ROLE_ADMIN")) {
            return bankCardService.findByIdDto(cardId)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            if (getBankCardId(user, cardId).equals(cardId)) {
                return bankCardService.findByIdDto(cardId)
                        .map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<BankCardDto> create(BankCardDto bankCardDto) {
        BankCardDto savedBankCardDto = bankCardService.saveDto(bankCardDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedBankCardDto);
    }

    @Override
    public ResponseEntity<BankCardDto> update(Long cardId, BankCardDto bankCardDto) {
        User user = userService.getAuthenticatedUser();
        Optional<BankCardDto> optionalBankCardDto = bankCardService.updateDto(cardId, bankCardDto);
        if (optionalBankCardDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (user.getRolesSet().toString().contains("ROLE_ADMIN")) {
            return ResponseEntity.ok(optionalBankCardDto.get());
        } else if (getBankCardId(user, cardId).equals(cardId)) {
            return ResponseEntity.ok(optionalBankCardDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @Override
    public ResponseEntity<Void> delete(Long cardId) {
        User user = userService.getAuthenticatedUser();
        if (user.getRolesSet().toString().contains("ROLE_ADMIN")) {
            return (bankCardService.deleteDto(cardId).isPresent())
                    ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } else if (getBankCardId(user, cardId).equals(cardId)) {
            return (bankCardService.deleteDto(cardId).isPresent())
                    ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private Long getBankCardId(User user, Long id) {
        return user.getBankCardsSet()
                .stream()
                .map(BankCard::getId)
                .filter(bcId -> Objects.equals(bcId, id))
                .findAny()
                .orElseThrow(() -> new ForbiddenException(HttpStatus.FORBIDDEN, "Карта не принадлежит пользователю"));
    }
}
