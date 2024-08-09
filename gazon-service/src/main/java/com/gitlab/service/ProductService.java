package com.gitlab.service;

import com.gitlab.dto.ProductDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.ProductMapper;
import com.gitlab.model.Product;
import com.gitlab.model.User;
import com.gitlab.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.jpa.FullTextQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

import static com.gitlab.util.ServiceUtils.updateFieldIfNotNull;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final FuzzySearchService fuzzySearchService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserService userService;

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

        updateFieldIfNotNull(currentProduct::setName, productDto.getName());

        updateFieldIfNotNull(currentProduct::setStockCount, productDto.getStockCount());

        updateFieldIfNotNull(currentProduct::setDescription, productDto.getDescription());

        updateFieldIfNotNull(currentProduct::setIsAdult, productDto.getIsAdult());

        updateFieldIfNotNull(currentProduct::setCode, productDto.getCode());

        updateFieldIfNotNull(currentProduct::setWeight, productDto.getWeight());

        updateFieldIfNotNull(currentProduct::setPrice, productDto.getPrice());

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

    @Transactional(readOnly = true)
    public Page<ProductDto> findByNameIgnoreCaseContaining(String name, Pageable pageable) throws InterruptedException {

        FullTextQuery jpaQuery = fuzzySearchService.getFullTextQuery(name);
        jpaQuery.setFirstResult(pageable.getPageSize() * pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize());
        Page<Product> fuzzyPage = new PageImpl<>(jpaQuery.getResultList(), pageable, jpaQuery.getResultSize());

        return fuzzyPage.map(productMapper::toDto);
    }


    public Optional<Product> addFavouriteProduct(Long productId) {

        User user = userService.getAuthenticatedUser();
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isEmpty()) {
            return Optional.empty();
        }
        Product product = productOptional.get();

        if (!user.getFavouriteProducts().add(product)) {
            return Optional.empty();
        }

        userService.save(user);
        return Optional.of(product);
    }

    public Optional<Product> removeFavouriteProduct(Long productId) {

        User user = userService.getAuthenticatedUser();
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isEmpty()) {
            return Optional.empty();
        }
        Product product = productOptional.get();

        if (!user.getFavouriteProducts().remove(product)) {
            return Optional.empty();
        }
        userService.save(user);
        return Optional.of(product);
    }

    public List<ProductDto> getFavouriteProducts() {

        User user = userService.getAuthenticatedUser();

        Set<Product> productSet = user.getFavouriteProducts();
        List<Product> productList = new ArrayList<>(productSet);

        return productMapper.toDtoList(productList);
    }
}