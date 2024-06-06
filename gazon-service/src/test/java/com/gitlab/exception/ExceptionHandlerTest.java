package com.gitlab.exception;

import com.gitlab.controller.AbstractIntegrationTest;
import com.gitlab.dto.RoleDto;
import com.gitlab.exception.handler.ErrorResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ExceptionHandlerTest extends AbstractIntegrationTest {

    private static final String REVIEW_URI = URL + "/api/review-amount";
    private static final String ROLE_URI = URL + "/api/role";

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

    @Test
    void should_return_bad_request_when_save_duplicate_in_unique_field() throws Exception {
        RoleDto role = new RoleDto();
        RoleDto roleDuplicate = new RoleDto();

        role.setRoleName("ROLE_ERROR");
        roleDuplicate.setRoleName("ROLE_ERROR");

        String jsonRoleDto = objectMapper.writeValueAsString(role);
        String jsonRoleDtoDuplicate = objectMapper.writeValueAsString(roleDuplicate);

        mockMvc.perform(post(ROLE_URI)
                        .content(jsonRoleDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(post(ROLE_URI)
                        .content(jsonRoleDtoDuplicate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private ErrorResponseDto generateErrorResponseDto(Integer code, String message) {
        return new ErrorResponseDto(code, message);
    }
}
