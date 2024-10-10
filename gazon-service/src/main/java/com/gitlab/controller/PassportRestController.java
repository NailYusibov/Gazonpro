package com.gitlab.controller;

import com.gitlab.controllers.api.rest.PassportRestApi;
import com.gitlab.dto.PassportDto;
import com.gitlab.service.PassportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
public class PassportRestController implements PassportRestApi {

    private final PassportService passportService;

    public PassportRestController(PassportService passportService) {
        this.passportService = passportService.clone();
    }

    public ResponseEntity<List<PassportDto>> getPage(Integer page, Integer size) {
        log.info("Request to get passport page - page: {}, size: {}", page, size);
        var passportPage = passportService.getPageDto(page, size);
        if (passportPage == null || passportPage.getContent().isEmpty()) {
            log.warn("No content found for page: {}, size: {}", page, size);
            return ResponseEntity.noContent().build();
        }
        log.info("Returning passport page with {} records", passportPage.getContent().size());
        return ResponseEntity.ok(passportPage.getContent());
    }

    @Override
    public ResponseEntity<PassportDto> get(Long id) {
        log.info("Request to get passport with id: {}", id);
        return passportService.findByIdDto(id)
                .map(passport -> {
                    log.info("Passport found with id: {}", id);
                    return ResponseEntity.ok(passport);
                })
                .orElseGet(() -> {
                    log.warn("Passport not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    public ResponseEntity<PassportDto> create(PassportDto passportDto) {
        log.info("Request to create passport with data: {}", passportDto);
        PassportDto savedPassportDto = passportService.saveDto(passportDto);
        log.info("Passport created with id: {}", savedPassportDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedPassportDto);
    }

    @Override
    public ResponseEntity<PassportDto> update(Long id, PassportDto passportDto) {
        log.info("Request to update passport with id: {}", id);
        Optional<PassportDto> updatedPassportDto = passportService.updateDto(id, passportDto);

        return updatedPassportDto
                .map(updatedPassport -> {
                    log.info("Passport updated with id: {}", id);
                    return ResponseEntity.ok(updatedPassport);
                })
                .orElseGet(() -> {
                    log.warn("Passport not found for update with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        log.info("Request to delete passport with id: {}", id);
        Optional<PassportDto> deletedPassportDto = passportService.deleteDto(id);

        if (deletedPassportDto.isPresent()) {
            log.info("Passport deleted with id: {}", id);
            return ResponseEntity.ok().build();
        } else {
            log.warn("Passport not found for deletion with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
