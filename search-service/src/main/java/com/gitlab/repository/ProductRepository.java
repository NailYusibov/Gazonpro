package com.gitlab.repository;

import com.gitlab.model.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ReadOnlyRepository<Product, Long> {

    Iterable<Product> findByNameContainingIgnoreCase(String name);
}