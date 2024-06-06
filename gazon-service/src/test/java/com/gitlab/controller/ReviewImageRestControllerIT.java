package com.gitlab.controller;

import com.gitlab.TestUtil;
import com.gitlab.dto.ReviewDto;
import com.gitlab.dto.ReviewImageDto;
import com.gitlab.mapper.ReviewImageMapper;
import com.gitlab.model.Review;
import com.gitlab.model.ReviewImage;
import com.gitlab.service.ProductService;
import com.gitlab.service.ReviewImageService;
import com.gitlab.service.ReviewService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.CoreMatchers.equalTo;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;

class ReviewImageRestControllerIT extends AbstractIntegrationTest {

    private static final String REVIEW_IMAGE_URN = "/api/review-images";
    private static final String REVIEW_URN = "/api/review";
    private static final String REVIEW_IMAGE_URI = URL + REVIEW_IMAGE_URN;

    @Autowired
    private ReviewImageService reviewImageService;
    @Autowired
    private ReviewImageMapper reviewImageMapper;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ProductService productService;

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
        long id;

        ReviewImageDto reviewImageDto = reviewImageService.saveDto(
                TestUtil.generateReviewImageDto(reviewService.saveDto(
                                TestUtil.generateReviewDto(productService.save(
                                        TestUtil.generateProductDto()).get().getId()))
                        .getId()));

        id = reviewImageDto.getId();

        String expected = objectMapper.writeValueAsString(
                reviewImageMapper.toDto(
                        reviewImageService
                                .findById(reviewImageDto.getId())
                                .orElse(null))
        );

        mockMvc.perform(get(REVIEW_IMAGE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_when_get_reviewImage_by_non_existent_id() throws Exception {
        long id = 9999L;

        mockMvc.perform(get(REVIEW_IMAGE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_create_reviewImage() throws Exception {
        ReviewImageDto reviewImageDto = reviewImageService.saveDto(
                TestUtil.generateReviewImageDto(reviewService.saveDto(
                                TestUtil.generateReviewDto(productService.save(
                                        TestUtil.generateProductDto()).get().getId()))
                        .getId()));

        String jsonReviewImageDto = objectMapper.writeValueAsString(reviewImageDto);

        mockMvc.perform(post(REVIEW_IMAGE_URI)
                        .content(jsonReviewImageDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void should_created_reviewImage_ignored_id_in_the_request_body() throws Exception {
        ReviewImageDto reviewImageDto = reviewImageService.saveDto(
                TestUtil.generateReviewImageDto(reviewService.saveDto(
                                TestUtil.generateReviewDto(productService.save(
                                        TestUtil.generateProductDto()).get().getId()))
                        .getId()));

        String jsonReviewImageDto = objectMapper.writeValueAsString(reviewImageDto);

        MockHttpServletResponse response = mockMvc.perform(post(REVIEW_IMAGE_URI)
                        .content(jsonReviewImageDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        ReviewImageDto createdReviewImageDto = objectMapper.readValue(response.getContentAsString(), ReviewImageDto.class);
        Assertions.assertNotEquals(reviewImageDto.getId(), createdReviewImageDto.getId());
    }

    @Test
    void should_update_reviewImage_by_id() throws Exception {
        long id;
        int numberOfEntitiesExpected;

        ReviewImageDto reviewImageDto = reviewImageService.saveDto(
                TestUtil.generateReviewImageDto(reviewService.saveDto(
                                TestUtil.generateReviewDto(productService.save(
                                        TestUtil.generateProductDto()).get().getId()))
                        .getId()));

        id = reviewImageDto.getId();

        numberOfEntitiesExpected = reviewImageService.findAll().size();
        reviewImageDto.setName("updatedTest");

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
    void should_update_reviewImage_ignored_id_in_the_request_body() throws Exception {
        long id;

        ReviewImageDto reviewImageDto = reviewImageService.saveDto(
                TestUtil.generateReviewImageDto(reviewService.saveDto(
                                TestUtil.generateReviewDto(productService.save(
                                        TestUtil.generateProductDto()).get().getId()))
                        .getId()));

        String expected = objectMapper.writeValueAsString(reviewImageDto);

        id = reviewImageDto.getId();
        reviewImageDto.setId(9999L);

        String jsonReviewImageDto = objectMapper.writeValueAsString(reviewImageDto);

        mockMvc.perform(patch(REVIEW_IMAGE_URI + "/{id}", id)
                        .content(jsonReviewImageDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_update_reviewImage_by_id_do_not_overwrite_fields_with_null() throws Exception {
        long id;

        ReviewImageDto reviewImageDto = reviewImageService.saveDto(
                TestUtil.generateReviewImageDto(reviewService.saveDto(
                                TestUtil.generateReviewDto(productService.save(
                                        TestUtil.generateProductDto()).get().getId()))
                        .getId()));

        id = reviewImageDto.getId();
        String expected = objectMapper.writeValueAsString(reviewImageDto);

        reviewImageDto.setName(null);
        reviewImageDto.setData(null);

        String jsonReviewImageDto = objectMapper.writeValueAsString(reviewImageDto);

        mockMvc.perform(patch(REVIEW_IMAGE_URI + "/{id}", id)
                        .content(jsonReviewImageDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_when_update_reviewImage_by_non_existent_id() throws Exception {
        long id = 9999L;

        ReviewImageDto reviewImageDto = reviewImageService.saveDto(
                TestUtil.generateReviewImageDto(reviewService.saveDto(
                                TestUtil.generateReviewDto(productService.save(
                                        TestUtil.generateProductDto()).get().getId()))
                        .getId()));

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
        long id;

        ReviewImageDto reviewImageDto = reviewImageService.saveDto(
                TestUtil.generateReviewImageDto(reviewService.saveDto(
                                TestUtil.generateReviewDto(productService.save(
                                        TestUtil.generateProductDto()).get().getId()))
                        .getId()));

        id = reviewImageDto.getId();

        mockMvc.perform(delete(REVIEW_IMAGE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get(REVIEW_IMAGE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_not_found_when_delete_reviewImage_by_non_existent_id() throws Exception {
        long id = 9999L;

        mockMvc.perform(delete(REVIEW_IMAGE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_get_images_ids_by_review_id() throws Exception {
        long id;

        ReviewDto reviewDto = reviewService.saveDto(TestUtil.generateReviewDto(
                productService.save(TestUtil.generateProductDto()).get().getId()));

        id = reviewDto.getId();

        reviewImageService.saveDto(TestUtil.generateReviewImageDto(id));
        reviewImageService.saveDto(TestUtil.generateReviewImageDto(id));

        Optional<Review> reviewOptional = reviewService.findById(id);

        String expected = objectMapper.writeValueAsString(
                reviewOptional.orElse(new Review()).getReviewImages().stream()
                        .map(ReviewImage::getId).mapToLong(Long::valueOf).toArray()
        );

        mockMvc.perform(get(URL + "/api/review" + "/{id}" + "/images", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_when_get_images_ids_by_review_id_by_non_existent_review_id()
            throws Exception {
        long id = 9999L;

        mockMvc.perform(get(URL + "/api/review" + "/{id}" + "/images", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_no_content_when_get_images_ids_by_review_id_when_accepting_an_empty_array_images_ids()
            throws Exception {
        long id = reviewService.saveDto(TestUtil.generateReviewDto(
                productService.save(TestUtil.generateProductDto()).get().getId())).getId();

        mockMvc.perform(get(URL + "/api/review" + "/{id}" + "/images", id))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_upload_images_by_review_id() throws Exception {
        long id = reviewService.saveDto(TestUtil.generateReviewDto(
                productService.save(TestUtil.generateProductDto()).get().getId())).getId();

        byte[] imageData = TestUtil.getBytesFromImage();

        MockMultipartFile file1 =
                new MockMultipartFile(
                        "files", "product.png", MediaType.IMAGE_PNG_VALUE, imageData);
        MockMultipartFile file2 =
                new MockMultipartFile(
                        "files", "product.png", MediaType.IMAGE_PNG_VALUE, imageData);

        mockMvc.perform(multipart(URL + REVIEW_URN + "/{id}" + "/images", id)
                        .file(file1)
                        .file(file2)
                        .accept(MediaType.IMAGE_PNG))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void should_return_not_found_upload_images_by_review_id_by_non_existent_review_id() throws Exception {
        long id = 9999L;

        byte[] imageData = TestUtil.getBytesFromImage();

        MockMultipartFile file1 =
                new MockMultipartFile(
                        "files", "product.png", MediaType.IMAGE_PNG_VALUE, imageData);
        MockMultipartFile file2 =
                new MockMultipartFile(
                        "files", "product.png", MediaType.IMAGE_PNG_VALUE, imageData);

        mockMvc.perform(multipart(URL + REVIEW_URN + "/{id}" + "/images", id)
                        .file(file1)
                        .file(file2)
                        .accept(MediaType.IMAGE_PNG))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_bad_request_upload_images_by_review_id_without_image_file() throws Exception {
        long id = reviewService.saveDto(TestUtil.generateReviewDto(
                productService.save(TestUtil.generateProductDto()).get().getId())).getId();

        mockMvc.perform(multipart(URL + REVIEW_URN + "/{id}" + "/images", id)
                        .accept(MediaType.IMAGE_PNG))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_delete_all_images_by_review_id() throws Exception {
        long id = reviewService.saveDto(TestUtil.generateReviewDto(
                productService.save(TestUtil.generateProductDto()).get().getId())).getId();

        reviewImageService.saveDto(TestUtil.generateReviewImageDto(id));
        reviewImageService.saveDto(TestUtil.generateReviewImageDto(id));
        reviewImageService.saveDto(TestUtil.generateReviewImageDto(id));

        mockMvc.perform(delete(URL + REVIEW_URN + "/{id}" + "/images", id))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(delete(URL + REVIEW_URN + "/{id}" + "/images", id))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_return_not_found_when_delete_all_images_by_review_id_non_existent_review_id() throws Exception {
        long id = 9999L;

        mockMvc.perform(delete(URL + REVIEW_URN + "/{id}" + "/images", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_no_content_when_delete_all_images_by_review_id_has_no_images() throws Exception {
        long id = reviewService.saveDto(TestUtil.generateReviewDto(
                productService.save(TestUtil.generateProductDto()).get().getId())).getId();

        mockMvc.perform(delete(URL + REVIEW_URN + "/{id}" + "/images", id))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}