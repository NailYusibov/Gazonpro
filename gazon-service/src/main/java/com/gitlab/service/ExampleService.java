package com.gitlab.service;

import com.gitlab.dto.ExampleDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.ExampleMapper;
import com.gitlab.model.Example;
import com.gitlab.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
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
public class ExampleService implements Cloneable{

    private final ExampleRepository exampleRepository;

    private final ExampleMapper exampleMapper;

    public List<Example> findAll() {
        return exampleRepository.findAll();
    }

    public List<ExampleDto> findAllDto() {
        return findAll()
                .stream()
                .map(exampleMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<Example> findById(Long id) {
        return exampleRepository.findById(id);
    }

    public Optional<ExampleDto> findByIdDto(Long id) {
        Optional<Example> optionalExample = exampleRepository.findById(id);
        if (optionalExample.isPresent() && optionalExample.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            return Optional.empty();
        }
        return optionalExample.map(exampleMapper::toDto);
    }

    public Page<Example> getPage(Integer page, Integer size) {
        if (page == null || size == null) {
            var examples = findAll();
            if (examples.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(examples);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return exampleRepository.findAll(pageRequest);
    }

    public Page<ExampleDto> getPageDto(Integer page, Integer size) {
        if (page == null || size == null) {
            var examples = findAllDto();
            if (examples.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(examples);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Example> examplePage = exampleRepository.findAll(pageRequest);
        return examplePage.map(exampleMapper::toDto);
    }

    public Example save(Example example) {
        example.setEntityStatus(EntityStatus.ACTIVE);
        return exampleRepository.save(example);
    }

    public ExampleDto saveDto(ExampleDto exampleDto) {
        Example example = exampleMapper.toEntity(exampleDto);

        example.setEntityStatus(EntityStatus.ACTIVE);

        Example savedExample = exampleRepository.save(example);
        return exampleMapper.toDto(savedExample);
    }

    public Optional<Example> update(Long id, Example example) {
        Optional<Example> optionalSavedExample = findById(id);
        Example savedExample;
        if (optionalSavedExample.isEmpty()) {
            return optionalSavedExample;
        } else {
            savedExample = optionalSavedExample.get();
        }
        if (example.getExampleText() != null) {
            savedExample.setExampleText(example.getExampleText());
        }

        savedExample.setEntityStatus(EntityStatus.ACTIVE);

        return Optional.of(exampleRepository.save(savedExample));
    }

    @Transactional
    public Optional<ExampleDto> updateDto(Long id, ExampleDto exampleDto) {
        Optional<Example> optionalSavedExample = findById(id);
        if (optionalSavedExample.isEmpty()) {
            return Optional.empty();
        }

        Example savedExample = optionalSavedExample.get();
        if (exampleDto.getExampleText() != null) {
            savedExample.setExampleText(exampleDto.getExampleText());
        }

        savedExample.setEntityStatus(EntityStatus.ACTIVE);

        savedExample = exampleRepository.save(savedExample);
        return Optional.of(exampleMapper.toDto(savedExample));
    }

    @Transactional
    public Optional<Example> delete(Long id) {
        Optional<Example> optionalDeletedExample = exampleRepository.findById(id);
        if (optionalDeletedExample.isEmpty() || optionalDeletedExample.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            return Optional.empty();
        }

        Example deletedExample = optionalDeletedExample.get();
        deletedExample.setEntityStatus(EntityStatus.DELETED);
        exampleRepository.save(deletedExample);

        return optionalDeletedExample;
    }

    @Override
    public ExampleService clone() {
        try {
            return (ExampleService) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}