package com.gitlab.controller;

import com.gitlab.TestUtil;
import com.gitlab.dto.RoleDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.RoleMapper;
import com.gitlab.model.Role;
import com.gitlab.service.RoleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleRestControllerIT extends AbstractIntegrationTest {

    private static final String ROLE_URN = "/api/role";
    private static final String ROLE_URI = URL + ROLE_URN;

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleMapper roleMapper;

    @Test
    void should_get_payment_by_id() throws Exception {
        long id;

        RoleDto savedRoleDto = TestUtil.generateRoleDto();

        id = roleService.saveDto(savedRoleDto).getId();
        savedRoleDto.setId(id);

        String expected = objectMapper.writeValueAsString(savedRoleDto);

        mockMvc.perform(get(ROLE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_when_role_not_existent() throws Exception {
        long id = -1L;

        mockMvc.perform(get(ROLE_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional(readOnly = true)
    void should_get_all_roles() throws Exception {
        var response = roleService.getPage(null, null);
        var expected = objectMapper.writeValueAsString(roleMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(ROLE_URI))
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

        var response = roleService.getPage(page, size);
        assertFalse(response.getContent().isEmpty());

        var expected = objectMapper.writeValueAsString(roleMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(ROLE_URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_get_page_with_incorrect_parameters() throws Exception {
        int page = 0;
        int size = -2;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(ROLE_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_page_without_content() throws Exception {
        int page = 10;
        int size = 100;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(ROLE_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    @Test
    void should_create_role() throws Exception {
        RoleDto paymentDto = TestUtil.generateRoleDto();

        String jsonPaymentDto = objectMapper.writeValueAsString(paymentDto);

        mockMvc.perform(post(ROLE_URI)
                        .content(jsonPaymentDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void should_update_role_by_id() throws Exception {
        Role savedRole = roleService.save(roleMapper.toEntity(TestUtil.generateRoleDto()));
        RoleDto dto = roleMapper.toDto(savedRole);

        dto.setRoleName("UPDATE_TEST_NAME");
        String jsonRoleDto = objectMapper.writeValueAsString(dto);
        String expected = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch(ROLE_URI + "/{id}", savedRole.getId())
                        .content(jsonRoleDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_if_role_not_exists() throws Exception {
        RoleDto dto = TestUtil.generateRoleDto();

        String jsonRoleDto = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch(ROLE_URI + "/{id}", 9999)
                        .content(jsonRoleDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_role_by_id() throws Exception {
        RoleDto roleDto = TestUtil.generateRoleDto();
        long savedRoleId = roleService.saveDto(roleDto).getId();

        mockMvc.perform(delete(ROLE_URI + "/{id}", savedRoleId))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(ROLE_URI + "/{id}", savedRoleId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_use_user_assigned_id_in_database_for_role() throws Exception {
        RoleDto roleDto = TestUtil.generateRoleDto();
        roleDto.setId(9999L);

        String jsonRoleDto = objectMapper.writeValueAsString(roleDto);

        MockHttpServletResponse response = mockMvc.perform(post(ROLE_URI)
                        .content(jsonRoleDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        RoleDto createdRoleDto = objectMapper.readValue(response.getContentAsString(), RoleDto.class);
        Assertions.assertNotEquals(roleDto.getId(), createdRoleDto.getId());
    }
}