package com.gitlab.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.gitlab.TestUtil;
import com.gitlab.client.PaymentClient;
import com.gitlab.dto.OrderDto;
import com.gitlab.dto.PaymentDto;
import com.gitlab.enums.OrderStatus;
import com.gitlab.enums.PaymentStatus;
import com.gitlab.mapper.PaymentMapper;
import com.gitlab.mapper.SelectedProductMapper;
import com.gitlab.repository.ShoppingCartRepository;
import com.gitlab.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnableFeignClients
@WithMockUser(username = "admin1", roles = "ADMIN")
class PaymentRestControllerIT extends AbstractIntegrationTest {
    private static final String PAYMENT_URN = "/api/payment";
    private static final String PAYMENT_URI = URL + PAYMENT_URN;

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private PersonalAddressService personalAddressService;
    @Autowired
    private BankCardService bankCardService;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private SelectedProductMapper selectedProductMapper;

    @MockBean
    private PaymentClient paymentClient;
    @InjectMocks
    private PaymentService paymentServiceWithMocks;

    @Test
    @Transactional(readOnly = true)
    void should_get_all_payments() throws Exception {

        var response = paymentService.getPage(null, null);
        var expected = objectMapper.writeValueAsString(paymentMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(PAYMENT_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_get_page_with_incorrect_parameters() throws Exception {
        int page = 0;
        int size = -2;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(PAYMENT_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_page_without_content() throws Exception {
        int page = 10;
        int size = 100;
        String parameters = "?page=" + page + "&size=" + size;

        mockMvc.perform(get(PAYMENT_URI + parameters))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional(readOnly = true)
    void should_get_page() throws Exception {
        int page = 0;
        int size = 2;
        String parameters = "?page=" + page + "&size=" + size;

        var response = paymentService.getPage(page, size);
        assertFalse(response.getContent().isEmpty());

        var expected = objectMapper.writeValueAsString(paymentMapper.toDtoList(response.getContent()));

        mockMvc.perform(get(PAYMENT_URI + parameters))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }


    @Test
    void should_get_payment_by_id() throws Exception {
        long id = 1L;
        String expected = objectMapper.writeValueAsString(
                paymentMapper.toDto(
                        paymentService
                                .findById(id)
                                .orElse(null))
        );

        mockMvc.perform(get(PAYMENT_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void should_return_not_found_when_get_payment_by_non_existent_id() throws Exception {
        long id = 10L;
        mockMvc.perform(get(PAYMENT_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // @Test
    @Transactional
    void should_create_payment() throws Exception {
        MockitoAnnotations.openMocks(this);

        long userId = userService.getAuthenticatedUser().getId();

        OrderDto orderDto = TestUtil.generateOrderDto(
                userId,
                personalAddressService.saveDto(TestUtil.generatePersonalAddressDto())
        );
        orderDto.setOrderStatus(OrderStatus.NOT_PAID);

        orderDto.setSelectedProducts(shoppingCartRepository.findByUser_Id(userId)
                                             .get()
                                             .getSelectedProducts()
                                             .stream()
                                             .map(selectedProduct -> selectedProductMapper.toDto(selectedProduct))
                                             .collect(Collectors.toSet()));

        Optional<OrderDto> optionalOrderDto = orderService.saveDto(orderDto);

        PaymentDto paymentDto = TestUtil.generatePaymentDto(
                optionalOrderDto.get().getId(),
                userId,
                bankCardService.saveDto(TestUtil.generateBankCardDto())
        );
        paymentDto.setPaymentStatus(PaymentStatus.NOT_PAID);

        PaymentDto mockPaymentDtoResponse = new PaymentDto();
        mockPaymentDtoResponse.setId(paymentDto.getId());
        mockPaymentDtoResponse.setUserId(paymentDto.getUserId());
        mockPaymentDtoResponse.setSum(paymentDto.getSum());
        mockPaymentDtoResponse.setCreateDateTime(paymentDto.getCreateDateTime());
        mockPaymentDtoResponse.setBankCardDto(paymentDto.getBankCardDto());
        mockPaymentDtoResponse.setPaymentStatus(PaymentStatus.PAID);
        mockPaymentDtoResponse.setOrderId(optionalOrderDto.get().getId());
        ResponseEntity<PaymentDto> mockResponseEntity = ResponseEntity.ok(mockPaymentDtoResponse);
        when(paymentClient.makePayment(any(PaymentDto.class))).thenReturn(mockResponseEntity);

        String jsonPaymentDto = objectMapper.writeValueAsString(paymentDto);

        mockMvc.perform(post(PAYMENT_URI)
                                .content(jsonPaymentDto)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.paymentStatus").value(PaymentStatus.PAID.name()))
                .andExpect(status().isCreated());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken("admin1", null, AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        OrderDto savedOrderDto = orderService.findByIdDto(optionalOrderDto.get().getId()).orElseThrow();
        assertEquals(OrderStatus.PAID, savedOrderDto.getOrderStatus());
    }

    @Test
    void should_update_payment_by_id() throws Exception {
        MockitoAnnotations.openMocks(this);

        long userId = userService.getAuthenticatedUser().getId();

        OrderDto orderDto = TestUtil.generateOrderDto(
                userId,
                personalAddressService.saveDto(TestUtil.generatePersonalAddressDto())
        );

        orderDto.setSelectedProducts(shoppingCartRepository.findByUser_Id(userId)
                                             .get()
                                             .getSelectedProducts()
                                             .stream()
                                             .map(selectedProduct -> selectedProductMapper.toDto(selectedProduct))
                                             .collect(Collectors.toSet()));

        Optional<OrderDto> optionalOrderDto = orderService.saveDto(orderDto);

        PaymentDto paymentDto = TestUtil.generatePaymentDto(
                optionalOrderDto.get().getId(),
                userId,
                bankCardService.saveDto(TestUtil.generateBankCardDto())
        );

        PaymentDto mockPaymentDtoResponse = new PaymentDto();
        mockPaymentDtoResponse.setId(paymentDto.getId());
        mockPaymentDtoResponse.setUserId(paymentDto.getUserId());
        mockPaymentDtoResponse.setSum(paymentDto.getSum());
        mockPaymentDtoResponse.setCreateDateTime(paymentDto.getCreateDateTime());
        mockPaymentDtoResponse.setBankCardDto(paymentDto.getBankCardDto());
        mockPaymentDtoResponse.setPaymentStatus(PaymentStatus.PAID);
        mockPaymentDtoResponse.setOrderId(optionalOrderDto.get().getId());
        ResponseEntity<PaymentDto> mockResponseEntity = ResponseEntity.ok(mockPaymentDtoResponse);
        when(paymentClient.makePayment(any(PaymentDto.class))).thenReturn(mockResponseEntity);

        String jsonPaymentDto = objectMapper.writeValueAsString(paymentDto);

        //создаем переменную хранящую response, получаем id сохраненной сущности и используем его для
        //дальнейшей очистки БД от созданной сущности, чтобы избавиться от сайд-эффектов и хардкода
        ResultActions resultActions = mockMvc.perform(post(PAYMENT_URI)
                                                              .content(jsonPaymentDto)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        JsonNode createdEntity = objectMapper.readTree(contentAsString);
        long id = createdEntity.get("id").asLong();

        paymentDto.setId(id);
        paymentDto.setSum(new BigDecimal(2000));

        jsonPaymentDto = objectMapper.writeValueAsString(paymentDto);
        String expected = objectMapper.writeValueAsString(paymentDto);

        mockMvc.perform(patch(PAYMENT_URI + "/{id}", id)
                                .content(jsonPaymentDto)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));

        mockMvc.perform(delete(PAYMENT_URI + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void should_return_not_found_when_update_payment_by_non_existent_id() throws Exception {
        long paymentId = 9999L;

        PaymentDto paymentDto = TestUtil.generatePaymentDto(paymentId, 9999L, TestUtil.generateBankCardDto());

        String jsonPaymentDto = objectMapper.writeValueAsString(paymentDto);

        mockMvc.perform(patch(PAYMENT_URI + "/{id}", paymentId)
                                .content(jsonPaymentDto)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // @Test
    @Transactional
    void should_use_user_assigned_id_in_database_for_payment() throws Exception {
        MockitoAnnotations.openMocks(this);

        long userId = userService.getAuthenticatedUser().getId();

        OrderDto orderDto = TestUtil.generateOrderDto(
                userId,
                personalAddressService.saveDto(TestUtil.generatePersonalAddressDto())
        );

        orderDto.setSelectedProducts(shoppingCartRepository.findByUser_Id(userId)
                                             .get()
                                             .getSelectedProducts()
                                             .stream()
                                             .map(selectedProduct -> selectedProductMapper.toDto(selectedProduct))
                                             .collect(Collectors.toSet()));

        Optional<OrderDto> optionalOrderDto = orderService.saveDto(orderDto);

        PaymentDto paymentDto = TestUtil.generatePaymentDto(
                optionalOrderDto.get().getId(),
                userId,
                bankCardService.saveDto(TestUtil.generateBankCardDto())
        );

        paymentDto.setId(9999L);

        PaymentDto mockPaymentDtoResponse = new PaymentDto();
        mockPaymentDtoResponse.setUserId(paymentDto.getUserId());
        mockPaymentDtoResponse.setSum(paymentDto.getSum());
        mockPaymentDtoResponse.setCreateDateTime(paymentDto.getCreateDateTime());
        mockPaymentDtoResponse.setBankCardDto(paymentDto.getBankCardDto());
        mockPaymentDtoResponse.setPaymentStatus(PaymentStatus.PAID);
        mockPaymentDtoResponse.setOrderId(optionalOrderDto.get().getId());
        ResponseEntity<PaymentDto> mockResponseEntity = ResponseEntity.ok(mockPaymentDtoResponse);
        when(paymentClient.makePayment(any(PaymentDto.class))).thenReturn(mockResponseEntity);

        String jsonPaymentDto = objectMapper.writeValueAsString(paymentDto);
        MockHttpServletResponse response = mockMvc.perform(post(PAYMENT_URI)
                                                                   .content(jsonPaymentDto)
                                                                   .contentType(MediaType.APPLICATION_JSON)
                                                                   .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        PaymentDto createdPaymentDto = objectMapper.readValue(response.getContentAsString(), PaymentDto.class);
        Assertions.assertNotEquals(paymentDto.getId(), createdPaymentDto.getId());
    }
}