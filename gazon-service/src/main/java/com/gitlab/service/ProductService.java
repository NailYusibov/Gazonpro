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
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ProductDto> findAllDto() {
        List<Product> products = findAll();
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

    public Page<Product> getPage(Integer page, Integer size) {
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
        return productRepository.findAll(pageRequest);
    }

    public Page<ProductDto> getPageDto(Integer page, Integer size) {

        if (page == null || size == null) {
            var products = findAllDto();
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

    public Product save(Product product) {
        product.setEntityStatus(EntityStatus.ACTIVE);
        return productRepository.save(product);
    }

    public ProductDto saveDto(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        product.setEntityStatus(EntityStatus.ACTIVE);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    public Optional<Product> update(Long id, Product product) {
        Optional<Product> currentOptionalProduct = findById(id);
        Product currentProduct;
        if (currentOptionalProduct.isEmpty()) {
            return currentOptionalProduct;
        } else {
            currentProduct = currentOptionalProduct.get();
        }
        if (product.getName() != null) {
            currentProduct.setName(product.getName());
        }
        if (product.getStockCount() != null) {
            currentProduct.setStockCount(product.getStockCount());
        }
        if (product.getProductImages() != null) {
            currentProduct.setProductImages(product.getProductImages());
        }
        if (product.getDescription() != null) {
            currentProduct.setDescription(product.getDescription());
        }
        if (product.getIsAdult() != null) {
            currentProduct.setIsAdult(product.getIsAdult());
        }
        if (product.getCode() != null) {
            currentProduct.setCode(product.getCode());
        }
        if (product.getWeight() != null) {
            currentProduct.setWeight(product.getWeight());
        }
        if (product.getPrice() != null) {
            currentProduct.setPrice(product.getPrice());
        }

        currentProduct.setEntityStatus(EntityStatus.ACTIVE);

        return Optional.of(productRepository.save(currentProduct));
    }

    public ProductDto updateDto(Long id, ProductDto productDto) {
        Optional<Product> currentOptionalProduct = findById(id);

        if (currentOptionalProduct.isEmpty()) {
            return null;
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

        return productMapper.toDto(productRepository.save(currentProduct));
    }

    public Optional<Product> delete(Long id) {
        Optional<Product> foundProduct = productRepository.findById(id);
        if (foundProduct.isPresent()) {
            productRepository.deleteById(id);
        }
        return foundProduct;
    }

    public Optional<ProductDto> deleteDto(Long id) {
        Optional<Product> foundProduct = findById(id);
        if (foundProduct.isPresent()) {
            foundProduct.get().setEntityStatus(EntityStatus.DELETED);
            productRepository.save(foundProduct.get());
        }
        return foundProduct.map(productMapper::toDto);
    }

    public ProductDto createDto(ProductDto productDto) {
        productDto.setId(null);
        Product productEntity = productMapper.toEntity(productDto);
        productEntity.setEntityStatus(EntityStatus.ACTIVE);
        Product savedProduct = productRepository.save(productEntity);
        return productMapper.toDto(savedProduct);
    }

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
