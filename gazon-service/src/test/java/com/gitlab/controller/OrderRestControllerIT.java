package com.gitlab.controller;

import com.gitlab.TestUtil;
import com.gitlab.dto.OrderDto;
import com.gitlab.dto.SelectedProductDto;
import com.gitlab.dto.ShoppingCartDto;
import com.gitlab.enums.OrderStatus;
import com.gitlab.mapper.OrderMapper;
import com.gitlab.mapper.SelectedProductMapper;
import com.gitlab.mapper.ShoppingCartMapper;
import com.gitlab.mapper.UserMapper;
import com.gitlab.model.Order;
import com.gitlab.model.ShoppingCart;
import com.gitlab.model.User;
import com.gitlab.repository.ShoppingCartRepository;
import com.gitlab.service.OrderService;
import com.gitlab.service.PersonalAddressService;
import com.gitlab.service.ShoppingCartService;
import com.gitlab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "admin1", roles = "ADMIN")
public class OrderRestControllerIT extends AbstractIntegrationTest {

    private static final String ORDER_URN = "/api/order";
    private static final String ORDER_URI = URL + ORDER_URN;

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SelectedProductMapper selectedProductMapper;
    @Autowired
    private PersonalAddressService personalAddressService;

    @Test
    @Transactional
    @WithMockUser(username = "user1", roles = "USER")
    void should_get_all_orders() throws Exception {
        User user = userService.getAuthenticatedUser();
        OrderDto generatedOrderDto = TestUtil.generateOrderDto(user.getId(),
                                                               personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));
        generatedOrderDto.setSelectedProducts(
                shoppingCartRepository.findByUser_Id(user.getId()).get()
                        .getSelectedProducts().stream()
                        .map(product -> selectedProductMapper.toDto(product))
                        .collect(Collectors.toSet()));

        orderService.saveDto(generatedOrderDto);

        var response = orderService.getPage(null, null);
        var expected = objectMapper.writeValueAsString(orderMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(ORDER_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", roles = "USER")
    void should_get_page() throws Exception {
        User user = userService.getAuthenticatedUser();
        OrderDto generatedOrderDto = TestUtil.generateOrderDto(user.getId(),
                                                               personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));
        generatedOrderDto.setSelectedProducts(
                shoppingCartRepository.findByUser_Id(user.getId()).get()
                        .getSelectedProducts().stream()
                        .map(product -> selectedProductMapper.toDto(product))
                        .collect(Collectors.toSet()));

        orderService.saveDto(generatedOrderDto);

        int page = 0;
        int size = 2;
        String parameters = "?page=" + page + "&size=" + size;

        Page<Order> response = orderService.getPage(page, size);
        assertFalse(response.getContent().isEmpty());

        String expected = objectMapper.writeValueAsString(orderMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(ORDER_URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_get_page_with_incorrect_parameters() throws Exception {
        int page = 0;
        int size = -2;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(ORDER_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_page_without_content() throws Exception {
        int page = 10;
        int size = 100;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(ORDER_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    void should_get_order_by_id() throws Exception {
        long id = userService.getAuthenticatedUser().getId();
        OrderDto orderDto = TestUtil.generateOrderDto(
                id,
                personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));

        orderDto.setSelectedProducts(shoppingCartRepository.findByUser_Id(id)
                                             .get()
                                             .getSelectedProducts()
                                             .stream()
                                             .map(selectedProduct -> selectedProductMapper.toDto(selectedProduct))
                                             .collect(Collectors.toSet()));

        Optional<OrderDto> optionalOrderDto = orderService.saveDto(orderDto);

        String expected = objectMapper.writeValueAsString(optionalOrderDto.get());

        mockMvc.perform(get(ORDER_URI + "/{id}", optionalOrderDto.get().getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_create_order() throws Exception {
        long id = userService.getAuthenticatedUser().getId();
        OrderDto orderDto = TestUtil.generateOrderDto(
                id,
                personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));

        orderDto.setSelectedProducts(shoppingCartRepository.findByUser_Id(id)
                                             .get()
                                             .getSelectedProducts()
                                             .stream()
                                             .map(selectedProduct -> selectedProductMapper.toDto(selectedProduct))
                                             .collect(Collectors.toSet()));

        String jsonOrderDto = objectMapper.writeValueAsString(orderDto);

        mockMvc.perform(post(ORDER_URI)
                                .content(jsonOrderDto)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    void check_null_update() throws Exception {
        User user = userService.getAuthenticatedUser();
        long id = user.getId();

        OrderDto orderDto = TestUtil.generateOrderDto(id,
                                                      personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));

        orderDto.setSelectedProducts(shoppingCartRepository.findByUser_Id(id)
                                             .get()
                                             .getSelectedProducts()
                                             .stream()
                                             .map(selectedProduct -> selectedProductMapper.toDto(selectedProduct))
                                             .collect(Collectors.toSet()));

        orderDto = orderService.saveDto(orderDto).get();
        int numberOfEntitiesExpected = orderService.findAll().size();

        String checkJsonOrderDto = objectMapper.writeValueAsString(orderDto);

        orderDto.setShippingAddressDto(null);
        orderDto.setOrderCode(null);
        orderDto.setShippingDate(null);
        orderDto.setCreateDateTime(null);
        orderDto.setSum(null);
        orderDto.setDiscount(null);
        orderDto.setBagCounter(null);
        orderDto.setOrderStatus(null);

        String jsonOrderDto = objectMapper.writeValueAsString(orderDto);
        mockMvc.perform(patch(ORDER_URI + "/{id}", orderDto.getId())
                                .content(jsonOrderDto)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(checkJsonOrderDto));
//                .andExpect(result -> assertThat(orderService.findAll().size(),
//                                                equalTo(numberOfEntitiesExpected)));

        // for some reason securityContext clears itself after
        // mockMvc.perform() call and you have to set it up again manually
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken("admin1", null, AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        assert (orderService.findAll().size() == numberOfEntitiesExpected);
    }

    @Test
    @Transactional
    void should_update_order_by_id() throws Exception {
        User user = userService.getAuthenticatedUser();
        long id = user.getId();

        OrderDto orderDto = TestUtil.generateOrderDto(id,
                                                      personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));

        orderDto.setSelectedProducts(shoppingCartRepository.findByUser_Id(id)
                                             .get()
                                             .getSelectedProducts()
                                             .stream()
                                             .map(selectedProduct -> selectedProductMapper.toDto(selectedProduct))
                                             .collect(Collectors.toSet()));

        orderDto = orderService.saveDto(orderDto).get();

        id = orderDto.getId();
        int numberOfEntitiesExpected = orderService.findAll().size();

        orderDto.setOrderStatus(OrderStatus.IN_PROGRESS);
        orderDto.setCreateDateTime(LocalDateTime.now());

        String jsonOrderDto = objectMapper.writeValueAsString(orderDto);
        String expected = objectMapper.writeValueAsString(orderDto);

        mockMvc.perform(patch(ORDER_URI + "/{id}", id)
                                .content(jsonOrderDto)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
//                .andExpect(result -> assertThat(orderService.findAll().size(),
//                                                equalTo(numberOfEntitiesExpected)));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken("admin1", null, AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        assert (orderService.findAll().size() == numberOfEntitiesExpected);
    }

    @Test
    void should_return_not_found_when_get_order_by_non_existent_id() throws Exception {
        long id = 10L;
        mockMvc.perform(get(ORDER_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_not_found_when_update_order_by_non_existent_id() throws Exception {
        long id = 10L;
        OrderDto orderDto = TestUtil.generateOrderDto(userService.saveDto(TestUtil.generateUserDto()).getId(),
                                                      personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));
        String jsonOrderDto = objectMapper.writeValueAsString(orderDto);

        mockMvc.perform(patch(ORDER_URI + "/{id}", id)
                                .content(jsonOrderDto)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_use_user_assigned_id_in_database_for_order() throws Exception {
        long id = userService.getAuthenticatedUser().getId();
        OrderDto orderDto = TestUtil.generateOrderDto(id,
                                                      personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));

        orderDto.setId(9999L);

        orderDto.setSelectedProducts(shoppingCartRepository.findByUser_Id(id)
                                             .get()
                                             .getSelectedProducts()
                                             .stream()
                                             .map(selectedProduct -> selectedProductMapper.toDto(selectedProduct))
                                             .collect(Collectors.toSet()));

        String jsonOrderDto = objectMapper.writeValueAsString(orderDto);

        MockHttpServletResponse response = mockMvc.perform(post(ORDER_URI)
                                                                   .content(jsonOrderDto)
                                                                   .contentType(MediaType.APPLICATION_JSON)
                                                                   .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        OrderDto createdOrderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        Assertions.assertNotEquals(orderDto.getId(), createdOrderDto.getId());
    }

    @Test
    void should_return_all_orders_for_admin() throws Exception {
        long id = userService.getAuthenticatedUser().getId();
        OrderDto orderDto = TestUtil.generateOrderDto(id,
                                                      personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));

        orderDto.setSelectedProducts(shoppingCartRepository.findByUser_Id(id)
                                             .get()
                                             .getSelectedProducts()
                                             .stream()
                                             .map(selectedProduct -> selectedProductMapper.toDto(selectedProduct))
                                             .collect(Collectors.toSet()));

        orderService.saveDto(orderDto);

        int numberOfOrders = orderService.findAll().size();

        mockMvc.perform(get(ORDER_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfOrders)));
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void should_return_only_user_orders() throws Exception {
        long id = userService.getAuthenticatedUser().getId();
        OrderDto orderDto = TestUtil.generateOrderDto(id,
                                                      personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));

        orderDto.setSelectedProducts(shoppingCartRepository.findByUser_Id(id)
                                             .get()
                                             .getSelectedProducts()
                                             .stream()
                                             .map(selectedProduct -> selectedProductMapper.toDto(selectedProduct))
                                             .collect(Collectors.toSet()));

        orderService.saveDto(orderDto);

        int numberOfOrders = orderService.findAll().size();

        mockMvc.perform(get(ORDER_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(numberOfOrders)));
    }

    @Test
    @Transactional
    void should_update_any_order_by_id_for_admin() throws Exception {
        long id = userService.saveDto(TestUtil.generateUserDto()).getId();

        Optional<ShoppingCart> optionalShoppingCart = shoppingCartRepository.findByUser_Id(id);
        ShoppingCart shoppingCart = optionalShoppingCart.get();

        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toDto(shoppingCart);
        shoppingCartDto.setSelectedProducts(Set.of(new SelectedProductDto()));
        shoppingCartService.saveDto(shoppingCartDto);

        // creating and saving order for regular user, which is going to be updated by admin later
        OrderDto orderDto = TestUtil.generateOrderDto(id,
                                                      personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));
        orderDto.setOrderCode("228");
        orderDto = orderService.saveDto(orderDto).get();

        OrderDto patch = new OrderDto();
        patch.setOrderCode("69");
        String jsonPatch = objectMapper.writeValueAsString(patch);

        OrderDto updatedOrderDto = TestUtil.generateOrderDto(
                id,
                orderDto.getShippingAddressDto());
        updatedOrderDto.setOrderCode("69");
        updatedOrderDto.setId(orderDto.getId());
        updatedOrderDto.setCreateDateTime(orderDto.getCreateDateTime());
        String updatedJson = objectMapper.writeValueAsString(updatedOrderDto);


        mockMvc.perform(patch(ORDER_URI + "/{id}", orderDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(jsonPatch))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(updatedJson));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", roles = "USER_LEAD")
    void should_update_own_order_by_id_for_user() throws Exception {
        long id = userService.getAuthenticatedUser().getId();

        Optional<ShoppingCart> optionalShoppingCart = shoppingCartRepository.findByUser_Id(id);
        ShoppingCart shoppingCart = optionalShoppingCart.get();

        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toDto(shoppingCart);
        shoppingCartDto.setSelectedProducts(Set.of(new SelectedProductDto()));
        shoppingCartService.saveDto(shoppingCartDto);

        OrderDto orderDto = TestUtil.generateOrderDto(id,
                                                      personalAddressService.saveDto(TestUtil.generatePersonalAddressDto()));
        orderDto.setOrderCode("228");
        orderDto = orderService.saveDto(orderDto).get();

        OrderDto patch = new OrderDto();
        patch.setOrderCode("69");
        String jsonPatch = objectMapper.writeValueAsString(patch);

        OrderDto updatedOrderDto = TestUtil.generateOrderDto(
                id,
                orderDto.getShippingAddressDto());
        updatedOrderDto.setOrderCode("69");
        updatedOrderDto.setId(orderDto.getId());
        updatedOrderDto.setCreateDateTime(orderDto.getCreateDateTime());
        String updatedJson = objectMapper.writeValueAsString(updatedOrderDto);


        mockMvc.perform(patch(ORDER_URI + "/{id}", orderDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(jsonPatch))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(updatedJson));
    }
}
