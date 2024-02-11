package com.gitlab.controllers.api.rest;

import com.gitlab.dto.ProductDto;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Api(tags = "Product REST")
@Tag(name = "Product REST", description = "Product API description")
public interface ProductRestApi {

    @GetMapping("/api/product")
    @ApiOperation(value = "Get all Products")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Products found"),
            @ApiResponse(code = 204, message = "Products not present")}
    )
    ResponseEntity<List<ProductDto>> getPage(@ApiParam(name = "page") @RequestParam(required = false, value = "page") Integer page,
                                             @ApiParam(name = "size") @RequestParam(required = false, value = "size") Integer size);

    @GetMapping("/api/product/{id}")
    @ApiOperation(value = "Get Product by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product found"),
            @ApiResponse(code = 404, message = "Product not found")}
    )
    ResponseEntity<ProductDto> get(@ApiParam(name = "id", value = "Product.id") @PathVariable(value = "id") Long id);

    @PostMapping("/api/product")
    @ApiOperation(value = "Create Product")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Product created"),
            @ApiResponse(code = 400, message = "Product not created")}
    )
    ResponseEntity<ProductDto> create(@ApiParam(name = "product", value = "ProductDto") @Valid @RequestBody ProductDto productDto);

    @PatchMapping("/api/product/{id}")
    @ApiOperation(value = "Update Product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product updated"),
            @ApiResponse(code = 404, message = "Previous product not found"),
            @ApiResponse(code = 400, message = "Product not updated")}
    )
    ResponseEntity<ProductDto> update(@ApiParam(name = "id", value = "Product.id") @PathVariable(value = "id") Long id,
                                      @ApiParam(name = "product", value = "ProductDto") @Valid @RequestBody ProductDto productDto);

    @DeleteMapping("/api/product/{id}")
    @ApiOperation(value = "Delete Product by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product deleted"),
            @ApiResponse(code = 404, message = "Product not found")}
    )
    ResponseEntity<Void> delete(@ApiParam(name = "id", value = "Product.id") @PathVariable(value = "id") Long id);


    @GetMapping("/api/product/{id}/images")
    @ApiOperation(value = "Get all ProductImages IDs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ProductImages found"),
            @ApiResponse(code = 204, message = "ProductImages not present"),
            @ApiResponse(code = 404, message = "Product's ProductImages not found")}
    )
    ResponseEntity<long[]> getImagesIDsByProductId(@ApiParam(name = "id", value = "Product.id")
                                                   @PathVariable (value = "id") Long id);


    @PostMapping("/api/product/{id}/images")
    @ApiOperation(value = "Upload ProductImages")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "ProductImages uploaded"),
            @ApiResponse(code = 400, message = "ProductImages not uploaded"),
            @ApiResponse(code = 404, message = "Product not found, unable to upload images without product")}
    )
    ResponseEntity<String> uploadImagesByProductId(@RequestParam(value = "files") MultipartFile[] files,
                                                   @PathVariable (value = "id") Long id) throws IOException;


    @DeleteMapping("/api/product/{id}/images")
    @ApiOperation(value = "Delete ProductImages by Product.id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ProductImages deleted"),
            @ApiResponse(code = 204, message = "Product with such id has no images"),
            @ApiResponse(code = 404, message = "Product not found")}
    )
    ResponseEntity<String> deleteAllImagesByProductId(@PathVariable (value = "id") Long id);

}