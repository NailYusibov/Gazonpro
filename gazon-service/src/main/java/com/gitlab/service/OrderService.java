package com.gitlab.service;

import com.gitlab.dto.OrderDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.enums.OrderStatus;
import com.gitlab.mapper.OrderMapper;
import com.gitlab.mapper.SelectedProductMapper;
import com.gitlab.mapper.ShippingAddressMapper;
import com.gitlab.mapper.UserMapper;
import com.gitlab.model.Order;
import com.gitlab.model.Product;
import com.gitlab.model.SelectedProduct;
import com.gitlab.repository.OrderRepository;
import com.gitlab.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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


    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<OrderDto> findAllDto() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(orderMapper::toDto).collect(Collectors.toList());
    }

    public Optional<Order> findById(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent() && !orderOptional.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            return orderOptional;
        }
        return Optional.empty();
    }

    public Optional<OrderDto> findByIdDto(Long id) {
        Optional<Order> orderOptional = findById(id);
        if (orderOptional.isPresent()) {
            return orderOptional.map(orderMapper::toDto);
        }
        return Optional.empty();
    }

    public Page<Order> getPage(Integer page, Integer size) {
        if (page == null || size == null) {
            var orders = findAll();
            if (orders.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(orders);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return orderRepository.findAll(pageRequest);
    }

    public Page<OrderDto> getPageDto(Integer page, Integer size) {

        if (page == null || size == null) {
            var orders = findAllDto();
            if (orders.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(orders);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findAll(pageRequest);
        return orderPage.map(orderMapper::toDto);
    }


    public OrderDto saveDto(OrderDto orderDto) {
        Order order = orderMapper.toEntity(orderDto);
        order.setEntityStatus(EntityStatus.ACTIVE);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    public Optional<OrderDto> updateDto(Long id, OrderDto orderDto) {
        Optional<Order> optionalSavedOrder = orderRepository.findById(id);
        if (optionalSavedOrder.isEmpty()) {
            return Optional.empty();
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
     * @List<Order>notPaidOrders собирает все ордера статус которых - NOT_PAID;
     * Каждому ордеру со статусом NOT_PAID и не оплатой в течении прошлых 15 минут
     * ставим новый статус OVERDUE;
     * Важным является то, что в блоке
     * @OrderRepository написан запрос, который обходит LIE, ошибку ленивой загрузки,
     * это сделано для того, чтобы обойти проблему подгрузки всех select_products на каждый Order;
     * Далее каждому продукту из selected_products увеличиваем занчение поля
     * @stockCount.
     * После обновления значения сохраняем измененный продукт в базе.
     */
    @Scheduled(fixedDelay = 60000)
    public void updateOverdueOrders() {
        List<Order> notPaidOrders = orderRepository.findOrdersWithNotPaidStatus();

        for (Order order : notPaidOrders) {
            if (isOverdue(order.getCreateDateTime())) {
                order.setOrderStatus(OrderStatus.OVERDUE);
                orderRepository.save(order);
                Set<SelectedProduct> selectedProductSet = order.getSelectedProducts();
                List<SelectedProduct> selectedProductList;
                if (selectedProductSet != null) {
                     selectedProductList = selectedProductSet.stream().toList();
                    for (SelectedProduct selectedProduct : selectedProductList) {
                        Product product = productRepository.findProductById(selectedProduct.getProduct().getId());
                        product.setStockCount(product.getStockCount() + selectedProduct.getCount());
                        productRepository.save(product);
                    }
                }
            }
        }
    }

    public boolean isOverdue(LocalDateTime creationTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime fifteenMinutesAgo = currentTime.minusMinutes(15);

        return creationTime.isBefore(fifteenMinutesAgo);
    }
}
