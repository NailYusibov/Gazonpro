package com.gitlab.controllers.api.rest;

import com.gitlab.dto.ProductDto;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Api(tags = "ProductSearch")
@Tag(name = "ProductSearch", description = "ProductSearch API description")
public interface ProductSearchRestApi {

    @GetMapping("/api/search")
    @ApiOperation(value = "Search products by Product.name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Products found"),
            @ApiResponse(code = 204, message = "Products not present")}
    )
    ResponseEntity<List<ProductDto>> search(
            @RequestParam("name") String name) throws InterruptedException;

    @GetMapping("/api/search/paginate")
    @ApiOperation(value = "Search products by Product.name with pagination")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Products found"),
            @ApiResponse(code = 204, message = "Products not present")}
    )
    ResponseEntity<List<ProductDto>> searchPaginate(
            @ApiParam(name = "name") @RequestParam(required = false, defaultValue = "", value = "name") String name,
            @ApiParam(name = "page") @RequestParam(required = false, defaultValue = "0", value = "page") Integer page,
            @ApiParam(name = "size") @RequestParam(required = false, defaultValue = "10", value = "size") Integer size)
            throws InterruptedException;
}