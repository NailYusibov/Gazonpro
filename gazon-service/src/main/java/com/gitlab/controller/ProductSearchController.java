package com.gitlab.controller;

import com.gitlab.controllers.api.rest.ProductSearchRestApi;
import com.gitlab.dto.ProductDto;
import com.gitlab.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        List<ProductDto> productDtos;
        if (search(name).getStatusCode() == HttpStatus.OK) {
            productDtos = search(name).getBody();
        } else {
            return ResponseEntity.noContent().build();
        }
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), productDtos.size());
        Page<ProductDto> pages = new PageImpl<>(productDtos.subList(start, end), pageable, productDtos.size());

        return ResponseEntity.ok(pages.getContent());
    }

}
