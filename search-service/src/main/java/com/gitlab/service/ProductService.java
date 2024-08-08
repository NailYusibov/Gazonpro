package com.gitlab.service;

import com.gitlab.dto.ProductDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.ProductMapper;
import com.gitlab.model.Product;
import com.gitlab.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.jpa.FullTextQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final FuzzySearchService fuzzySearchService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductDto> findByNameIgnoreCaseContaining(String name) throws InterruptedException {

        FullTextQuery jpaQuery = fuzzySearchService.getFullTextQuery(name);
        List<Product> firstList = jpaQuery.getResultList();
        List<Product> secondList = (List<Product>) productRepository.findByNameContainingIgnoreCase(name);

        List<Product> mergedList = new ArrayList<>(firstList);

        mergedList.removeAll(secondList);
        mergedList.addAll(secondList);

        return mergedList.stream().filter(mergedList1 -> mergedList1.getEntityStatus().equals(EntityStatus.ACTIVE)).map(productMapper::toDto).toList();
    }
}
