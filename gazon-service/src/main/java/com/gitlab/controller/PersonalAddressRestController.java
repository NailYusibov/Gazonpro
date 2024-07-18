package com.gitlab.controller;

import com.gitlab.controllers.api.rest.PersonalAddressRestApi;
import com.gitlab.dto.PersonalAddressDto;
import com.gitlab.dto.ShippingAddressDto;
import com.gitlab.dto.UserDto;
import com.gitlab.mapper.ShippingAddressMapper;
import com.gitlab.service.PersonalAddressService;
import com.gitlab.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PersonalAddressRestController implements PersonalAddressRestApi {

    private final PersonalAddressService personalAddressService;
    private final UserService userService;


    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PersonalAddressDto>> getPage(Integer page, Integer size) {
        var personalAddressPage = personalAddressService.getPageDto(page, size);
        if (personalAddressPage == null || personalAddressPage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(personalAddressPage.getContent());
    }

    @Override
    public ResponseEntity<PersonalAddressDto> get(Long id) {
        Optional<UserDto> user = userService.getUserFromAuthentication();
        if (user.get().getRoles().contains("ROLE_ADMIN")){
            return personalAddressService.findByIdDto(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else if (shippingAddressId(user, id).equals(id)) {
            return personalAddressService.findByIdDto(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<PersonalAddressDto> create(PersonalAddressDto personalAddressDto) {
        PersonalAddressDto createdPersonalAddressDto = personalAddressService.saveDto(personalAddressDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPersonalAddressDto);
    }

    @Override
    public ResponseEntity<PersonalAddressDto> update(Long id, PersonalAddressDto personalAddressDto) {
        Optional<UserDto> user = userService.getUserFromAuthentication();
        if (user.get().getRoles().contains("ROLE_ADMIN")){
            return ResponseEntity.ok(personalAddressService.update(id, personalAddressDto));
        } else if (shippingAddressId(user, id).equals(id)) {
            return ResponseEntity.ok(personalAddressService.update(user.get().getId(), personalAddressDto));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        Optional<UserDto> user = userService.getUserFromAuthentication();
        if (user.get().getRoles().contains("ROLE_ADMIN")){
            Optional<PersonalAddressDto> deletedPersonalAddressDto = personalAddressService.deleteDto(id);
            if (deletedPersonalAddressDto.isPresent()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else if (shippingAddressId(user, id).equals(id)) {
            Optional<PersonalAddressDto> deletedPersonalAddressDto = personalAddressService.deleteDto(id);
            if (deletedPersonalAddressDto.isPresent()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
    }

    private Long shippingAddressId (Optional<UserDto> user, Long id){
        return user.get().getShippingAddressDtos()
                .stream()
                .map(ShippingAddressDto::getId)
                .filter(saId -> Objects.equals(saId, id))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("The address does not belong to the user.(Адрес не принадлежит пользователю)."));
    }

}