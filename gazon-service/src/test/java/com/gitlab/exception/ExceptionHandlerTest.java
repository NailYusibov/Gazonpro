package com.gitlab.exception;

import com.gitlab.controller.AbstractIntegrationTest;
import com.gitlab.exception.handler.ErrorResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ExceptionHandlerTest extends AbstractIntegrationTest {

    private static final String REVIEW_URI = URL + "/api/review-amount";

    @Test
    void should_handle_entity_not_found_exception() throws Exception {
        long id = 9999L;
        ErrorResponseDto expectedErrorResponseDto = generateErrorResponseDto(404, "Товар не найден");

        MvcResult result = mockMvc.perform(get(REVIEW_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponseDto responseErrorResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString()
                , ErrorResponseDto.class);

        Assertions.assertEquals(expectedErrorResponseDto, responseErrorResponseDto);
    }

    private ErrorResponseDto generateErrorResponseDto(Integer code, String message) {
        return new ErrorResponseDto(code, message);
    }
}
