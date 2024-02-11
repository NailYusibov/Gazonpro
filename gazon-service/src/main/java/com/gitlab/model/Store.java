package com.gitlab.model;

import com.gitlab.enums.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "store")
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(name = "store",
        attributeNodes = {
                @NamedAttributeNode("products"),
                @NamedAttributeNode("managers"),
                @NamedAttributeNode("owner")
        }
)
public class Store {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @OneToMany(mappedBy = "someProduct")
    private Set<Product> products;

    @ManyToMany
    @JoinTable(
            name = "store_managers",
            joinColumns = @JoinColumn(name = "store_id"),
            inverseJoinColumns = @JoinColumn(name = "managers_id")
    )
    private Set<User> managers;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "entity_status")
    @Enumerated(EnumType.STRING)
    private EntityStatus entityStatus;

}