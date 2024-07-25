package com.gitlab.repository;

import com.gitlab.model.ProductImage;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends ReadOnlyRepository<ProductImage, Long> {

}
