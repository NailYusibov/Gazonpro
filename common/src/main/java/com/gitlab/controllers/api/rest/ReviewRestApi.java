package com.gitlab.controllers.api.rest;

import com.gitlab.dto.ReviewDto;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Api(tags = "Review REST")
@Tag(name = "Review REST", description = "Review API description")
public interface ReviewRestApi {

    @GetMapping("/api/review")
    @ApiOperation(value = "Get all Reviews")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Reviews found"),
            @ApiResponse(code = 204, message = "Reviews not present")}
    )
    ResponseEntity<List<ReviewDto>> getPage(@ApiParam(name = "page") @RequestParam(required = false, value = "page") Integer page,
                                            @ApiParam(name = "size") @RequestParam(required = false, value = "size") Integer size);

    @GetMapping("/api/review/{id}")
    @ApiOperation(value = "Get Review by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Review found"),
            @ApiResponse(code = 404, message = "Review not found")}
    )
    ResponseEntity<ReviewDto> get(@ApiParam(name = "id", value = "Review.id") @PathVariable("id") Long id);
    @GetMapping("/api/review-amount/{id}")
    @ApiOperation(value = "Get Review amount by product id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Review for product found"),
            @ApiResponse(code = 400, message = "Product not found")}
    )
    ResponseEntity<Long> getReviewAmount(@ApiParam(name = "id", value = "Product.id") @PathVariable("id") Long id);

    @PostMapping("/api/review")
    @ApiOperation(value = "Create Review", notes = "Поле \"reviewImagesId\" оставляй [0]")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Review created"),
            @ApiResponse(code = 400, message = "Review not created")}
    )
    ResponseEntity<ReviewDto> create(@ApiParam(name = "Review", value = "ReviewDto") @Valid @RequestBody ReviewDto reviewDto);

    @PatchMapping("/api/review/{id}")
    @ApiOperation(value = "Update Review", notes = "Поле \"reviewImagesId\" оставляй [0]")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Review updated"),
            @ApiResponse(code = 404, message = "Previous Review not found"),
            @ApiResponse(code = 400, message = "Review not updated")}
    )
    ResponseEntity<ReviewDto> update(@ApiParam(name = "id", value = "Review.id") @PathVariable("id") Long id,
                                      @ApiParam(name = "Review", value = "ReviewDto")
                                      @Valid @RequestBody ReviewDto reviewDto);

    @DeleteMapping("/api/review/{id}")
    @ApiOperation(value = "Delete Review by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Review deleted"),
            @ApiResponse(code = 404, message = "Review not found")}
    )
    ResponseEntity<Void> delete(@ApiParam(name = "id", value = "Review.id") @PathVariable("id") Long id);
}