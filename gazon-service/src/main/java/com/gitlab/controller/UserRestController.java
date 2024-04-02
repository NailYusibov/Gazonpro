package com.gitlab.controller;

import com.gitlab.controllers.api.rest.UserRestApi;
import com.gitlab.dto.UserDto;
import com.gitlab.model.User;
import com.gitlab.service.UserService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserRestController implements UserRestApi {

    private final UserService userService;

    public ResponseEntity<List<UserDto>> getPage(Integer page, Integer size) {
        var userPage = userService.getPageDto(page, size);
        if (userPage == null || userPage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userPage.getContent());
    }

    @Override
    public ResponseEntity<UserDto> get(Long id) {
        Optional<UserDto> optionalUser = userService.findById(id);

        return optionalUser
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<UserDto> create(UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.saveDto(userDto));
    }

    @Override
    public ResponseEntity<UserDto> update(Long id, UserDto userDto) {
        Optional<UserDto> updatedUser = userService.updateDto(id, userDto);

        return updatedUser
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Override
    public ResponseEntity<Void> delete(Long id) {
        Optional<User> user = userService.delete(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
}
