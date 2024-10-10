package com.gitlab.service;

import com.gitlab.dto.ExampleDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.ExampleMapper;
import com.gitlab.model.Example;
import com.gitlab.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExampleService implements Cloneable {

    private final ExampleRepository exampleRepository;
    private final ExampleMapper exampleMapper;

    public List<Example> findAll() {
        log.info("Вызван метод findAll");
        return exampleRepository.findAll();
    }

    public List<ExampleDto> findAllDto() {
        log.info("Вызван метод findAllDto");
        List<ExampleDto> result = findAll()
                .stream()
                .map(exampleMapper::toDto)
                .collect(Collectors.toList());
        log.info("findAllDto: найдено {} элементов", result.size());
        return result;
    }

    public Optional<Example> findById(Long id) {
        log.info("Вызван метод findById с id={}", id);
        return exampleRepository.findById(id);
    }

    public Optional<ExampleDto> findByIdDto(Long id) {
        log.info("Вызван метод findByIdDto с id={}", id);
        Optional<Example> optionalExample = findById(id);
        if (optionalExample.isPresent() && optionalExample.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            log.warn("findByIdDto: элемент с id={} помечен как удаленный", id);
            return Optional.empty();
        }
        return optionalExample.map(exampleMapper::toDto);
    }

    public Page<Example> getPage(Integer page, Integer size) {
        log.info("Вызван метод getPage с page={}, size={}", page, size);
        if (page == null || size == null) {
            log.warn("getPage: переданы некорректные параметры");
            var examples = findAll();
            return examples.isEmpty() ? Page.empty() : new PageImpl<>(examples);
        }
        if (page < 0 || size < 1) {
            log.warn("getPage: page или size имеют недопустимые значения");
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Example> examplePage = exampleRepository.findAll(pageRequest);
        log.info("getPage: возвращена страница с {} элементами", examplePage.getTotalElements());
        return examplePage;
    }

    public Page<ExampleDto> getPageDto(Integer page, Integer size) {
        log.info("Вызван метод getPageDto с page={}, size={}", page, size);
        Page<Example> examplePage = getPage(page, size);
        return examplePage.map(exampleMapper::toDto);
    }

    public Example save(Example example) {
        log.info("Вызван метод save с Example: {}", example);
        example.setEntityStatus(EntityStatus.ACTIVE);
        Example savedExample = exampleRepository.save(example);
        log.info("save: элемент сохранен с id={}", savedExample.getId());
        return savedExample;
    }

    public ExampleDto saveDto(ExampleDto exampleDto) {
        log.info("Вызван метод saveDto с ExampleDto: {}", exampleDto);
        Example example = exampleMapper.toEntity(exampleDto);
        example.setEntityStatus(EntityStatus.ACTIVE);
        Example savedExample = save(example);
        return exampleMapper.toDto(savedExample);
    }

    public Optional<Example> update(Long id, Example example) {
        log.info("Вызван метод update с id={} и Example: {}", id, example);
        Optional<Example> optionalSavedExample = findById(id);
        if (optionalSavedExample.isEmpty()) {
            log.warn("update: элемент с id={} не найден", id);
            return Optional.empty();
        }

        Example savedExample = optionalSavedExample.get();
        if (example.getExampleText() != null) {
            savedExample.setExampleText(example.getExampleText());
        }

        savedExample.setEntityStatus(EntityStatus.ACTIVE);
        savedExample = exampleRepository.save(savedExample);
        log.info("update: элемент с id={} обновлен", savedExample.getId());
        return Optional.of(savedExample);
    }

    @Transactional
    public Optional<ExampleDto> updateDto(Long id, ExampleDto exampleDto) {
        log.info("Вызван метод updateDto с id={} и ExampleDto: {}", id, exampleDto);
        Optional<Example> optionalSavedExample = findById(id);
        if (optionalSavedExample.isEmpty()) {
            log.warn("updateDto: элемент с id={} не найден", id);
            return Optional.empty();
        }

        Example savedExample = optionalSavedExample.get();
        if (exampleDto.getExampleText() != null) {
            savedExample.setExampleText(exampleDto.getExampleText());
        }

        savedExample.setEntityStatus(EntityStatus.ACTIVE);
        savedExample = exampleRepository.save(savedExample);
        log.info("updateDto: элемент с id={} обновлен", savedExample.getId());
        return Optional.of(exampleMapper.toDto(savedExample));
    }

    @Transactional
    public Optional<Example> delete(Long id) {
        log.info("Вызван метод delete с id={}", id);
        Optional<Example> optionalDeletedExample = findById(id);
        if (optionalDeletedExample.isEmpty() || optionalDeletedExample.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            log.warn("delete: элемент с id={} не найден или уже удален", id);
            return Optional.empty();
        }

        Example deletedExample = optionalDeletedExample.get();
        deletedExample.setEntityStatus(EntityStatus.DELETED);
        exampleRepository.save(deletedExample);
        log.info("delete: элемент с id={} удален", deletedExample.getId());
        return Optional.of(deletedExample);
    }

    @Override
    public ExampleService clone() {
        try {
            return (ExampleService) super.clone();
        } catch (CloneNotSupportedException e) {
            log.error("Ошибка клонирования ExampleService", e);
            throw new AssertionError();
        }
    }
}
