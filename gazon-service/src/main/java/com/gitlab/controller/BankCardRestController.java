package com.gitlab.controller;

import com.gitlab.controllers.api.rest.BankCardRestApi;
import com.gitlab.dto.BankCardDto;
import com.gitlab.service.BankCardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class BankCardRestController implements BankCardRestApi {

    private final BankCardService bankCardService;

    public ResponseEntity<List<BankCardDto>> getPage(Integer page, Integer size) {
        var bankCardPage = bankCardService.getPageDto(page, size);
        if (bankCardPage == null || bankCardPage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bankCardPage.getContent());
    }

    @Override
    public ResponseEntity<BankCardDto> get(Long id) {
        Optional<BankCardDto> optionalBankCardDto = bankCardService.findByIdDto(id);

        return optionalBankCardDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<BankCardDto> create(BankCardDto bankCardDto) {
        BankCardDto savedBankCardDto = bankCardService.saveDto(bankCardDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedBankCardDto);
    }

    @Override
    public ResponseEntity<BankCardDto> update(Long id, BankCardDto bankCardDto) {
        Optional<BankCardDto> updatedBankCardDto = bankCardService.updateDto(id, bankCardDto);

        return updatedBankCardDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        if (bankCardService.delete(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
