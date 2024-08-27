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
        log.info("getPage: Received GET request with page: {} and size: {}", page, size);
        var userPage = userService.getPageDto(page, size);
        if (userPage == null || userPage.getContent().isEmpty()) {
            log.warn("getPage: Page is empty");
            return ResponseEntity.noContent().build();
        }
        log.info("getPage: Returning {} users for page number: {}, page size: {}", userPage.getContent().size(), page, size);
        return ResponseEntity.ok(userPage.getContent());
    }

    @Override
    public ResponseEntity<UserDto> get(Long id) {
        log.info("get: Received GET request with id: {}", id);
        Optional<UserDto> optionalUser = userService.findById(id);

        log.info("get: Returning user with id: {}", id);
        return optionalUser
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<UserDto> create(UserDto userDto) {
        log.info("create: Received POST request with user: {}", userDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.saveDto(userDto));
    }

    @Override
    public ResponseEntity<UserDto> update(Long id, UserDto userDto) {
        log.info("update: Received PUT request with id: {} and user: {}", id, userDto);
        Optional<UserDto> updatedUser = userService.updateDto(id, userDto);

        log.info("update: Returning user with id: {}", id);
        return updatedUser
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Override
    public ResponseEntity<Void> delete(Long id) {
        log.info("delete: Received DELETE request with id: {}", id);
        Optional<User> user = userService.delete(id);
        if (user.isEmpty()) {
            log.warn("delete: User with id: {} not found", id);
            return ResponseEntity.notFound().build();
        } else {
            log.info("delete: User with id: {} deleted", id);
            return ResponseEntity.ok().build();
        }
    }
}
