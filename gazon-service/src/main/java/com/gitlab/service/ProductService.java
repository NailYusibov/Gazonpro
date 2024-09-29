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
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

import static com.gitlab.util.ServiceUtils.updateFieldIfNotNull;

@Slf4j
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
        log.info("findAll: Returning {} products", productRepository.findAll().size());
        return productMapper.toDtoList(productRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> findAllByStoreId(Long storeId) {
        log.info("findAllByStoreId: storeId: {}", storeId);
        List<Product> products;
        if (storeId == null) {
            log.warn("findAllByStoreId: Store ID is null. Retrieving all products.");
            products = productRepository.findAll();
        } else {
            log.info("findAllByStoreId: Retrieving products for Store ID: {}", storeId);
            products = productRepository.findAll(storeId);
        }

        log.info("findAllByStoreId: Found {} products for Store ID: {}", products.size(),storeId);
        return productMapper.toDtoList(products);
    }

    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        log.info("findById: id: {}", id);
        log.info("findById: Returning product with id: {}", id);
        return productRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ProductDto> findByIdDto(Long id) {
        log.info("findByIdDto: id: {}", id);
        Optional<Product> currentOptionalProduct = productRepository.findById(id);
        log.info("findByIdDto: Returning product with id: {}", id);
        return currentOptionalProduct.map(productMapper::toDto);
    }

    public Page<ProductDto> getPage(Integer page, Integer size) {
        log.info("getPage: Page: {} Size: {}", page, size);
        if (page == null || size == null) {
            var products = findAll();
            if (products.isEmpty()) {
                log.warn("getPage: Page {} is empty", page);
                return Page.empty();
            }
            log.info("getPage: Returning {} products for page number: {}, page size: {}", products.size(), page, size);
            return new PageImpl<>(products);
        }
        if (page < 0 || size < 1) {
            log.warn("getPage: Page or size is not valid");
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageRequest);

        log.info("getPage: Returning {} products for page number: {}, page size: {}", productPage.getContent().size(), page, size);
        return productPage.map(productMapper::toDto);
    }

    public Page<ProductDto> getPageByStoreId(Integer page, Integer size, Long storeId) {
        log.info("getPageByStoreId: StoreId: {} Page: {} Size: {}", storeId, page, size);
        if (page == null || size == null) {
            log.debug("getPageByStoreId: Page or Size is null. Fetching all products for StoreId: {}", storeId);
            var products = findAllByStoreId(storeId);

            if (products.isEmpty()) {
                log.warn("No products found for storeId: {}. Returning empty page.", storeId);
                return Page.empty();
            }
            log.debug("getPageByStoreId: Returning {} products for storeId: {}", products.size(), storeId);
            return new PageImpl<>(products);
        }
        if (page < 0 || size < 1) {
            log.warn("getPageByStoreId: Page or size is not valid");
            return Page.empty();
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Product> productPage;
        if (storeId != null) {
            log.info("getPageByStoreId: Fetching products for storeId: {}", storeId);
            productPage = productRepository.findAllByStore(pageRequest, storeId);
        } else {
            log.info("getPageByStoreId: Fetching all products");
            productPage = productRepository.findAll(pageRequest);

        }
        log.info("getPageByStoreId: Returning {} products for page number: {},  page size: {}", productPage.getContent().size(), page, size);
        return productPage.map(productMapper::toDto);
    }

    public Optional<ProductDto> save(ProductDto productDto) {
        log.info("save: ProductDto: {}", productDto);
        Product product = productMapper.toEntity(productDto);
        product.setEntityStatus(EntityStatus.ACTIVE);
        Product savedProduct = productRepository.save(product);
        log.info("save: Returning product with id: {}", product.getId());
        return Optional.of(productMapper.toDto(savedProduct));
    }

    public Product save(Product product) {
        log.info("save: Returning product with id: {}", product.getId());
        return productRepository.save(product);
    }

    public Optional<ProductDto> update(Long id, ProductDto productDto) {
        log.info("update: id: {} productDto: {}", id, productDto);
        Optional<Product> currentOptionalProduct = productRepository.findById(id);

        if (currentOptionalProduct.isEmpty()) {
            log.warn("update: Product with id: {} not found.", id);
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

        log.info("update: Returning product with id: {}", currentProduct.getId());
        return Optional.of(productMapper.toDto(productRepository.save(currentProduct)));
    }

    public Optional<ProductDto> delete(Long id) {
        log.info("delete: id: {}", id);
        Optional<Product> foundProduct = productRepository.findById(id);
        if (foundProduct.isPresent()) {
            log.info("delete: {}. Marking as deleted.", foundProduct.get());
            foundProduct.get().setEntityStatus(EntityStatus.DELETED);
            productRepository.save(foundProduct.get());
        }
        return foundProduct.map(productMapper::toDto);
    }

    public Optional<ProductDto> create(ProductDto productDto) {
        log.info("create: Create productDto with id: {}", productDto.getId());
        Product productEntity = productMapper.toEntity(productDto);
        productEntity.setEntityStatus(EntityStatus.ACTIVE);
        Product savedProduct = productRepository.save(productEntity);
        log.info("create: Returning ProductDto with id: {}", savedProduct.getId());
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
        log.info("addFavouriteProduct: Adding product to favorite with id: {}", productId);
        User user = userService.getAuthenticatedUser();
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isEmpty()) {
            log.warn("No product found with id: {}.", productId);
            return Optional.empty();
        }
        Product product = productOptional.get();

        if (!user.getFavouriteProducts().add(product)) {
            log.warn("addFavouriteProduct: Product with id: {} is already exists in the favourite products.", productId);
            return Optional.empty();
        }

        userService.save(user);

        log.info("addFavouriteProduct: product with id: {} is added to favourite", productId);
        return Optional.of(product);
    }

    public Optional<Product> removeFavouriteProduct(Long productId) {
        log.info("removeFavouriteProduct: Removing product with id: {} from favorite", productId);
        User user = userService.getAuthenticatedUser();
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isEmpty()) {
            log.warn("removeFavouriteProduct: Product with id: {} not found", productId);
            return Optional.empty();
        }
        Product product = productOptional.get();

        if (!user.getFavouriteProducts().remove(product)) {
            log.info("removeFavouriteProduct: Product with id: {} was not in the favourites", productId);
            return Optional.empty();
        }
        userService.save(user);

        log.info("removeFavouriteProduct: Product with id: {} was removed from favourites", productId);
        return Optional.of(product);
    }

    public List<ProductDto> getFavouriteProducts() {
        log.info("getFavouriteProducts: Method started");
        User user = userService.getAuthenticatedUser();

        Set<Product> productSet = user.getFavouriteProducts();
        List<Product> productList = new ArrayList<>(productSet);

        log.info("getFavouriteProducts: Returned {} favourite products for user with id: {}", productSet.size(), user.getId());
        return productMapper.toDtoList(productList);
    }
}