package com.gitlab.service;

import com.gitlab.dto.PersonalAddressDto;
import com.gitlab.mapper.PersonalAddressMapper;
import com.gitlab.model.PersonalAddress;
import com.gitlab.repository.PersonalAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PersonalAddressService implements Cloneable {

    private final PersonalAddressRepository personalAddressRepository;
    private final PersonalAddressMapper personalAddressMapper;

    @Transactional(readOnly = true)
    public List<PersonalAddress> findAll() {
        log.info("Fetching all personal addresses.");
        return personalAddressRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PersonalAddressDto> findAllDto() {
        log.info("Fetching all personal address DTOs.");
        List<PersonalAddress> personalAddresses = personalAddressRepository.findAll();
        return personalAddresses.stream()
                .map(personalAddressMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<PersonalAddress> findById(Long id) {
        log.info("Fetching personal address by id: {}", id);
        return personalAddressRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<PersonalAddressDto> findByIdDto(Long id) {
        log.info("Fetching personal address DTO by id: {}", id);
        return personalAddressRepository.findById(id)
                .map(personalAddressMapper::toDto);
    }

    public Page<PersonalAddress> getPage(Integer page, Integer size) {
        log.info("Fetching personal address page. Page: {}, Size: {}", page, size);
        if (page == null || size == null) {
            var personalAddresses = findAll();
            if (personalAddresses.isEmpty()) {
                log.warn("No personal addresses found.");
                return Page.empty();
            }
            return new PageImpl<>(personalAddresses);
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return personalAddressRepository.findAll(pageRequest);
    }

    public Page<PersonalAddressDto> getPageDto(Integer page, Integer size) {
        log.info("Fetching personal address DTO page. Page: {}, Size: {}", page, size);
        if (page == null || size == null) {
            var personalAddresses = findAllDto();
            if (personalAddresses.isEmpty()) {
                log.warn("No personal address DTOs found.");
                return Page.empty();
            }
            return new PageImpl<>(personalAddresses);
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PersonalAddress> personalAddressPage = personalAddressRepository.findAll(pageRequest);
        return personalAddressPage.map(personalAddressMapper::toDto);
    }

    public PersonalAddress save(PersonalAddress personalAddress) {
        log.info("Saving personal address: {}", personalAddress);
        return personalAddressRepository.save(personalAddress);
    }

    public PersonalAddressDto saveDto(PersonalAddressDto personalAddressDto) {
        log.info("Saving personal address DTO: {}", personalAddressDto);
        PersonalAddress personalAddress = personalAddressMapper.toEntity(personalAddressDto);
        return personalAddressMapper.toDto(personalAddressRepository.save(personalAddress));
    }

    public PersonalAddressDto update(Long id, PersonalAddressDto personalAddressDto) {
        log.info("Updating personal address with id: {}", id);
        Optional<PersonalAddress> optionalSavedAddress = personalAddressRepository.findById(id);
        if (optionalSavedAddress.isEmpty()) {
            log.warn("Personal address with id: {} not found.", id);
            throw new EntityNotFoundException("Адрес не найден");
        }

        PersonalAddress savedPersonalAddress = optionalSavedAddress.get();
        if (personalAddressDto.getDirections() != null) {
            savedPersonalAddress.setDirections(personalAddressDto.getDirections());
        }
        if (personalAddressDto.getDoorCode() != null) {
            savedPersonalAddress.setDoorCode(personalAddressDto.getDoorCode());
        }
        if (personalAddressDto.getPostCode() != null) {
            savedPersonalAddress.setPostCode(personalAddressDto.getPostCode());
        }
        if (personalAddressDto.getAddress() != null) {
            savedPersonalAddress.setAddress(personalAddressDto.getAddress());
        }
        if (personalAddressDto.getApartment() != null) {
            savedPersonalAddress.setApartment(personalAddressDto.getApartment());
        }
        if (personalAddressDto.getFloor() != null) {
            savedPersonalAddress.setFloor(personalAddressDto.getFloor());
        }
        if (personalAddressDto.getEntrance() != null) {
            savedPersonalAddress.setEntrance(personalAddressDto.getEntrance());
        }

        log.info("Updated personal address with id: {}", id);
        return personalAddressMapper.toDto(personalAddressRepository.save(savedPersonalAddress));
    }

    public Optional<PersonalAddress> delete(Long id) {
        log.info("Deleting personal address with id: {}", id);
        Optional<PersonalAddress> optionalSavedAddress = findById(id);
        if (optionalSavedAddress.isPresent()) {
            personalAddressRepository.deleteById(id);
            log.info("Personal address with id: {} deleted.", id);
        } else {
            log.warn("Personal address with id: {} not found.", id);
        }
        return optionalSavedAddress;
    }

    public Optional<PersonalAddressDto> deleteDto(Long id) {
        log.info("Deleting personal address DTO with id: {}", id);
        Optional<PersonalAddress> optionalSavedAddress = personalAddressRepository.findById(id);
        if (optionalSavedAddress.isPresent()) {
            personalAddressRepository.deleteById(id);
            log.info("Personal address DTO with id: {} deleted.", id);
        } else {
            log.warn("Personal address DTO with id: {} not found.", id);
        }
        return optionalSavedAddress.map(personalAddressMapper::toDto);
    }

    @Override
    public PersonalAddressService clone() {
        try {
            log.info("Cloning PersonalAddressService");
            return (PersonalAddressService) super.clone();
        } catch (CloneNotSupportedException e) {
            log.error("Cloning not supported", e);
            throw new AssertionError();
        }
    }
}
