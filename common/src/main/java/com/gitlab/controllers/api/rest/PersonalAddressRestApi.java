package com.gitlab.controllers.api.rest;

import com.gitlab.dto.PersonalAddressDto;
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

@Api(tags = "Personal Address REST")
@Tag(name = "Personal Address REST", description = "API for personal address")
public interface PersonalAddressRestApi {
    @GetMapping("/api/personal-address")
    @ApiOperation(value = "Get all Personal Addresses")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Personal Addresses found"),
            @ApiResponse(code = 204, message = "Personal Addresses not present")}
    )
    ResponseEntity<List<PersonalAddressDto>> getPage(@ApiParam(name = "page") @RequestParam(required = false, value = "page") Integer page,
                                                     @ApiParam(name = "size") @RequestParam(required = false, value = "size") Integer size);

    @GetMapping("/api/personal-address/{id}")
    @ApiOperation(value = "Get Personal Address by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Personal Address found"),
            @ApiResponse(code = 404, message = "Personal Address not found")}
    )
    ResponseEntity<PersonalAddressDto> get(@ApiParam(name = "id", value = "PersonalAddress.id")
                                           @PathVariable (value = "id") Long id);

    @PostMapping("/api/personal-address")
    @ApiOperation(value = "Create Personal Address")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Personal Address created"),
            @ApiResponse(code = 400, message = "Personal Address not created")}
    )
    ResponseEntity<PersonalAddressDto> create(@ApiParam(name = "personalAddress", value = "PersonalAddressDto")
                                              @Valid @RequestBody PersonalAddressDto personalAddressDto);

    @PatchMapping("/api/personal-address/{id}")
    @ApiOperation(value = "Update Personal Address")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Personal Address updated"),
            @ApiResponse(code = 400, message = "Personal Address not updated")}
    )
    ResponseEntity<PersonalAddressDto> update(@ApiParam(name = "id", value = "PersonalAddress.id")
                                              @PathVariable (value = "id") Long id,
                                              @ApiParam(name = "personalAddress", value = "PersonalAddressDto")
                                              @Valid @RequestBody PersonalAddressDto personalAddressDto);

    @DeleteMapping("/api/personal-address/{id}")
    @ApiOperation(value = "Delete Personal Address by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Personal Address deleted"),
            @ApiResponse(code = 404, message = "Personal Address not found")}
    )
    ResponseEntity<Void> delete(@ApiParam(name = "id", value = "PersonalAddress.id")
                                @PathVariable (value = "id") Long id);
}
