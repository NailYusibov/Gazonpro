package com.gitlab.controller;

import com.gitlab.controllers.api.rest.PostomatRestApi;
import com.gitlab.dto.PostomatDto;
import com.gitlab.service.PostomatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class PostomatRestController implements PostomatRestApi {

    private final PostomatService postomatService;

    public ResponseEntity<List<PostomatDto>> getPage(Integer page, Integer size) {
        var postomatPage = postomatService.getPageDto(page, size);
        if (postomatPage == null || postomatPage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(postomatPage.getContent());
    }
    
    @Override
    public ResponseEntity<PostomatDto> get(Long id) {
        return postomatService.findByIdDto(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<PostomatDto> create(PostomatDto postomatDto) {
        PostomatDto createdPostomatDto = postomatService.saveDto(postomatDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdPostomatDto);
    }

    @Override
    public ResponseEntity<PostomatDto> update(Long id, PostomatDto postomatDto) {
        return postomatService.updateDto(id, postomatDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        return postomatService.deleteDto(id).isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok().build();
    }
}
