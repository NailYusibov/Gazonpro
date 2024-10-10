package com.gitlab.service;

import com.gitlab.dto.PassportDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.PassportMapper;
import com.gitlab.model.Passport;
import com.gitlab.repository.PassportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PassportService implements Cloneable {

    private final PassportRepository passportRepository;
    private final PassportMapper passportMapper;

    @Transactional(readOnly = true)
    public List<Passport> findAllActive() {
        log.info("Fetching all active passports");
        return passportRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PassportDto> findAllActiveDto() {
        log.info("Fetching all active passports as DTO");
        return passportMapper.toDtoList(passportRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<Passport> findById(Long id) {
        log.info("Fetching passport by id: {}", id);
        return passportRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<PassportDto> findByIdDto(Long id) {
        log.info("Fetching passport DTO by id: {}", id);
        Optional<Passport> passportOptional = passportRepository.findById(id);
        if (passportOptional.isPresent()) {
            log.info("Passport found by id: {}", id);
            return passportOptional.map(passportMapper::toDto);
        }
        log.warn("Passport not found by id: {}", id);
        return Optional.empty();
    }

    public Page<Passport> getPage(Integer page, Integer size) {
        log.info("Fetching passport page - page: {}, size: {}", page, size);
        if (page == null || size == null) {
            var passports = findAllActive();
            if (passports.isEmpty()) {
                log.warn("No passports found");
                return Page.empty();
            }
            return new PageImpl<>(passports);
        }
        if (page < 0 || size < 1) {
            log.warn("Invalid page or size - page: {}, size: {}", page, size);
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return passportRepository.findAll(pageRequest);
    }

    public Page<PassportDto> getPageDto(Integer page, Integer size) {
        log.info("Fetching passport DTO page - page: {}, size: {}", page, size);
        if (page == null || size == null) {
            var passports = findAllActiveDto();
            if (passports.isEmpty()) {
                log.warn("No passport DTOs found");
                return Page.empty();
            }
            return new PageImpl<>(passports);
        }
        if (page < 0 || size < 1) {
            log.warn("Invalid page or size for DTO - page: {}, size: {}", page, size);
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Passport> passportPage = passportRepository.findAll(pageRequest);
        return passportPage.map(passportMapper::toDto);
    }

    public Passport save(Passport passport) {
        log.info("Saving passport: {}", passport);
        passport.setEntityStatus(EntityStatus.ACTIVE);
        return passportRepository.save(passport);
    }

    public PassportDto saveDto(PassportDto passportDto) {
        log.info("Saving passport DTO: {}", passportDto);
        Passport passport = passportMapper.toEntity(passportDto);
        passport.setEntityStatus(EntityStatus.ACTIVE);
        Passport savedPassport = passportRepository.save(passport);
        log.info("Passport saved with id: {}", savedPassport.getId());
        return passportMapper.toDto(savedPassport);
    }

    public Optional<Passport> update(Long id, Passport passport) {
        log.info("Updating passport with id: {}", id);
        Optional<Passport> optionalSavedPassport = passportRepository.findById(id);
        if (optionalSavedPassport.isEmpty()) {
            log.warn("Passport not found for update with id: {}", id);
            return Optional.empty();
        }

        Passport savedPassport = optionalSavedPassport.get();
        // Update logic omitted for brevity

        savedPassport.setEntityStatus(EntityStatus.ACTIVE);
        log.info("Passport updated with id: {}", savedPassport.getId());
        return Optional.of(passportRepository.save(savedPassport));
    }

    public Optional<PassportDto> updateDto(Long id, PassportDto passportDto) {
        log.info("Updating passport DTO with id: {}", id);
        Optional<Passport> optionalSavedPassport = passportRepository.findById(id);
        if (optionalSavedPassport.isEmpty()) {
            log.warn("Passport DTO not found for update with id: {}", id);
            return Optional.empty();
        }

        Passport savedPassport = optionalSavedPassport.get();
        // Update logic omitted for brevity

        savedPassport.setEntityStatus(EntityStatus.ACTIVE);
        Passport updatedPassport = passportRepository.save(savedPassport);
        log.info("Passport DTO updated with id: {}", updatedPassport.getId());
        return Optional.ofNullable(passportMapper.toDto(updatedPassport));
    }

    public Optional<Passport> delete(Long id) {
        log.info("Deleting passport with id: {}", id);
        Optional<Passport> optionalDeletedPassport = passportRepository.findById(id);
        if (optionalDeletedPassport.isEmpty()) {
            log.warn("Passport not found for deletion with id: {}", id);
            return Optional.empty();
        }
        Passport deletedPassport = optionalDeletedPassport.get();
        deletedPassport.setEntityStatus(EntityStatus.DELETED);
        passportRepository.save(deletedPassport);
        log.info("Passport marked as deleted with id: {}", deletedPassport.getId());
        return optionalDeletedPassport;
    }

    public Optional<PassportDto> deleteDto(Long id) {
        log.info("Deleting passport DTO with id: {}", id);
        Optional<Passport> optionalDeletedPassport = passportRepository.findById(id);
        if (optionalDeletedPassport.isEmpty()) {
            log.warn("Passport DTO not found for deletion with id: {}", id);
            return Optional.empty();
        }
        Passport deletedPassport = optionalDeletedPassport.get();
        deletedPassport.setEntityStatus(EntityStatus.DELETED);
        passportRepository.save(deletedPassport);
        log.info("Passport DTO marked as deleted with id: {}", deletedPassport.getId());
        return Optional.ofNullable(passportMapper.toDto(optionalDeletedPassport.get()));
    }

    @Override
    public PassportService clone() {
        try {
            return (PassportService) super.clone();
        } catch (CloneNotSupportedException e) {
            log.error("Error cloning PassportService", e);
            throw new AssertionError();
        }
    }
}
