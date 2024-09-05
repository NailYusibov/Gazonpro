package com.gitlab.controller;

import com.gitlab.controllers.api.rest.BankCardRestApi;
import com.gitlab.dto.BankCardDto;
import com.gitlab.mapper.BankCardMapper;
import com.gitlab.model.BankCard;
import com.gitlab.model.User;
import com.gitlab.service.BankCardService;
import com.gitlab.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.gitlab.util.UserUtils.isAdmin;

@Validated
@RestController
public class BankCardRestController implements BankCardRestApi {

    private final BankCardService bankCardService;
    private final UserService userService;
    private final BankCardMapper bankCardMapper;

    public BankCardRestController(BankCardService bankCardService,
                                  UserService userService,
                                  BankCardMapper bankCardMapper) {
        this.bankCardService = bankCardService.clone();
        this.userService = userService.clone();
        this.bankCardMapper = bankCardMapper;
    }

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

        if (isAdmin(user)) {
            return bankCardService.findByIdDto(cardId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        if (isAuthorized(user, cardId)) {
            return bankCardService.findByIdDto(cardId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Override
    public ResponseEntity<BankCardDto> create(BankCardDto bankCardDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bankCardService.saveDto(bankCardDto));
    }

    @Override
    public ResponseEntity<BankCardDto> update(Long cardId, BankCardDto bankCardDto) {
        User user = userService.getAuthenticatedUser();

        if (isAdmin(user) || isAuthorized(user, cardId)) {
            Optional<BankCardDto> optionalBankCardDto = bankCardService.updateDto(cardId, bankCardDto);
            return optionalBankCardDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Override
    public ResponseEntity<Void> delete(Long cardId) {
        User user = userService.getAuthenticatedUser();

        if (isAdmin(user)) {
            return (bankCardService.deleteDto(cardId).isPresent())
                    ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        }

        if (isAuthorized(user, cardId)) {
            return (bankCardService.deleteDto(cardId).isPresent())
                    ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    private boolean isAuthorized(User user, Long requestedCardId) {
        return user.getBankCardsSet()
                .stream()
                .map(BankCard::getId)
                .anyMatch(cardId -> Objects.equals(cardId, requestedCardId));
    }
}
