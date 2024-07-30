package com.gitlab.service;

import com.gitlab.dto.OrderDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.enums.OrderStatus;
import com.gitlab.exception.handler.UserDoesNotHaveAccessException;
import com.gitlab.exception.handler.WrongSelectedProductsException;
import com.gitlab.mapper.OrderMapper;
import com.gitlab.mapper.SelectedProductMapper;
import com.gitlab.mapper.ShippingAddressMapper;
import com.gitlab.mapper.UserMapper;
import com.gitlab.model.Order;
import com.gitlab.model.SelectedProduct;
import com.gitlab.model.ShoppingCart;
import com.gitlab.model.User;
import com.gitlab.repository.OrderRepository;
import com.gitlab.repository.ProductRepository;
import com.gitlab.repository.ShoppingCartRepository;
import com.gitlab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final ShippingAddressMapper shippingAddressMapper;
    private final SelectedProductMapper selectedProductMapper;
    private final UserMapper userMapper;
    private final ProductRepository productRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;


    public List<Order> findAll() {
        User user = userService.getAuthenticatedUser();
        return isAdmin(user)
                ? orderRepository.findAll()
                : orderRepository.findAll(user.getUsername());
    }

    public List<OrderDto> findAllDto() {
        List<Order> orders = findAll();
        return orders.stream().map(orderMapper::toDto).toList();
    }

    public Optional<Order> findById(Long id) {
        User user = userService.getAuthenticatedUser();
        String username = user.getUsername();

        Optional<Order> orderOptional = isAdmin(user)
                ? orderRepository.findById(id)
                : orderRepository.findById(id, username);

        return orderOptional.isPresent() && !orderOptional.get().getEntityStatus().equals(EntityStatus.DELETED)
                ? orderOptional
                : Optional.empty();
    }

    public Optional<OrderDto> findByIdDto(Long id) {
        return findById(id).map(orderMapper::toDto);
    }

    public Page<Order> getPage(Integer page, Integer size) {
        if (page == null || size == null) {
            List<Order> orders = findAll();
            if (orders.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(orders);
        }

        if (page < 0 || size < 1) {
            return Page.empty();
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        User user = userService.getAuthenticatedUser();
        return isAdmin(user)
                ? orderRepository.findAll(pageRequest)
                : orderRepository.findAll(pageRequest, user.getUsername());
    }

    public Page<OrderDto> getPageDto(Integer page, Integer size) {
        return getPage(page, size).map(orderMapper::toDto);
    }

    boolean authenticatedUserHasAccess(Order order) {
        User user = userService.getAuthenticatedUser();
        return user.getId().equals(order.getUser().getId()) || isAdmin(user);
    }

    boolean isAdmin(User user) {
        return user.getRolesSet().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }

    public Optional<OrderDto> saveDto(OrderDto orderDto) {
        Order order = orderMapper.toEntity(orderDto);
        order.setEntityStatus(EntityStatus.ACTIVE);
        order.setUser(userRepository.findById(orderDto.getUserId())
                              .orElseThrow(
                                      () -> new EntityNotFoundException(String.format("User with id %s was not found.", orderDto.getId()))
                              )
        );

        User user = userService.getAuthenticatedUser();
        if (!Objects.equals(user.getId(), order.getUser().getId()) && !isAdmin(user)) {
            throw new UserDoesNotHaveAccessException(
                    HttpStatus.BAD_REQUEST,
                    String.format("User with id %s can't create orders for someone else", user.getId()));
        }

        Optional<ShoppingCart> optionalUserShoppingCart = shoppingCartRepository.findByUser_Id(order.getUser().getId());

        if (optionalUserShoppingCart.isEmpty()) {
            return Optional.empty();
        }

        ShoppingCart userShoppingCart = optionalUserShoppingCart.get();

        Set<Long> selectedProductsIds = userShoppingCart
                .getSelectedProducts().stream()
                .map(SelectedProduct::getId)
                .collect(Collectors.toSet());

        boolean cartHasOrderProducts = order
                .getSelectedProducts().stream()
                .map(SelectedProduct::getId)
                .allMatch(selectedProductsIds::contains);

        if (!cartHasOrderProducts) {
            throw new WrongSelectedProductsException(
                    HttpStatus.BAD_REQUEST,
                    "Your order includes products that are not present in user's shopping cart"
            );
        }

        Order savedOrder = orderRepository.save(order);
        return Optional.of(orderMapper.toDto(savedOrder));
    }

    public Optional<OrderDto> updateDto(Long id, OrderDto orderDto) {
        Optional<Order> optionalSavedOrder = orderRepository.findById(id);
        if (optionalSavedOrder.isEmpty()) {
            return Optional.empty();
        }
        if (!authenticatedUserHasAccess(optionalSavedOrder.get())) {
            throw new UserDoesNotHaveAccessException(
                    HttpStatus.BAD_REQUEST,
                    String.format("This user does not have access to order with id %s", orderDto.getId()));
        }
        Order savedOrder = optionalSavedOrder.get();
        savedOrder.setEntityStatus(EntityStatus.ACTIVE);
        if (orderDto.getShippingAddressDto() != null) {
            savedOrder.setShippingAddress(shippingAddressMapper.toEntity(orderDto.getShippingAddressDto()));
        }
        if (orderDto.getShippingDate() != null) {
            savedOrder.setShippingDate(orderDto.getShippingDate());
        }
        if (orderDto.getOrderCode() != null) {
            savedOrder.setOrderCode(orderDto.getOrderCode());
        }
        if (orderDto.getCreateDateTime() != null) {
            savedOrder.setCreateDateTime(orderDto.getCreateDateTime());
        }
        if (orderDto.getSum() != null) {
            savedOrder.setSum(orderDto.getSum());
        }
        if (orderDto.getDiscount() != null) {
            savedOrder.setDiscount(orderDto.getDiscount());
        }
        if (orderDto.getBagCounter() != null) {
            savedOrder.setBagCounter(orderDto.getBagCounter());
        }
        if (orderDto.getUserId() != null) {
            savedOrder.setUser(userMapper.toEntity(userService.findById(orderDto.getUserId()).get()));
        }
        if (orderDto.getSelectedProducts() != null) {
            savedOrder.setSelectedProducts(orderDto.getSelectedProducts().stream().map(selectedProductMapper::toEntity).collect(Collectors.toSet()));
        }
        if (orderDto.getOrderStatus() != null) {
            savedOrder.setOrderStatus(orderDto.getOrderStatus());
        }

        savedOrder = orderRepository.save(savedOrder);
        return Optional.of(orderMapper.toDto(savedOrder));
    }

    public Optional<Order> delete(Long id) {
        Optional<Order> optionalSavedOrder = findById(id);
        if (optionalSavedOrder.isEmpty()) {
            return Optional.empty();
        }
        Order deletedOrder = optionalSavedOrder.get();
        deletedOrder.setEntityStatus(EntityStatus.DELETED);
        orderRepository.save(deletedOrder);
        return optionalSavedOrder;
    }

    /**
     * Данный метод по расписанию - раз в минуту (60000 = 1 минута) ходит в базу и проверяет
     * нет ли там заказов, которые офрмили и не оплатили более чем 15 минут назад,
     * если такие имеются, тогда:
     *
     * @List<Order>notPaidOrders собирает все ордера статус которых - NOT_PAID;
     * Каждому ордеру со статусом NOT_PAID и не оплатой в течении прошлых 15 минут
     * ставим новый статус OVERDUE;
     * Важным является то, что в блоке
     * @OrderRepository написан запрос, который обходит LIE, ошибку ленивой загрузки,
     * это сделано для того, чтобы обойти проблему подгрузки всех select_products на каждый Order;
     * Далее каждому продукту из selected_products увеличиваем занчение поля
     * @stockCount. После обновления значения сохраняем измененный продукт в базе.
     */
    @Transactional
    @Scheduled(fixedDelay = 60000)
    public void updateOverdueOrders() {
        orderRepository
                .findOrdersWithNotPaidStatus()
                .stream()
                .filter(order -> isOverdue(order.getCreateDateTime()))
                .forEach(order -> {
                             order.setOrderStatus(OrderStatus.OVERDUE);
                             orderRepository.save(order);
                             Optional.ofNullable(order.getSelectedProducts())
                                     .stream()
                                     .flatMap(Collection::stream)
                                     .forEach(this::increaseStockCount);
                         }
                );
    }

    private boolean isOverdue(LocalDateTime creationTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime fifteenMinutesAgo = currentTime.minusMinutes(15);
        return creationTime.isBefore(fifteenMinutesAgo);
    }

    private void increaseStockCount(SelectedProduct selectedProduct) {
        var product = selectedProduct.getProduct();
        product.setStockCount(product.getStockCount() + selectedProduct.getCount());
        productRepository.save(product);
    }
}