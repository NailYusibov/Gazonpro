package com.gitlab.model;

import com.gitlab.enums.EntityStatus;
import com.gitlab.enums.OrderStatus;
import lombok.*;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"shippingAddress", "user", "selectedProducts"})
@ToString(callSuper = true)
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "shipping_address_id", referencedColumnName = "shipping_address_id")
    private ShippingAddress shippingAddress;

    @Column(name = "shipping_date")
    private LocalDate shippingDate;

    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "create_date_time")
    private LocalDateTime createDateTime;

    @Column(name = "sum")
    private BigDecimal sum;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "bag_counter")
    private Byte bagCounter;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private Set<SelectedProduct> selectedProducts;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "entity_status")
    @Enumerated(EnumType.STRING)
    private EntityStatus entityStatus;

    public Order(long id) {
        this.id = id;
    }

    public Order(long id, String orderCode) {
        this.id = id;
        this.orderCode = orderCode;
    }
}
