package com.gitlab.controller;

import com.gitlab.controllers.api.rest.ProductSearchRestApi;
import com.gitlab.dto.ProductDto;
import com.gitlab.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductSearchController implements ProductSearchRestApi {


    private final ProductService productService;

    @Override
    public ResponseEntity<List<ProductDto>> search(String name) throws InterruptedException {

        List<ProductDto> products = productService.findByNameIgnoreCaseContaining(name);
        return !products.isEmpty() ?
                ResponseEntity.ok(products) :
        ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ProductDto>> searchPaginate(String name, Integer page, Integer size) throws InterruptedException {

        Pageable pageable = PageRequest.of(page, size);
        if (!name.isEmpty()) {
            List<ProductDto> result = productService.findByNameIgnoreCaseContaining(name, pageable).getContent();
            if (!result.isEmpty()) {
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(productService.getPage(page, size).getContent()); // if the name is empty, all products will be found
        }
    }

}
