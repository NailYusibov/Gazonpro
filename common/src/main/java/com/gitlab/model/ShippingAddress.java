package com.gitlab.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "shipping_address")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ShippingAddress {

    @Id
    @Column(name = "shipping_address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private String directions;

}