package com.gitlab.controller;

import com.gitlab.controllers.api.rest.ProductSearchRestApi;
import com.gitlab.dto.ProductDto;
import com.gitlab.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            List<ProductDto> products = productService.findByNameIgnoreCaseContaining(name, pageable).getContent();
            return products.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(products);
        }
        List<ProductDto> products = productService.findByNameIgnoreCaseContaining(name);
        return products.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(products);
    }

}
