package com.gitlab.controllers.api.rest;

import com.gitlab.dto.BankCardDto;
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

@Api(tags = "BankCard REST")
@Tag(name = "BankCard REST", description = "API BankCard  description")
public interface BankCardRestApi {

    @GetMapping("/api/bank-card")
    @ApiOperation(value = "Get all BankCards")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "BankCards found"),
            @ApiResponse(code = 204, message = "BankCards not present")}
    )
    ResponseEntity<List<BankCardDto>> getPage(@ApiParam(name = "page") @RequestParam(required = false, value = "page") Integer page,
                                              @ApiParam(name = "size") @RequestParam(required = false, value = "size") Integer size);

    @GetMapping("/api/bank-card/{id}")
    @ApiOperation(value = "Get BankCard by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "BankCard found"),
            @ApiResponse(code = 404, message = "BankCard not found")}
    )
    ResponseEntity<BankCardDto> get(@ApiParam(name = "id", value = "BankCard.id") @PathVariable (value = "id") Long id);



    @PostMapping("/api/bank-card")
    @ApiOperation(value = "Create BankCard")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "BankCard created"),
            @ApiResponse(code = 400, message = "BankCard not created")}
    )
    ResponseEntity<BankCardDto> create(@ApiParam(name = "bankCard", value = "BankCardDto") @Valid @RequestBody BankCardDto bankCardDto);


    @PatchMapping("/api/bank-card/{id}")
    @ApiOperation(value = "Update BankCard")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "BankCard updated"),
            @ApiResponse(code = 400, message = "BankCard not updated")}
    )
    ResponseEntity<BankCardDto> update(@ApiParam(name = "id", value = "BankCard.id") @PathVariable (value = "id") Long id,
                                       @ApiParam(name = "bankCard", value = "BankCardDto") @Valid @RequestBody BankCardDto bankCardDto);


    @DeleteMapping("/api/bank-card/{id}")
    @ApiOperation(value = "Delete BankCard by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "BankCard deleted"),
            @ApiResponse(code = 404, message = "BankCard not found")}
    )
    ResponseEntity<Void> delete(@ApiParam(name = "id", value = "BankCard.id") @PathVariable (value = "id") Long id);

}
