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
        log.info("ExampleRestController создан с ExampleService");
    }

    @Override
    public ResponseEntity<List<ExampleDto>> getPage(Integer page, Integer size) {
        log.info("Вызван метод getPage с page={}, size={}", page, size);
        var examplePage = exampleService.getPageDto(page, size);
        if (examplePage == null || examplePage.getContent().isEmpty()) {
            log.warn("getPage: страница пуста");
            return ResponseEntity.noContent().build();
        }
        log.info("getPage: возвращено {} элементов", examplePage.getTotalElements());
        return ResponseEntity.ok(examplePage.getContent());
    }

    @Override
    public ResponseEntity<ExampleDto> get(Long id) {
        log.info("Вызван метод get с id={}", id);
        return exampleService.findByIdDto(id)
                .map(dto -> {
                    log.info("get: найден элемент с id={}", id);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("get: элемент с id={} не найден", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    public ResponseEntity<ExampleDto> create(ExampleDto exampleDto) {
        log.info("Вызван метод create с ExampleDto: {}", exampleDto);
        ExampleDto savedExampleDto = exampleService.saveDto(exampleDto);
        log.info("create: элемент создан с id={}", savedExampleDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedExampleDto);
    }

    @Override
    public ResponseEntity<ExampleDto> update(Long id, ExampleDto exampleDto) {
        log.info("Вызван метод update с id={} и ExampleDto: {}", id, exampleDto);
        Optional<ExampleDto> updatedExampleDto = exampleService.updateDto(id, exampleDto);
        return updatedExampleDto
                .map(dto -> {
                    log.info("update: элемент с id={} обновлен", id);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("update: элемент с id={} не найден", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        log.info("Вызван метод delete с id={}", id);
        Optional<Example> example = exampleService.delete(id);
        if (example.isEmpty()) {
            log.warn("delete: элемент с id={} не найден", id);
            return ResponseEntity.notFound().build();
        } else {
            log.info("delete: элемент с id={} удален", id);
            return ResponseEntity.ok().build();
        }
    }
}