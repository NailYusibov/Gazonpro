package com.gitlab.controller;

import com.gitlab.controller.api.PassportRestApi;
import com.gitlab.dto.PassportDto;
import com.gitlab.service.PassportService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PassportRestController implements PassportRestApi {

    private final PassportService passportService;

    @Override
    public ResponseEntity<List<PassportDto>> getAll() {
        List<PassportDto> passportDtos = passportService.findAllDto();

        if (passportDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(passportDtos);
        }
    }

    @Override
    public ResponseEntity<PassportDto> get(Long id) {
        return passportService.findByIdDto(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<PassportDto> create(PassportDto passportDto) {
        PassportDto savedPassportDto = passportService.saveDto(passportDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedPassportDto);
    }

    @Override
    public ResponseEntity<PassportDto> update(Long id, PassportDto passportDto) {
        Optional<PassportDto> updatedPassportDto = passportService.updateDto(id, passportDto);

        return updatedPassportDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        Optional<PassportDto> deletedPassportDto = passportService.deleteDto(id);

        if (deletedPassportDto.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}