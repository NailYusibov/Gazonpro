package com.gitlab.service;

import com.gitlab.dto.ProductDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.ProductMapper;
import com.gitlab.model.Product;
import com.gitlab.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.jpa.FullTextQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final FuzzySearchService fuzzySearchService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return productMapper.toDtoList(productRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> findAllByStoreId(Long storeId) {
        List<Product> products;
        if (storeId == null) {
            products = productRepository.findAll();
        } else {
            products = productRepository.findAll(storeId);
        }
        return productMapper.toDtoList(products);
    }

    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ProductDto> findByIdDto(Long id) {
        Optional<Product> currentOptionalProduct = productRepository.findById(id);
        return currentOptionalProduct.map(productMapper::toDto);
    }

    public Page<ProductDto> getPage(Integer page, Integer size) {
        if (page == null || size == null) {
            var products = findAll();
            if (products.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(products);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageRequest);
        return productPage.map(productMapper::toDto);
    }

    public Page<ProductDto> getPageByStoreId(Integer page, Integer size, Long storeId) {
        if (page == null || size == null) {
            var products = findAllByStoreId(storeId);
            if (products.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(products);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Product> productPage;
        if (storeId != null) {
            productPage = productRepository.findAllByStore(pageRequest, storeId);
        } else {
            productPage = productRepository.findAll(pageRequest);

        }
        return productPage.map(productMapper::toDto);
    }

    public Optional<ProductDto> save(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        product.setEntityStatus(EntityStatus.ACTIVE);
        Product savedProduct = productRepository.save(product);
        return Optional.of(productMapper.toDto(savedProduct));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Optional<ProductDto> update(Long id, ProductDto productDto) {
        Optional<Product> currentOptionalProduct = productRepository.findById(id);

        if (currentOptionalProduct.isEmpty()) {
            return Optional.empty();
        }

        Product currentProduct = currentOptionalProduct.get();

        if (productDto.getName() != null) {
            currentProduct.setName(productDto.getName());
        }
        if (productDto.getStockCount() != null) {
            currentProduct.setStockCount(productDto.getStockCount());
        }
        if (productDto.getDescription() != null) {
            currentProduct.setDescription(productDto.getDescription());
        }
        if (productDto.getIsAdult() != null) {
            currentProduct.setIsAdult(productDto.getIsAdult());
        }
        if (productDto.getCode() != null) {
            currentProduct.setCode(productDto.getCode());
        }
        if (productDto.getWeight() != null) {
            currentProduct.setWeight(productDto.getWeight());
        }
        if (productDto.getPrice() != null) {
            currentProduct.setPrice(productDto.getPrice());
        }

        return Optional.of(productMapper.toDto(productRepository.save(currentProduct)));
    }

    public Optional<ProductDto> delete(Long id) {
        Optional<Product> foundProduct = productRepository.findById(id);
        if (foundProduct.isPresent()) {
            foundProduct.get().setEntityStatus(EntityStatus.DELETED);
            productRepository.save(foundProduct.get());
        }
        return foundProduct.map(productMapper::toDto);
    }

    public Optional<ProductDto> create(ProductDto productDto) {
        Product productEntity = productMapper.toEntity(productDto);
        productEntity.setEntityStatus(EntityStatus.ACTIVE);
        Product savedProduct = productRepository.save(productEntity);
        return Optional.of(productMapper.toDto(savedProduct));
    }

    public List<ProductDto> findByNameIgnoreCaseContaining(String name) throws InterruptedException {

        FullTextQuery jpaQuery = fuzzySearchService.getFullTextQuery(name);
        List<Product> firstList = jpaQuery.getResultList();
        List<Product> secondList = (List<Product>) productRepository.findByNameContainingIgnoreCase(name);

        List<Product> mergedList = new ArrayList<>(firstList);

        mergedList.removeAll(secondList);
        mergedList.addAll(secondList);

        return mergedList.stream().filter(mergedList1 -> mergedList1
                .getEntityStatus()
                .equals(EntityStatus.ACTIVE))
                .map(productMapper::toDto).toList();
    }
}
