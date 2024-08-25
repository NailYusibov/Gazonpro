package com.gitlab.service;

import com.gitlab.dto.PassportDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.PassportMapper;
import com.gitlab.model.Passport;
import com.gitlab.repository.PassportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PassportService implements Cloneable{

    private final PassportRepository passportRepository;

    private final PassportMapper passportMapper;

    @Transactional(readOnly = true)
    public List<Passport> findAllActive() {
        return passportRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PassportDto> findAllActiveDto() {
        return passportMapper.toDtoList(passportRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<Passport> findById(Long id) {
        return passportRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<PassportDto> findByIdDto(Long id) {
        Optional<Passport> passportOptional = passportRepository.findById(id);
        if (passportOptional.isPresent()) {
            return passportOptional.map(passportMapper::toDto);
        }
        return Optional.empty();
    }

    public Page<Passport> getPage(Integer page, Integer size) {
        if (page == null || size == null) {
            var passports = findAllActive();
            if (passports.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(passports);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return passportRepository.findAll(pageRequest);
    }

    public Page<PassportDto> getPageDto(Integer page, Integer size) {

        if (page == null || size == null) {
            var passports = findAllActiveDto();
            if (passports.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(passports);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Passport> passportPage = passportRepository.findAll(pageRequest);
        return passportPage.map(passportMapper::toDto);
    }

    public Passport save(Passport passport) {
        passport.setEntityStatus(EntityStatus.ACTIVE);
        return passportRepository.save(passport);
    }

    public PassportDto saveDto(PassportDto passportDto) {
        Passport passport = passportMapper.toEntity(passportDto);
        passport.setEntityStatus(EntityStatus.ACTIVE);
        Passport savedPassport = passportRepository.save(passport);
        return passportMapper.toDto(savedPassport);
    }

    public Optional<Passport> update(Long id, Passport passport) {
        Optional<Passport> optionalSavedPassport = passportRepository.findById(id);
        if (optionalSavedPassport.isEmpty()) {
            return Optional.empty();
        }

        Passport savedPassport = optionalSavedPassport.get();

        if (passport.getCitizenship() != null) {
            savedPassport.setCitizenship(passport.getCitizenship());
        }
        if (passport.getFirstName() != null) {
            savedPassport.setFirstName(passport.getFirstName());
        }
        if (passport.getLastName() != null) {
            savedPassport.setLastName(passport.getLastName());
        }
        if (passport.getPatronym() != null) {
            savedPassport.setPatronym(passport.getPatronym());
        }
        if (passport.getBirthDate() != null) {
            savedPassport.setBirthDate(passport.getBirthDate());
        }
        if (passport.getIssueDate() != null) {
            savedPassport.setIssueDate(passport.getIssueDate());
        }
        if (passport.getPassportNumber() != null) {
            savedPassport.setPassportNumber(passport.getPassportNumber());
        }
        if (passport.getIssuer() != null) {
            savedPassport.setIssuer(passport.getIssuer());
        }
        if (passport.getIssuerNumber() != null) {
            savedPassport.setIssuerNumber(passport.getIssuerNumber());
        }

        savedPassport.setEntityStatus(EntityStatus.ACTIVE);

        return Optional.of(passportRepository.save(savedPassport));
    }

    public Optional<PassportDto> updateDto(Long id, PassportDto passportDto) {
        Optional<Passport> optionalSavedPassport = passportRepository.findById(id);
        if (optionalSavedPassport.isEmpty()) {
            return Optional.empty();
        }

        Passport savedPassport = optionalSavedPassport.get();

        if (passportDto.getCitizenship() != null) {
            savedPassport.setCitizenship(passportDto.getCitizenship());
        }
        if (passportDto.getFirstName() != null) {
            savedPassport.setFirstName(passportDto.getFirstName());
        }
        if (passportDto.getLastName() != null) {
            savedPassport.setLastName(passportDto.getLastName());
        }
        if (passportDto.getPatronym() != null) {
            savedPassport.setPatronym(passportDto.getPatronym());
        }
        if (passportDto.getBirthDate() != null) {
            savedPassport.setBirthDate(passportDto.getBirthDate());
        }
        if (passportDto.getIssueDate() != null) {
            savedPassport.setIssueDate(passportDto.getIssueDate());
        }
        if (passportDto.getPassportNumber() != null) {
            savedPassport.setPassportNumber(passportDto.getPassportNumber());
        }
        if (passportDto.getIssuer() != null) {
            savedPassport.setIssuer(passportDto.getIssuer());
        }
        if (passportDto.getIssuerNumber() != null) {
            savedPassport.setIssuerNumber(passportDto.getIssuerNumber());
        }

        savedPassport.setEntityStatus(EntityStatus.ACTIVE);

        Passport updatedPassport = passportRepository.save(savedPassport);
        return Optional.ofNullable(passportMapper.toDto(updatedPassport));
    }

    public Optional<Passport> delete(Long id) {
        Optional<Passport> optionalDeletedPassport = passportRepository.findById(id);
        if (optionalDeletedPassport.isEmpty()) {
            return Optional.empty();
        }
        Passport deletedPassport = optionalDeletedPassport.get();
        deletedPassport.setEntityStatus(EntityStatus.DELETED);
        passportRepository.save(deletedPassport);
        return optionalDeletedPassport;

    }

    public Optional<PassportDto> deleteDto(Long id) {
        Optional<Passport> optionalDeletedPassport = passportRepository.findById(id);
        if (optionalDeletedPassport.isEmpty()) {
            return Optional.empty();
        }
        Passport deletedPassport = optionalDeletedPassport.get();
        deletedPassport.setEntityStatus(EntityStatus.DELETED);
        passportRepository.save(deletedPassport);
        return Optional.ofNullable(passportMapper.toDto(optionalDeletedPassport.get()));
    }

    @Override
    public PassportService clone() {
        try {
            return (PassportService) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}