package com.gitlab.controllers.api.rest;

import com.gitlab.dto.SelectedProductDto;
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

@Api(tags = "SelectedProduct REST")
@Tag(name = "SelectedProduct REST", description = "API для работы с товарами, добавленными в корзину")
public interface SelectedProductRestAPI {


    @GetMapping("/api/selected-product")
    @ApiOperation(value = "Get all SelectedProduct")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SelectedProduct found"),
            @ApiResponse(code = 204, message = "SelectedProduct not present")}
    )
    ResponseEntity<List<SelectedProductDto>> getPage(@ApiParam(name = "page") @RequestParam(required = false, value = "page") Integer page,
                                                     @ApiParam(name = "size") @RequestParam(required = false, value = "size") Integer size);

    @GetMapping("/api/selected-product/{id}")
    @ApiOperation(value = "Get SelectedProduct by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SelectedProduct found"),
            @ApiResponse(code = 404, message = "SelectedProduct not found")}
    )
    ResponseEntity<SelectedProductDto> get(@ApiParam(name = "id", value = "SelectedProduct.id") @PathVariable(value = "id") Long id);

    @PostMapping("/api/selected-product")
    @ApiOperation(value = "Create SelectedProduct")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "SelectedProduct created"),
            @ApiResponse(code = 400, message = "SelectedProduct not created")}
    )
    ResponseEntity<SelectedProductDto> create(@ApiParam(name = "SelectedProduct", value = "SelectedProductDto")
                                              @Valid @RequestBody SelectedProductDto selectedProductDto);

    @PatchMapping("/api/selected-product/{id}")
    @ApiOperation(value = "Update SelectedProduct")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SelectedProduct updated"),
            @ApiResponse(code = 404, message = "Previous SelectedProduct not found"),
            @ApiResponse(code = 400, message = "SelectedProduct not updated")}
    )
    ResponseEntity<SelectedProductDto> update(@ApiParam(name = "id", value = "SelectedProduct.id") @PathVariable(value = "id") Long id,
                                              @ApiParam(name = "SelectedProduct", value = "SelectedProductDto")
                                              @Valid @RequestBody SelectedProductDto selectedProductDto);

    @DeleteMapping("/api/selected-product/{id}")
    @ApiOperation(value = "Delete SelectedProduct by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SelectedProduct deleted"),
            @ApiResponse(code = 404, message = "SelectedProduct not found")}
    )
    ResponseEntity<Void> delete(@ApiParam(name = "id", value = "SelectedProduct.id") @PathVariable(value = "id") Long id);


}
