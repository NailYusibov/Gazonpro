package com.gitlab.controllers.api.rest;

import com.gitlab.dto.WorkingScheduleDto;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "WorkingSchedule REST")
@Tag(name = "WorkingSchedule REST", description = "API Pickup point working schedule description")
public interface WorkingScheduleRestApi {

    @GetMapping("/api/working-schedule")
    @ApiOperation(value = "Get Page of Pickup points working schedule")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pickup points working schedule page found"),
            @ApiResponse(code = 204, message = "Pickup points working schedule not present")}
    )
    ResponseEntity<List<WorkingScheduleDto>> getPage(@ApiParam(name = "page") @RequestParam(required = false, value = "page") Integer page,
                                                     @ApiParam(name = "size") @RequestParam(required = false, value = "size") Integer size);

    @GetMapping("/api/working-schedule/{id}")
    @ApiOperation(value = "Get Pickup point working schedule by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pickup point working schedule found"),
            @ApiResponse(code = 404, message = "Pickup point working schedule not found")}
    )
    ResponseEntity<WorkingScheduleDto> get(@ApiParam(name = "id", value = "WorkingSchedule.id") @PathVariable(value = "id") Long id);

    @PostMapping("/api/working-schedule")
    @ApiOperation(value = "Create Pickup point working schedule")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Pickup point working schedule created"),
            @ApiResponse(code = 400, message = "Pickup point working schedule not created")}
    )
    ResponseEntity<WorkingScheduleDto> create(@ApiParam(name = "workingSchedule", value = "WorkingScheduleDto") @Valid @RequestBody WorkingScheduleDto workingScheduleDto);

    @PatchMapping("/api/working-schedule/{id}")
    @ApiOperation(value = "Update Pickup point working schedule")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Working Schedule updated"),
            @ApiResponse(code = 400, message = "Working Schedule not updated")}
    )
    ResponseEntity<WorkingScheduleDto> update(@ApiParam(name = "id", value = "WorkingSchedule.id") @PathVariable(value = "id") Long id,
                                              @ApiParam(name = "workingSchedule", value = "WorkingScheduleDto") @Valid @RequestBody WorkingScheduleDto workingScheduleDto);

    @DeleteMapping("/api/working-schedule/{id}")
    @ApiOperation(value = "Delete Pickup point working schedule by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pickup point working schedule deleted"),
            @ApiResponse(code = 404, message = "Pickup point working schedule not found")}
    )
    ResponseEntity<Void> delete(@ApiParam(name = "id", value = "WorkingSchedule.id") @PathVariable(value = "id") Long id);
}
