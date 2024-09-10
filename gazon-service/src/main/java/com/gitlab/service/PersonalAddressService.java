package com.gitlab.service;

import com.gitlab.dto.PersonalAddressDto;
import com.gitlab.mapper.PersonalAddressMapper;
import com.gitlab.model.PersonalAddress;
import com.gitlab.repository.PersonalAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonalAddressService implements Cloneable {

    private final PersonalAddressRepository personalAddressRepository;

    private final PersonalAddressMapper personalAddressMapper;

    @Transactional(readOnly = true)
    public List<PersonalAddress> findAll() {
        return personalAddressRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PersonalAddressDto> findAllDto() {
        List<PersonalAddress> personalAddresses = personalAddressRepository.findAll();
        return personalAddresses.stream()
                .map(personalAddressMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<PersonalAddress> findById(Long id) {
        return personalAddressRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<PersonalAddressDto> findByIdDto(Long id) {
        return personalAddressRepository.findById(id)
                .map(personalAddressMapper::toDto);
    }

    public Page<PersonalAddress> getPage(Integer page, Integer size) {
        if (page == null || size == null) {
            var personalAddress = findAll();
            if (personalAddress.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(personalAddress);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return personalAddressRepository.findAll(pageRequest);
    }

    public Page<PersonalAddressDto> getPageDto(Integer page, Integer size) {

        if (page == null || size == null) {
            var personalAddress = findAllDto();
            if (personalAddress.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(personalAddress);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PersonalAddress> personalAddressPage = personalAddressRepository.findAll(pageRequest);
        return personalAddressPage.map(personalAddressMapper::toDto);
    }

    public PersonalAddress save(PersonalAddress personalAddress) {
        return personalAddressRepository.save(personalAddress);
    }

    public PersonalAddressDto saveDto(PersonalAddressDto personalAddressDto) {
        PersonalAddress personalAddress = personalAddressMapper.toEntity(personalAddressDto);
        return personalAddressMapper.toDto(personalAddressRepository.save(personalAddress));
    }

    public PersonalAddressDto update(Long id, PersonalAddressDto personalAddressDto) {
        Optional<PersonalAddress> optionalSavedAddress = personalAddressRepository.findById(id);
        if (optionalSavedAddress.isEmpty()) {
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

        return personalAddressMapper.toDto(personalAddressRepository.save(savedPersonalAddress));
    }


    public Optional<PersonalAddress> delete(Long id) {
        Optional<PersonalAddress> optionalSavedAddress = findById(id);
        if (optionalSavedAddress.isPresent()) {
            personalAddressRepository.deleteById(id);
        }
        return optionalSavedAddress;
    }

    public Optional<PersonalAddressDto> deleteDto(Long id) {
        Optional<PersonalAddress> optionalSavedAddress = personalAddressRepository.findById(id);

        if (optionalSavedAddress.isPresent()) {
            personalAddressRepository.deleteById(id);
        }

        return optionalSavedAddress.map(personalAddressMapper::toDto);
    }

    @Override
    public PersonalAddressService clone() {
        try {
            return (PersonalAddressService) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}