package com.gitlab.model;

import com.gitlab.enums.EntityStatus;
import com.gitlab.enums.Gender;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"passport", "bankCardsSet", "shippingAddressSet"})
@ToString
@Table(name = "users", schema = "public", catalog = "postgres")
/*@NamedEntityGraph(name = "userWithSets",
        attributeNodes = {
                @NamedAttributeNode("bankCardsSet"),
                @NamedAttributeNode("shippingAddressSet"),
                @NamedAttributeNode("rolesSet")})*/
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(unique = true, name = "email")
    private String email;

    @Column(unique = true, name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "security_question")
    private String securityQuestion;

    @Column(name = "answer_question")
    private String answerQuestion;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    /*@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "passport_id", referencedColumnName = "id")
    private Passport passport;

    @Column(name = "create_date")
    private LocalDate createDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_bank_cards",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "bank_card_id"))
    private Set<BankCard> bankCardsSet;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_shipping_address",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_shipping_address_id"))
    private Set<ShippingAddress> shippingAddressSet;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> rolesSet;*/

    @Column(name = "entity_status")
    @Enumerated(EnumType.STRING)
    private EntityStatus entityStatus;
}
