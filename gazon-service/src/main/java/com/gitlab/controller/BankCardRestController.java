package com.gitlab.controller;

import com.gitlab.controllers.api.rest.BankCardRestApi;
import com.gitlab.dto.BankCardDto;
import com.gitlab.mapper.BankCardMapper;
import com.gitlab.model.BankCard;
import com.gitlab.model.User;
import com.gitlab.service.BankCardService;
import com.gitlab.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("Вызван метод getPage с параметрами: page={}, size={}", page, size);
        var bankCardPage = bankCardService.getPageDto(page, size);
        if (bankCardPage == null || bankCardPage.getContent().isEmpty()) {
            log.warn("getPage: Page is empty");
            return ResponseEntity.noContent().build();
        }
        log.info("getPage: возвращено {} карт", bankCardPage.getTotalElements());
        return ResponseEntity.ok(bankCardPage.getContent());
    }

    @Override
    public ResponseEntity<BankCardDto> get(Long cardId) {
        log.info("Вызван метод get с cardId={}", cardId);
        User user = userService.getAuthenticatedUser();

        if (isAdmin(user)) {
            log.info("Пользователь {} является администратором", user.getUsername());
            return bankCardService.findByIdDto(cardId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        if (isAuthorized(user, cardId)) {
            log.info("Пользователь {} авторизован для доступа к карте cardId={}", user.getUsername(), cardId);
            return bankCardService.findByIdDto(cardId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else {
            log.warn("Пользователь {} не авторизован для доступа к карте cardId={}", user.getUsername(), cardId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Override
    public ResponseEntity<BankCardDto> create(BankCardDto bankCardDto) {
        log.info("Вызван метод create с объектом BankCardDto: {}", bankCardDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bankCardService.saveDto(bankCardDto));
    }

    @Override
    public ResponseEntity<BankCardDto> update(Long cardId, BankCardDto bankCardDto) {
        log.info("Вызван метод update с cardId={} и объектом BankCardDto: {}", cardId, bankCardDto);
        User user = userService.getAuthenticatedUser();

        if (isAdmin(user) || isAuthorized(user, cardId)) {
            Optional<BankCardDto> optionalBankCardDto = bankCardService.updateDto(cardId, bankCardDto);
            log.info("Карта обновлена: {}", optionalBankCardDto);
            return optionalBankCardDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            log.warn("Пользователь {} не авторизован для обновления карты cardId={}", user.getUsername(), cardId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Override
    public ResponseEntity<Void> delete(Long cardId) {
        log.info("Вызван метод delete с cardId={}", cardId);
        User user = userService.getAuthenticatedUser();

        if (isAdmin(user)) {
            log.info("Пользователь {} является администратором", user.getUsername());
            return (bankCardService.deleteDto(cardId).isPresent())
                    ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        }

        if (isAuthorized(user, cardId)) {
            log.info("Пользователь {} авторизован для удаления карты cardId={}", user.getUsername(), cardId);
            return (bankCardService.deleteDto(cardId).isPresent())
                    ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } else {
            log.warn("Пользователь {} не авторизован для удаления карты cardId={}", user.getUsername(), cardId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private boolean isAuthorized(User user, Long requestedCardId) {
        log.debug("Проверка авторизации пользователя {} для карты cardId={}", user.getUsername(), requestedCardId);
        return user.getBankCardsSet()
                .stream()
                .map(BankCard::getId)
                .anyMatch(cardId -> Objects.equals(cardId, requestedCardId));
    }
}
