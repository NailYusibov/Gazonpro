package com.gitlab.model;

import com.gitlab.enums.PaymentStatus;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(exclude = {"order", "user"})
@Table(name = "payments")
public class Payment {

    public Payment(long id) {
        this.id = id;
    }

    public Payment(long id, PaymentStatus paymentStatus) {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "bank_card_id")
    private BankCard bankCard;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "create_date_time")
    private LocalDateTime createDateTime;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "sum")
    private BigDecimal sum;

}
