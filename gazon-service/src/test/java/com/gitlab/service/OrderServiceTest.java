package com.gitlab.service;

import com.gitlab.dto.OrderDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.exception.handler.UserDoesNotHaveAccessException;
import com.gitlab.exception.handler.WrongSelectedProductsException;
import com.gitlab.mapper.*;
import com.gitlab.model.Order;
import com.gitlab.model.Role;
import com.gitlab.model.SelectedProduct;
import com.gitlab.model.ShoppingCart;
import com.gitlab.model.User;
import com.gitlab.repository.OrderRepository;
import com.gitlab.repository.ProductRepository;
import com.gitlab.repository.ShoppingCartRepository;
import com.gitlab.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    private List<Order> generateOrders() {
        return List.of(
                Order.builder().user(User.builder().id(1L).username("user1").build()).id(1L).build(),
                Order.builder().user(User.builder().id(1L).username("user1").build()).id(2L).build(),
                Order.builder().user(User.builder().id(2L).username("user2").build()).id(3L).build(),
                Order.builder().user(User.builder().id(3L).username("user3").build()).id(4L).build(),
                Order.builder().user(User.builder().id(4L).username("user4").build()).id(5L).build(),
                Order.builder().user(User.builder().id(5L).username("user5").build()).id(6L).build()
        );
    }

    private List<Order> generateOrders(String username) {
        return generateOrders().stream().filter(order -> order.getUser().getUsername().equals(username)).toList();
    }

    private Order generateOrder() {
        Order order = new Order();
        order.setEntityStatus(EntityStatus.ACTIVE);
        order.setId(1L);
        return order;
    }

    private OrderDto generateOrderDto() {
        return new OrderDto();
    }

    @Test
    void should_find_only_their_own_orders() {
        User user = User.builder()
                .id(1L).username("user1")
                .rolesSet(Set.of(Role.builder().name("ROLE_USER_LEAD").build()))
                .build();

        when(orderRepository.findAll(user.getUsername())).thenReturn(generateOrders(user.getUsername()));
        when(userService.getAuthenticatedUser()).thenReturn(user);

        List<Order> expectedOrders = generateOrders(user.getUsername());
        List<Order> actualOrders = orderService.findAll();

        assertEquals(expectedOrders, actualOrders);
    }

    @Test
    void should_find_all_orders_if_admin() {
        User user = User.builder()
                .id(1L).username("admin1")
                .rolesSet(Set.of(Role.builder().name("ROLE_ADMIN").build()))
                .build();

        when(orderRepository.findAll()).thenReturn(generateOrders());
        when(userService.getAuthenticatedUser()).thenReturn(user);

        List<Order> expectedOrders = generateOrders();
        List<Order> actualOrders = orderService.findAll();

        assertEquals(expectedOrders, actualOrders);
    }

    @Test
    void should_find_only_their_own_order_by_id() {
        User user = User.builder()
                .id(1L).username("user1")
                .rolesSet(Set.of(Role.builder().name("ROLE_USER_LEAD").build()))
                .build();

        Long id = 1L;

        when(orderRepository.findById(id, user.getUsername())).thenReturn(Optional.of(generateOrder()));
        when(userService.getAuthenticatedUser()).thenReturn(user);

        Order expectedOrder = generateOrder();
        Optional<Order> actualOrder = orderService.findById(id);

        assertEquals(expectedOrder, actualOrder.orElse(null));
    }

    @Test
    void should_find_any_order_by_id_if_admin() {
        User user = User.builder()
                .id(1L).username("admin1")
                .rolesSet(Set.of(Role.builder().name("ROLE_ADMIN").build()))
                .build();

        Long id = 1L;

        when(orderRepository.findById(id)).thenReturn(Optional.of(generateOrder()));
        when(userService.getAuthenticatedUser()).thenReturn(user);

        Order expectedOrder = generateOrder();
        Optional<Order> actualOrder = orderService.findById(id);

        assertEquals(expectedOrder, actualOrder.orElse(null));
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void should_save_order() {
        User mockUser = new User();
        mockUser.setId(1L);
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        OrderDto mockOrderDto = new OrderDto();
        mockOrderDto.setUserId(1L);

        Order mockOrder = new Order();
        SelectedProduct selectedProduct = new SelectedProduct();
        Set<SelectedProduct> selectedProducts = Set.of(selectedProduct);
        mockOrder.setSelectedProducts(selectedProducts);

        ShoppingCart mockCart = mock(ShoppingCart.class);
        when(mockCart.getSelectedProducts()).thenReturn(selectedProducts);

        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(mockOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(orderMapper.toDto(any(Order.class))).thenReturn(mockOrderDto);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(shoppingCartRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(mockCart));

        Order actualResult = orderMapper.toEntity(orderService.saveDto(orderMapper.toDto(mockOrder)).get());
        assertEquals(actualResult, mockOrder);
    }

    @Test
    void should_fail_if_order_is_created_by_wrong_user() {
        User userCreatingOrder = User.builder()
                .id(1L).username("user1")
                .rolesSet(Set.of(Role.builder().name("ROLE_USER_LEAD").build()))
                .build();

        User userInsideOrder = User.builder()
                .id(2L).username("user2")
                .rolesSet(Set.of(Role.builder().name("ROLE_USER_LEAD").build()))
                .build();

        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(userInsideOrder.getId());

        Order mockOrder = new Order();
        SelectedProduct selectedProduct = new SelectedProduct();
        Set<SelectedProduct> selectedProducts = Set.of(selectedProduct);
        mockOrder.setSelectedProducts(selectedProducts);

        when(userService.getAuthenticatedUser()).thenReturn(userCreatingOrder);
        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(mockOrder);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userInsideOrder));

        assertThrows(UserDoesNotHaveAccessException.class, () -> {
            orderService.saveDto(orderDto);
        });
    }

    @Test
    void should_not_fail_if_order_is_created_by_wrong_user_admin() {
        User userCreatingOrder = User.builder()
                .id(1L).username("user1")
                .rolesSet(Set.of(Role.builder().name("ROLE_ADMIN").build()))
                .build();

        User userInsideOrder = User.builder()
                .id(2L).username("user2")
                .rolesSet(Set.of(Role.builder().name("ROLE_USER_LEAD").build()))
                .build();

        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(userInsideOrder.getId());

        Order mockOrder = new Order();
        SelectedProduct selectedProduct = new SelectedProduct();
        Set<SelectedProduct> selectedProducts = Set.of(selectedProduct);
        mockOrder.setSelectedProducts(selectedProducts);

        ShoppingCart mockCart = mock(ShoppingCart.class);
        when(mockCart.getSelectedProducts()).thenReturn(selectedProducts);

        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(mockOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userInsideOrder));
        when(shoppingCartRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(mockCart));
        when(userService.getAuthenticatedUser()).thenReturn(userCreatingOrder);

        Optional<OrderDto> optionalOrderDto = orderService.saveDto(orderDto);

        assertEquals(orderDto, optionalOrderDto.orElse(null));
    }

    @Test
    void should_fail_if_created_order_has_products_which_are_not_present_in_shopping_cart() {
        User userCreatingOrder = User.builder()
                .id(1L).username("user1")
                .rolesSet(Set.of(Role.builder().name("ROLE_ADMIN").build()))
                .build();

        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(userCreatingOrder.getId());

        Order mockOrder = new Order();
        SelectedProduct selectedProduct = new SelectedProduct();
        selectedProduct.setId(25L);
        Set<SelectedProduct> selectedProducts = Set.of(selectedProduct);
        mockOrder.setSelectedProducts(selectedProducts);

        ShoppingCart shoppingCart = mock(ShoppingCart.class);

        when(userService.getAuthenticatedUser()).thenReturn(userCreatingOrder);
        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(mockOrder);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userCreatingOrder));
        when(shoppingCartRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(shoppingCart));

        assertThrows(WrongSelectedProductsException.class, () -> {
            orderService.saveDto(orderDto);
        });
    }

    @Test
    void should_fail_if_order_is_updated_by_wrong_user() {
        User mockUser = User.builder()
                .id(1L).username("user1")
                .rolesSet(Set.of(Role.builder().name("ROLE_USER_LEAD").build()))
                .build();
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        User mockUser2 = new User();
        mockUser2.setId(2L);

        long id = 1L;
        OrderDto orderDtoToUpdate = generateOrderDto();
        orderDtoToUpdate.setOrderCode("updatesOrder");

        Order orderBeforeUpdate = new Order(id, "unmodifiedCode");
        orderBeforeUpdate.setUser(mockUser2);

        when(orderRepository.findById(id)).thenReturn(Optional.of(orderBeforeUpdate));


        assertThrows(UserDoesNotHaveAccessException.class, () -> {
            orderService.updateDto(id, orderDtoToUpdate);
        });
    }

    @Test
    void should_not_fail_if_order_is_updated_by_wrong_user_admin() {
        User mockUser = User.builder()
                .id(1L).username("user1")
                .rolesSet(Set.of(Role.builder().name("ROLE_ADMIN").build()))
                .build();
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        User mockUser2 = new User();
        mockUser2.setId(2L);

        long id = 1L;
        OrderDto orderDtoToUpdate = generateOrderDto();
        orderDtoToUpdate.setOrderCode("updatedOrder");

        OrderDto orderBeforeUpdateDto = new OrderDto();
        orderBeforeUpdateDto.setId(id);
        orderBeforeUpdateDto.setOrderCode("updatesOrder");

        Order orderBeforeUpdate = new Order(id, "unmodifiedCode");
        orderBeforeUpdate.setUser(mockUser2);

        Order updatedOrder = new Order(id, "updatedOrder");

        when(orderRepository.findById(id)).thenReturn(Optional.of(orderBeforeUpdate));
        when(orderRepository.save(orderBeforeUpdate)).thenReturn(updatedOrder);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDtoToUpdate);

        Optional<OrderDto> actualResult = orderService.updateDto(id, orderDtoToUpdate);

        assertEquals(orderDtoToUpdate, actualResult.orElse(null));
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void should_update_order() {
        User mockUser = new User();
        mockUser.setId(1L);
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        long id = 1L;
        OrderDto orderDtoToUpdate = generateOrderDto();
        orderDtoToUpdate.setOrderCode("updatesOrder");

        Order orderBeforeUpdate = new Order(id, "unmodifiedCode");
        orderBeforeUpdate.setUser(mockUser);

        Order updatedOrder = new Order(id, "updatesOrder");

        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDtoToUpdate);
        when(orderMapper.toEntity(orderDtoToUpdate)).thenReturn(updatedOrder);

        when(orderRepository.findById(id)).thenReturn(Optional.of(orderBeforeUpdate));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Optional<Order> actualResult = Optional.of(orderMapper
                                                           .toEntity(orderService.updateDto(id, orderDtoToUpdate).get()));

        assertEquals(updatedOrder, actualResult.orElse(null));
    }

    @Test
    void should_not_update_order_when_entity_not_found() {
        long id = 1L;
        OrderDto orderDtoToUpdate = new OrderDto();
        orderDtoToUpdate.setOrderCode("updatesOrder");

        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        Optional<OrderDto> actualDtoResult = orderService.updateDto(id, orderDtoToUpdate);

        verify(orderRepository, never()).save(any());
        assertNull(actualDtoResult.orElse(null));
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void should_not_updated_orderCode_field_if_null() {
        User mockUser = new User();
        mockUser.setId(1L);
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        long id = 1L;
        OrderDto orderDtoToUpdate = new OrderDto();
        orderDtoToUpdate.setOrderCode(null); // Intentionally set to null

        Order orderBeforeUpdate = new Order();
        orderBeforeUpdate.setId(id);
        orderBeforeUpdate.setOrderCode("unmodifiedCode");
        orderBeforeUpdate.setUser(mockUser); // Ensure the user is set
        orderBeforeUpdate.setSelectedProducts(new HashSet<>()); // Initialize selected products

        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDtoToUpdate);
        when(orderMapper.toEntity(orderDtoToUpdate)).thenReturn(orderBeforeUpdate);

        when(orderRepository.findById(id)).thenReturn(Optional.of(orderBeforeUpdate));
        when(orderRepository.save(orderBeforeUpdate)).thenReturn(orderBeforeUpdate);

        Optional<Order> actualResult = Optional.of(orderMapper.toEntity(orderService.updateDto(id, orderDtoToUpdate).get()));

        verify(orderRepository).save(orderBeforeUpdate);
        assertEquals(orderBeforeUpdate, actualResult.orElse(null));
        assertEquals("unmodifiedCode", orderBeforeUpdate.getOrderCode());
    }

    @Test
    void should_delete_order() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setRolesSet(Set.of(Role.builder().name("ROLE_USER_LEAD").id(8L).build()));
        mockUser.setUsername("user");
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        Order order = generateOrder();
        when(orderRepository.findById(order.getId(), mockUser.getUsername())).thenReturn(Optional.of(order));
        Order delete = orderService.delete(order.getId()).orElseGet(null);
        assertEquals(EntityStatus.DELETED, delete.getEntityStatus());
    }

    @Test
    void should_not_delete_order_when_entity_not_found() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setRolesSet(Set.of(Role.builder().name("ROLE_USER_LEAD").id(8L).build()));
        mockUser.setUsername("user");
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        long id = 1L;
        when(orderRepository.findById(id, mockUser.getUsername())).thenReturn(Optional.empty());

        orderService.delete(id);

        verify(orderRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateOverdueOrders_NoOverdueOrders() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Order> notPaidOrders = new ArrayList<>();

        Order order1 = new Order();
        order1.setCreateDateTime(currentTime.plusMinutes(2));
        notPaidOrders.add(order1);

        Order order2 = new Order();
        order2.setCreateDateTime(currentTime.minusMinutes(2));
        notPaidOrders.add(order2);

        when(orderRepository.findOrdersWithNotPaidStatus()).thenReturn(notPaidOrders);
        orderService.updateOverdueOrders();

        verify(orderRepository, never()).save(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateOverdueOrders_WithOverdueOrders() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Order> notPaidOrders = new ArrayList<>();

        Order order1 = new Order();
        order1.setCreateDateTime(currentTime.minusHours(2));
        notPaidOrders.add(order1);

        Order order2 = new Order();
        order2.setCreateDateTime(currentTime.minusDays(1));
        notPaidOrders.add(order2);

        when(orderRepository.findOrdersWithNotPaidStatus()).thenReturn(notPaidOrders);
        orderService.updateOverdueOrders();

        verify(orderRepository, times(2)).save(any());
        verify(productRepository, never()).save(any());
    }
}