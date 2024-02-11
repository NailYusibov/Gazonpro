package com.gitlab.model;

import com.gitlab.enums.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@NamedEntityGraph(name = "Product.productImages",
        attributeNodes = @NamedAttributeNode("productImages"))
@Entity
@EqualsAndHashCode(exclude = {"productImages", "selectedProducts", "review"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
@Indexed
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @Field(termVector = TermVector.YES, store = Store.YES)
    private String name;

    @Column(name = "stock_count")
    private Integer stockCount;

    @OneToMany(mappedBy = "someProduct")
    private Set<ProductImage> productImages;

    @OneToMany(mappedBy = "product")
    private Set<Review> review;

    @Column(name = "description")
    private String description;

    @Column(name = "is_adult")
    private Boolean isAdult;

    @Column(name = "code")
    private String code;

    @Column(name = "weight")
    private Long weight;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "entity_status")
    @Enumerated(EnumType.STRING)
    private EntityStatus entityStatus;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private com.gitlab.model.Store someProduct;
}