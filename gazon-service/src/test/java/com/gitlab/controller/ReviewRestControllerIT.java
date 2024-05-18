package com.gitlab.controller;

import com.gitlab.dto.ReviewDto;
import com.gitlab.mapper.ReviewMapper;
import com.gitlab.model.Review;
import com.gitlab.service.ReviewService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.CoreMatchers.equalTo;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;

class ReviewRestControllerIT extends AbstractIntegrationTest {

    private static final String REVIEW_URN = "/api/review";
    private static final String REVIEW_URI = URL + REVIEW_URN;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewMapper reviewMapper;

    @Test
    @Transactional(readOnly = true)
    void should_get_all_reviews() throws Exception {

        var response = reviewService.getPage(null, null);
        var expected = objectMapper.writeValueAsString(reviewMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(REVIEW_URI))
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

        var response = reviewService.getPage(page, size);
        assertFalse(response.getContent().isEmpty());

        var expected = objectMapper.writeValueAsString(reviewMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(REVIEW_URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_get_page_with_incorrect_parameters() throws Exception {
        int page = 0;
        int size = -2;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(REVIEW_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_page_without_content() throws Exception {
        int page = 10;
        int size = 100;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(REVIEW_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    @Test
    void should_get_review_by_id() throws Exception {
        ReviewDto reviewDto = generateReviewDto();
        reviewDto.setId(4L);
        Long id = reviewDto.getId();
        String expected = objectMapper.writeValueAsString(
                reviewMapper.toDto(
                        reviewService
                                .findById(id)
                                .orElse(null))
        );

        mockMvc.perform(get(REVIEW_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_when_get_review_by_non_existent_id() throws Exception {
        long id = 10L;
        mockMvc.perform(get(REVIEW_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_create_review() throws Exception {
        ReviewDto reviewDto = generateReviewDto();
        reviewDto.setId(1L);
        String jsonReviewDto = objectMapper.writeValueAsString(reviewDto);

        mockMvc.perform(post(REVIEW_URI)
                        .content(jsonReviewDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void should_review_Id_notEqual_before_and_after_post_request() throws Exception {
        ReviewDto reviewDto = generateReviewDto();
        reviewDto.setId(1000L);
        Long idBefore = reviewDto.getId();

        String jsonReviewDto = objectMapper.writeValueAsString(reviewDto);

        MvcResult result = mockMvc.perform(post(REVIEW_URI)
                        .content(jsonReviewDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Long idAfter = objectMapper.readValue(response, ReviewDto.class).getId();

        Assertions.assertNotEquals(idBefore, idAfter);
    }

    @Test
    void should_update_review_by_id() throws Exception {
        Review review = reviewService.save(reviewMapper.toEntity(generateReviewDto()));
        long id = review.getId();
        int numberOfEntitiesExpected = reviewService.findAll().size();
        ReviewDto reviewDto = generateReviewDto();
        String jsonReviewDto = objectMapper.writeValueAsString(reviewDto);
        reviewDto.setId(id);
        String expected = objectMapper.writeValueAsString(reviewDto);

        mockMvc.perform(patch(REVIEW_URI + "/{id}", id)
                        .content(jsonReviewDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andExpect(result -> assertThat(reviewService.findAll().size(),
                        equalTo(numberOfEntitiesExpected)));
    }

    @Test
    void should_return_not_found_when_update_review_by_non_existent_id() throws Exception {
        long id = 100L;
        ReviewDto reviewDto = generateReviewDto();

        String jsonReviewDto = objectMapper.writeValueAsString(reviewDto);

        mockMvc.perform(patch(REVIEW_URI + "/{id}", id)
                        .content(jsonReviewDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_review_Id_equal_before_and_after_patch_request() throws Exception {
        ReviewDto reviewDto = generateReviewDto();
        reviewDto.setId(4L);
        Long idBefore = reviewDto.getId();

        String jsonReviewDto = objectMapper.writeValueAsString(reviewDto);

        MvcResult result = mockMvc.perform(patch(REVIEW_URI + "/{id}", idBefore)
                        .content(jsonReviewDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Long idAfter = objectMapper.readValue(response, ReviewDto.class).getId();

        Assertions.assertEquals(idBefore, idAfter);
    }

    @Test
    void should_delete_review_by_id() throws Exception {
        ReviewDto reviewDto = generateReviewDto();
        reviewDto.setId(4L);
        Long id = reviewDto.getId();

        mockMvc.perform(delete(REVIEW_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(REVIEW_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_create_multiple_reviews_by_review_id() throws Exception {
        ReviewDto reviewDto = generateReviewDto();
        reviewDto.setId(4L);
        Long id = reviewDto.getId();

        mockMvc.perform(multipart(REVIEW_URI + "/{id}" + "/images", id)
                        .file(new MockMultipartFile("files",
                                "hello.txt", MediaType.TEXT_PLAIN_VALUE, "hello".getBytes()))
                        .file(new MockMultipartFile("files",
                                "bye.txt", MediaType.TEXT_PLAIN_VALUE, "bye".getBytes()))

                        .accept(MediaType.ALL))
                .andExpect(status().isCreated());
    }

    @Test
    void should_delete_all_reviews_by_review_id() throws Exception {
        Review review = reviewService.save(reviewMapper.toEntity(generateReviewDto()));
        long id = reviewService.findById(review.getId()).get().getId();
        mockMvc.perform(delete(REVIEW_URI + "/{id}" + "/images", id))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    private ReviewDto generateReviewDto() {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setProductId(1L);
        reviewDto.setCreateDate(LocalDate.now());
        reviewDto.setPros("pros1");
        reviewDto.setCons("cons1");
        reviewDto.setComment("comment1");
        reviewDto.setRating((byte) 9);
        reviewDto.setHelpfulCounter(17);
        reviewDto.setNotHelpfulCounter(2);

        return reviewDto;
    }

    @Test
    void should_use_user_assigned_id_in_database_for_review() throws Exception {
        ReviewDto reviewDto = generateReviewDto();
        reviewDto.setId(9999L);
        String jsonReviewDto = objectMapper.writeValueAsString(reviewDto);

        MockHttpServletResponse response = mockMvc.perform(post(REVIEW_URI)
                        .content(jsonReviewDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        ReviewDto createdReviewDto = objectMapper.readValue(response.getContentAsString(), ReviewDto.class);
        Assertions.assertNotEquals(reviewDto.getId(), createdReviewDto.getId());
    }

}