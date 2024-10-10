package com.gitlab.controller;

import com.gitlab.controllers.api.rest.PersonalAddressRestApi;
import com.gitlab.dto.PersonalAddressDto;
import com.gitlab.model.ShippingAddress;
import com.gitlab.model.User;
import com.gitlab.service.PersonalAddressService;
import com.gitlab.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.gitlab.util.UserUtils.isAdmin;

@Slf4j
@Validated
@RestController
public class PersonalAddressRestController implements PersonalAddressRestApi {

    private final PersonalAddressService personalAddressService;
    private final UserService userService;

    public PersonalAddressRestController(PersonalAddressService personalAddressService, UserService userService) {
        this.personalAddressService = personalAddressService.clone();
        this.userService = userService.clone();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PersonalAddressDto>> getPage(Integer page, Integer size) {
        log.info("Fetching personal address page. Page: {}, Size: {}", page, size);
        var personalAddressPage = personalAddressService.getPageDto(page, size);
        if (personalAddressPage == null || personalAddressPage.getContent().isEmpty()) {
            log.warn("No personal addresses found for page: {}, size: {}", page, size);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(personalAddressPage.getContent());
    }

    @Override
    public ResponseEntity<PersonalAddressDto> get(Long personalAddressId) {
        log.info("Fetching personal address with id: {}", personalAddressId);
        User user = userService.getAuthenticatedUser();

        if (isAdmin(user)) {
            return personalAddressService.findByIdDto(personalAddressId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        if (isAuthorized(user, personalAddressId)) {
            return personalAddressService.findByIdDto(personalAddressId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else {
            log.warn("User is not authorized to access personal address with id: {}", personalAddressId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Override
    public ResponseEntity<PersonalAddressDto> create(PersonalAddressDto personalAddressDto) {
        log.info("Creating new personal address: {}", personalAddressDto);
        PersonalAddressDto createdPersonalAddressDto = personalAddressService.saveDto(personalAddressDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPersonalAddressDto);
    }

    @Override
    public ResponseEntity<PersonalAddressDto> update(Long personalAddressId, PersonalAddressDto personalAddressDto) {
        log.info("Updating personal address with id: {}", personalAddressId);
        User user = userService.getAuthenticatedUser();

        if (isAdmin(user) || isAuthorized(user, personalAddressId)) {
            Optional<PersonalAddressDto> updatedAddressDto = Optional
                    .ofNullable(personalAddressService.update(personalAddressId, personalAddressDto));
            return updatedAddressDto.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            log.warn("User is not authorized to update personal address with id: {}", personalAddressId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        log.info("Deleting personal address with id: {}", id);
        User user = userService.getAuthenticatedUser();

        if (isAdmin(user)) {
            return (personalAddressService.deleteDto(id).isPresent())
                    ? ResponseEntity.ok().build()
                    : ResponseEntity.notFound().build();
        }

        if (isAuthorized(user, id)) {
            return (personalAddressService.deleteDto(id).isPresent())
                    ? ResponseEntity.ok().build()
                    : ResponseEntity.notFound().build();
        } else {
            log.warn("User is not authorized to delete personal address with id: {}", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private boolean isAuthorized(User user, Long requestedPersonalAddressId) {
        log.info("Checking authorization for user: {} on personal address id: {}", user.getUsername(), requestedPersonalAddressId);
        return user.getShippingAddressSet()
                .stream()
                .map(ShippingAddress::getId)
                .anyMatch(cardId -> Objects.equals(cardId, requestedPersonalAddressId));
    }
}
