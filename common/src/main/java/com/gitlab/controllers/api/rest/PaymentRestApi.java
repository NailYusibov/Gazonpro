package com.gitlab.controllers.api.rest;

import com.gitlab.dto.PaymentDto;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Payment REST")
@Tag(name = "Payment REST", description = "Payment API description")
@RequestMapping("/api/payment")
public interface PaymentRestApi {

    @GetMapping("")
    @ApiOperation(value = "Get payment page")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "payment page found"),
            @ApiResponse(code = 204, message = "payment page not present")}
    )
    ResponseEntity<List<PaymentDto>> getPage(@ApiParam(name = "page") @RequestParam(required = false, value = "page") Integer page,
                                             @ApiParam(name = "size") @RequestParam(required = false, value = "size") Integer size);


    @ApiOperation(value = "Get payment by Id")
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Payment found"),
            @ApiResponse(code = 404, message = "Payment not present")}
    )
    ResponseEntity<PaymentDto> get(@ApiParam(name = "id", value = "Payment Id") @PathVariable Long id);

    @ApiOperation(value = "Create a new payment")
    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Payment created"),
            @ApiResponse(code = 400, message = "Payment not created")}
    )
    ResponseEntity<PaymentDto> create(@ApiParam(name = "paymentDto", value = "Payment details") @RequestBody PaymentDto paymentDto);

    @ApiOperation(value = "Update payment by Id")
    @PatchMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Payment updated"),
            @ApiResponse(code = 404, message = "Payment not found")}
    )
    ResponseEntity<PaymentDto> update(@ApiParam(name = "id", value = "Payment Id") @PathVariable Long id, @ApiParam(name = "PaymentDto", value = "Update Payment details") @RequestBody PaymentDto paymentDto);
}
