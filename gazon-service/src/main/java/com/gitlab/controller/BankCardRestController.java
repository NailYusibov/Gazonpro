package com.gitlab.controller;

import com.gitlab.controllers.api.rest.BankCardRestApi;
import com.gitlab.dto.BankCardDto;
import com.gitlab.model.BankCard;
import com.gitlab.model.Role;
import com.gitlab.service.BankCardService;
import com.gitlab.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.List;
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
        Long userBankCardId = userService.getUsernameFromAuthentication().getBankCardsSet().stream().map(BankCard::getId).findAny()
                .orElseThrow(() -> new EntityNotFoundException("Банковская карта не найдена"));
        if ("ROLE_ADMIN".equals(userService.getUsernameFromAuthentication().getRolesSet().stream().map(Role::getName).findAny().orElse(null))) {
            Optional<BankCardDto> optionalBankCardDto = bankCardService.findByIdDto(cardId);
            return optionalBankCardDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } else {
            if (userBankCardId != null && userBankCardId.equals(cardId)) {
                Optional<BankCardDto> optionalBankCardDto = bankCardService.findByIdDto(cardId);
                return optionalBankCardDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
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
        Long userBankCardId = userService.getUsernameFromAuthentication().getBankCardsSet().stream().map(BankCard::getId).findAny()
                .orElseThrow(() -> new EntityNotFoundException("Банковская карта не найдена"));
        if ("ROLE_ADMIN".equals(userService.getUsernameFromAuthentication().getRolesSet().stream().map(Role::getName).findAny().orElse(null))) {
            Optional<BankCardDto> optionalBankCardDto = bankCardService.updateDto(cardId, bankCardDto);
            if (optionalBankCardDto.isPresent()) {
                Optional<BankCardDto> updatedBankCardDto = bankCardService.updateDto(cardId, bankCardDto);

                return ResponseEntity.ok(updatedBankCardDto.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } else {
            if (userBankCardId != null && userBankCardId.equals(cardId)) {
                Optional<BankCardDto> optionalBankCardDto = bankCardService.updateDto(cardId, bankCardDto);
                if (optionalBankCardDto.isPresent()) {
                    Optional<BankCardDto> updatedBankCardDto = bankCardService.updateDto(cardId, bankCardDto);

                    return ResponseEntity.ok(updatedBankCardDto.get());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @Override
    public ResponseEntity<Void> delete(Long cardId) {
        Long userBankCardId = userService.getUsernameFromAuthentication().getBankCardsSet().stream().map(BankCard::getId).findAny()
                .orElseThrow(() -> new EntityNotFoundException("Банковская карта не найдена"));
        if ("ROLE_ADMIN".equals(userService.getUsernameFromAuthentication().getRolesSet().stream().map(Role::getName).findAny().orElse(null))) {
            if (bankCardService.delete(cardId)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            if (userBankCardId != null && userBankCardId.equals(cardId)) {
                if (bankCardService.delete(cardId)) {
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
}
