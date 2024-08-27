package com.gitlab.controller;

import com.gitlab.controllers.api.rest.ExampleRestApi;
import com.gitlab.dto.ExampleDto;
import com.gitlab.model.Example;
import com.gitlab.service.ExampleService;
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
public class ExampleRestController implements ExampleRestApi {

    private final ExampleService exampleService;

    public ExampleRestController(ExampleService exampleService) {
        this.exampleService = exampleService.clone();
    }

    @Override
    public ResponseEntity<List<ExampleDto>> getPage(Integer page, Integer size) {
        var examplePage = exampleService.getPageDto(page, size);
        if (examplePage == null || examplePage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(examplePage.getContent());
    }

    @Override
    public ResponseEntity<ExampleDto> get(Long id) {
        return exampleService.findByIdDto(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ExampleDto> create(ExampleDto exampleDto) {
        ExampleDto savedExampleDto = exampleService.saveDto(exampleDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedExampleDto);
    }

    @Override
    public ResponseEntity<ExampleDto> update(Long id, ExampleDto exampleDto) {
        Optional<ExampleDto> updatedExampleDto = exampleService.updateDto(id, exampleDto);
        return updatedExampleDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        Optional<Example> example = exampleService.delete(id);
        if (example.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
}