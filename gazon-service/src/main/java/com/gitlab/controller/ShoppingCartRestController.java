package com.gitlab.controller;


import com.gitlab.controllers.api.rest.ShoppingCartRestApi;
import com.gitlab.dto.ShoppingCartDto;
import com.gitlab.model.ShoppingCart;
import com.gitlab.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartRestController implements ShoppingCartRestApi {

    private final ShoppingCartService shoppingCartService;

    public ResponseEntity<List<ShoppingCartDto>> getPage(Integer page, Integer size) {
        var shoppingCartPage = shoppingCartService.getPageDto(page, size);
        if (shoppingCartPage == null || shoppingCartPage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(shoppingCartPage.getContent());
    }

    @Override
    public ResponseEntity<ShoppingCartDto> get(Long id) {
        Optional<ShoppingCartDto> shoppingCartOptional = shoppingCartService.findByIdDto(id);

        return shoppingCartOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ShoppingCartDto> create(ShoppingCartDto shoppingCartDto) {
        ShoppingCartDto savedShoppingCartDto = shoppingCartService.saveDto(shoppingCartDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShoppingCartDto);
    }

    @Override
    public ResponseEntity<ShoppingCartDto> update(Long id, ShoppingCartDto shoppingCartDto) {
        Optional<ShoppingCartDto> updatedShoppingCartDto = shoppingCartService.updateDto(id, shoppingCartDto);
        return updatedShoppingCartDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        Optional<ShoppingCart> shoppingCart = shoppingCartService.delete(id);
        if (shoppingCart.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
}

