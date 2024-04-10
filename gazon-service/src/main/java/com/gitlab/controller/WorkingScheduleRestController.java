package com.gitlab.controller;

import com.gitlab.controllers.api.rest.WorkingScheduleRestApi;
import com.gitlab.dto.WorkingScheduleDto;
import com.gitlab.model.WorkingSchedule;
import com.gitlab.service.WorkingScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class WorkingScheduleRestController implements WorkingScheduleRestApi {

    private final WorkingScheduleService workingScheduleService;
    public ResponseEntity<List<WorkingScheduleDto>> getPage(Integer page, Integer size) {
        var workingSchedulePage = workingScheduleService.getPageDto(page, size);
        if (workingSchedulePage == null || workingSchedulePage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(workingSchedulePage.getContent());
    }

    @Override
    public ResponseEntity<WorkingScheduleDto> get(Long id) {
        Optional<WorkingScheduleDto> optionalWorkingScheduleDto = workingScheduleService.findByIdDto(id);
        return optionalWorkingScheduleDto
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<WorkingScheduleDto> create(WorkingScheduleDto workingScheduleDto) {
        List<WorkingScheduleDto> workingScheduleDtoList = workingScheduleService.findAllDto();
        for (WorkingScheduleDto dto : workingScheduleDtoList) {
            if ( /* workingScheduleDto.getId().equals(dto.getId()) && */ // Избыточная проверка, с ним не работало добавление нового расписания
                workingScheduleDto.getDayOfWeek().equals(dto.getDayOfWeek()) &&
                workingScheduleDto.getFrom().equals(dto.getFrom()) &&
                workingScheduleDto.getTo().equals(dto.getTo())) {
                Optional<WorkingScheduleDto> oldWorkingScheduleDto = workingScheduleService.findByIdDto(dto.getId());
                return ResponseEntity.status(HttpStatus.OK)
                        .body(oldWorkingScheduleDto.get());
            }
        }
        if (workingScheduleDto.getFrom().isAfter(workingScheduleDto.getTo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(workingScheduleDto);
        } else {
            WorkingScheduleDto savedWorkingScheduleDto = workingScheduleService.saveDto(workingScheduleDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedWorkingScheduleDto);
        }
    }

    @Override
    public ResponseEntity<WorkingScheduleDto> update(Long id, WorkingScheduleDto workingScheduleDto) {
        if (workingScheduleDto.getFrom().isAfter(workingScheduleDto.getTo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(workingScheduleDto);
        } else {
            Optional<WorkingScheduleDto> optionalUpdatedWorkingScheduleDto = workingScheduleService.updateDto(id, workingScheduleDto);
            return optionalUpdatedWorkingScheduleDto
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        Optional<WorkingSchedule> optionalDeletedWorkingSchedule = workingScheduleService.delete(id);
        return optionalDeletedWorkingSchedule
                .map(workingSchedule -> ResponseEntity.ok().<Void>build())
                .orElse(ResponseEntity.notFound().build());
    }
    private ResponseEntity<Page<WorkingScheduleDto>> createPagedResponse(Page<WorkingSchedule> workingSchedulePage) {
        var workingScheduleDtoPage = workingScheduleService.getPageDto(workingSchedulePage.getPageable().getPageNumber(), workingSchedulePage.getPageable().getPageSize());
        return ResponseEntity.ok(workingScheduleDtoPage);
    }

}
