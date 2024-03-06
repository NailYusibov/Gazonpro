package com.gitlab.controllers.api.rest;

import com.gitlab.dto.PostomatDto;
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

@Api(tags = "Postomat REST")
@Tag(name = "Postomat REST", description = "API for postomat")
public interface PostomatRestApi {

    @GetMapping("/api/postomat")
    @ApiOperation(value = "Get all Postomats")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Postomats found"),
            @ApiResponse(code = 204, message = "Postomats not present")}
    )
    ResponseEntity<List<PostomatDto>> getPage(@ApiParam(name = "page") @RequestParam(required = false, value = "page") Integer page,
                                              @ApiParam(name = "size") @RequestParam(required = false, value = "size") Integer size);

    @GetMapping("/api/postomat/{id}")
    @ApiOperation(value = "Get Postomat by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Postomat found"),
            @ApiResponse(code = 404, message = "Postomat not found")}
    )
    ResponseEntity<PostomatDto> get(@ApiParam(name = "id", value = "Postomat.id")
                                    @PathVariable (value = "id") Long id);

    @PostMapping("/api/postomat")
    @ApiOperation(value = "Create Postomat", notes="В тело запроса необходимо добавить сточку:  \"type\": \"postomat\"")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Postomat created"),
            @ApiResponse(code = 400, message = "Postomat not created")}
    )
    ResponseEntity<PostomatDto> create(@ApiParam(name = "postomat", value = "PostomatDto")
                                       @Valid @RequestBody PostomatDto postomatDto);

    @PatchMapping("/api/postomat/{id}")
    @ApiOperation(value = "Update Postomat")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Postomat updated"),
            @ApiResponse(code = 400, message = "Postomat not updated")}
    )
    ResponseEntity<PostomatDto> update(@ApiParam(name = "id", value = "Postomat.id")
                                       @PathVariable (value = "id") Long id,
                                       @ApiParam(name = "postomat", value = "PostomatDto")
                                       @Valid @RequestBody PostomatDto postomatDto);

    @DeleteMapping("/api/postomat/{id}")
    @ApiOperation(value = "Delete Postomat by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Postomat deleted"),
            @ApiResponse(code = 404, message = "Postomat not found")}
    )
    ResponseEntity<Void> delete(@ApiParam(name = "id", value = "Postomat.id")
                                @PathVariable (value = "id") Long id);
}
