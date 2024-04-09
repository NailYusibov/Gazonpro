package com.gitlab.controller;

import com.gitlab.dto.ReviewImageDto;
import com.gitlab.mapper.ReviewImageMapper;
import com.gitlab.model.Review;
import com.gitlab.model.ReviewImage;
import com.gitlab.service.ReviewImageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.CoreMatchers.equalTo;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;


class ReviewImageRestControllerIT extends AbstractIntegrationTest {

    private static final String REVIEW_IMAGE_URN = "/api/review-images";
    private static final String REVIEW_IMAGE_URI = URL + REVIEW_IMAGE_URN;
    @Autowired
    private ReviewImageService reviewImageService;
    @Autowired
    private ReviewImageMapper reviewImageMapper;

    @Test
    @Transactional(readOnly = true)
    void should_get_all_reviewImages() throws Exception {

        var response = reviewImageService.getPage(null, null);
        var expected = objectMapper.writeValueAsString(reviewImageMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(REVIEW_IMAGE_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @Transactional(readOnly = true)
    void should_get_page() throws Exception {
        int page = 0;
        int size = 2;
        String parameters = "?page=" + page + "&size=" + size;

        var response = reviewImageService.getPage(page, size);
        assertFalse(response.getContent().isEmpty());

        var expected = objectMapper.writeValueAsString(reviewImageMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(REVIEW_IMAGE_URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_get_page_with_incorrect_parameters() throws Exception {
        int page = 0;
        int size = -2;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(REVIEW_IMAGE_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_page_without_content() throws Exception {
        int page = 10;
        int size = 100;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(REVIEW_IMAGE_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_reviewImage_by_id() throws Exception {
        long id = 1L;
        String expected = objectMapper.writeValueAsString(
                reviewImageMapper.toDto(
                        reviewImageService
                                .findById(id)
                                .orElse(null))
        );

        mockMvc.perform(get(REVIEW_IMAGE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_when_get_reviewImage_by_non_existent_id() throws Exception {
        long id = 10L;
        mockMvc.perform(get(REVIEW_IMAGE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_create_reviewImage() throws Exception {
        ReviewImageDto reviewImageDto = generateReviewDto();

        String jsonReviewImageDto = objectMapper.writeValueAsString(reviewImageDto);

        mockMvc.perform(post(REVIEW_IMAGE_URI)
                        .content(jsonReviewImageDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void should_update_reviewImage_by_id() throws Exception {
        long id = 1L;
        int numberOfEntitiesExpected = reviewImageService.findAll().size();

        ReviewImageDto reviewImageDto = generateReviewDto();
        reviewImageDto.setId(id);
        reviewImageDto.setName("updatedName");
        String jsonReviewImageDto = objectMapper.writeValueAsString(reviewImageDto);
        String expected = objectMapper.writeValueAsString(reviewImageDto);

        mockMvc.perform(patch(REVIEW_IMAGE_URI + "/{id}", id)
                        .content(jsonReviewImageDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andExpect(result -> assertThat(reviewImageService.findAll().size(),
                        equalTo(numberOfEntitiesExpected)));
    }

    @Test
    void should_return_not_found_when_update_reviewImage_by_non_existent_id() throws Exception {
        long id = 10L;

        ReviewImageDto reviewImageDto = generateReviewDto();
        reviewImageDto.setId(id);
        reviewImageDto.setName("updatedName");
        String jsonReviewImageDto = objectMapper.writeValueAsString(reviewImageDto);

        mockMvc.perform(patch(REVIEW_IMAGE_URI + "/{id}", id)
                        .content(jsonReviewImageDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_reviewImage_by_id() throws Exception {
        ReviewImage reviewImage = reviewImageService.save(reviewImageMapper.toEntity(generateReviewDto()));
        long id = reviewImageService.findById(reviewImage.getId()).get().getId();
        mockMvc.perform(delete(REVIEW_IMAGE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(REVIEW_IMAGE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private ReviewImageDto generateReviewDto() {
        ReviewImageDto reviewImageDto = new ReviewImageDto();
        reviewImageDto.setReviewId(1L);
        reviewImageDto.setName("file.txt");
        reviewImageDto.setData(new byte[]{1, 2, 3});

        return reviewImageDto;
    }

    @Test
    void should_use_user_assigned_id_in_database_for_review_image() throws Exception {
        ReviewImageDto reviewImageDto = generateReviewDto();
        reviewImageDto.setId(9999L);
        String jsonReviewImageDto = objectMapper.writeValueAsString(reviewImageDto);

        MockHttpServletResponse response = mockMvc.perform(post(REVIEW_IMAGE_URI)
                        .content(jsonReviewImageDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        ReviewImageDto createdReviewImageDto = objectMapper.readValue(response.getContentAsString(), ReviewImageDto.class);
        Assertions.assertNotEquals(reviewImageDto.getId(), createdReviewImageDto.getId());
    }


}