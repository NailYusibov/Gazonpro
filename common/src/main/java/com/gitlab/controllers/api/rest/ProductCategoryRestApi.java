package com.gitlab.controllers.api.rest;

import com.gitlab.dto.ProductCategoryDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "ProductCategory REST")
@Tag(name = "ProductCategory REST", description = "ProductCategory API description")
public interface ProductCategoryRestApi {


    @GetMapping("/api/category")
    @ApiOperation(value = "Get Page of Product category")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product category Page found"),
            @ApiResponse(code = 204, message = "Product category Page not present")}
    )
    ResponseEntity<List<ProductCategoryDto>> getPage(@ApiParam(name = "page") @RequestParam(required = false, value = "page") Integer page,
                                                     @ApiParam(name = "size") @RequestParam(required = false, value = "size") Integer size);

    @GetMapping("/api/category/{id}")
    @ApiOperation(value = "Get ProductCategory by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product category found"),
            @ApiResponse(code = 404, message = "Product category not found")}
    )
    ResponseEntity<ProductCategoryDto> get(@ApiParam(name = "id", value = "ProductCategory.id") @PathVariable(value = "id") Long id);

    @PostMapping("/api/category")
    @ApiOperation(value = "Create Product category")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Product category created"),
            @ApiResponse(code = 400, message = "Product category not created")}
    )
    ResponseEntity<ProductCategoryDto> create(@ApiParam(name = "productCategory", value = "ProductCategoryDto") @Valid @RequestBody ProductCategoryDto productCategoryDto);


    @PatchMapping("/api/category/{id}")
    @ApiOperation(value = "Update Product category")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product category updated"),
            @ApiResponse(code = 400, message = "Product category not updated")}
    )
    ResponseEntity<ProductCategoryDto> update(@ApiParam(name = "id", value = "ProductCategory.id") @PathVariable(value = "id") Long id,
                                    @ApiParam(name = "productCategory", value = "ProductCategoryDto") @Valid @RequestBody ProductCategoryDto productCategoryDto);

    @DeleteMapping("/api/category/{id}")
    @ApiOperation(value = "Delete Product category by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product category deleted"),
            @ApiResponse(code = 404, message = "Product category not found")}
    )
    ResponseEntity<Void> delete(@ApiParam(name = "id", value = "ProductCategory.id") @PathVariable(value = "id") Long id);
}