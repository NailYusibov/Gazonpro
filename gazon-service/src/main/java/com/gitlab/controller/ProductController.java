package com.gitlab.controller;

import com.gitlab.controllers.api.rest.ProductRestApi;
import com.gitlab.dto.ProductDto;
import com.gitlab.model.Product;
import com.gitlab.model.ProductImage;
import com.gitlab.service.ProductImageService;
import com.gitlab.service.ProductService;
import com.gitlab.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController implements ProductRestApi {

    private final ProductService productService;

    private final ProductImageService productImageService;

    public ResponseEntity<List<ProductDto>> getPage(Integer page, Integer size, Long storeId) {
        log.info("getPage: Received GET request with page: {} , size: {} and storeId: {}", page, size, storeId);
        var productPage = (storeId == null)
                ? productService.getPage(page, size)
                : productService.getPageByStoreId(page, size, storeId);
        if (productPage == null || productPage.getContent().isEmpty()) {
            log.warn("getPage: Page is empty");
            return ResponseEntity.noContent().build();
        }
        log.info("getPage: Returning {} products for page number: {}, page size: {} and storeId: {}", productPage.getContent().size(), page, size, storeId);
        return ResponseEntity.ok(productPage.getContent());
    }

    @Override
    public ResponseEntity<ProductDto> get(Long id) {
        log.info("get: Received GET request with id: {}", id);
        Optional<ProductDto> productDtoOptional = productService.findByIdDto(id);

        log.info("get: Returning product with id: {}", id);
        return productDtoOptional.map(productDto -> ResponseEntity.status(HttpStatus.OK).body(productDto))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Override
    public ResponseEntity<ProductDto> create(ProductDto productDto) {
        log.info("create: Received POST request with product: {}", productDto);
        ProductDto createdProductDto = productService.create(productDto).get();
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductDto);
    }

    @Override
    public ResponseEntity<ProductDto> update(Long id, ProductDto productDto) {
        log.info("update: Received PUT request with id: {} and product: {}", id, productDto);
        Optional<ProductDto> updatedProductDtoOptional = productService.update(id, productDto);

        log.info("update: Returning product with id: {}", id);
        return updatedProductDtoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        log.info("delete: Received DELETE request with id: {}", id);
        Optional<ProductDto> product = productService.delete(id);
        if (product.isEmpty()) {
            log.warn("delete: Product with id: {} not found", id);
            return ResponseEntity.notFound().build();
        }
        else {
            log.info("delete: Product with id: {} deleted", id);
            return ResponseEntity.ok().build();
        }
    }

    @Override
    public ResponseEntity<long[]> getImagesIDsByProductId(Long id) {
        log.info("getImagesIDsByProductId: Received GET request with id: {}", id);
        Optional<Product> product = productService.findById(id);

        if (product.isEmpty()) {
            log.warn("getImagesIDsByProductId: Product with id: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (product.get().getProductImages().isEmpty()) {
            log.error("getImagesIDsByProductId: Product with id: {} has no images", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        long[] images = product.orElse(null).getProductImages().stream()
                .map(ProductImage::getId).mapToLong(Long::valueOf).toArray();
        log.info("getImagesIDsByProductId: Returning images of product with id: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(images);
    }

    @Override
    public ResponseEntity<String> uploadImagesByProductId(MultipartFile[] files, Long id) throws IOException {
        log.info("uploadImagesByProductId: Received PUT request with id: {}", id);
        Optional<Product> product = productService.findById(id);

        if (product.isEmpty()) {
            log.warn("uploadImagesByProductId: Product with id: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("There is no product with such id");
        }
        if (files.length == 0) {
            log.error("uploadImagesByProductId: There are no files in the fileArray: {}", (Object[]) files);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("At least one file should be included");
        }

        List<ProductImage> imageList = new ArrayList<>();
        for (MultipartFile file : files) {
            var image = new ProductImage();
            image.setSomeProduct(product.get());
            image.setName(file.getOriginalFilename());
            image.setData(ImageUtils.compressImage(file.getBytes()));
            imageList.add(image);
        }
        productImageService.saveAll(imageList);
        log.info("uploadImagesByProductId: Images for product with id: {} were updated", id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<String> deleteAllImagesByProductId(Long id) {
        log.info("deleteAllImagesByProductId: Received DELETE request with id: {}", id);
        Optional<Product> product = productService.findById(id);

        if (product.isEmpty()) {
            log.warn("deleteAllImagesByProductId: Product with id: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no product with such id");
        }

        if (product.get().getProductImages().isEmpty()) {
            log.error("deleteAllImagesByProductId: Product with id: {} has no images", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Product with such id has no images");
        }

        product.get().getProductImages().stream().map(ProductImage::getId).forEach(productImageService::delete);
        log.info("deleteAllImagesByProductId: Images for product with id: {} were deleted", id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<String> addFavouriteProduct(Long productId) {
        log.info("addFavouriteProduct: Received POST request with product: {}", productId);
        Optional<Product> productOptional = productService.addFavouriteProduct(productId);

        if (productOptional.isEmpty()) {
            log.warn("addFavouriteProduct: Product with ID: {} could not be added to favorites, checking if it exists", productId);
            Optional<Product> existingProductOptional = productService.findById(productId);

            if (existingProductOptional.isEmpty()) {
                log.error("addFavouriteProduct: Product with ID: {} not found", productId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            } else {
                log.info("addFavouriteProduct: Product with ID: {} is already in favorites", productId);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Product is already in favorites");
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<String> deleteFavouriteProductById(Long productId) {
        log.info("deleteFavouriteProductById: Received to remove favourite product request with id: {}", productId);
        Optional<Product> productOptional = productService.removeFavouriteProduct(productId);

        if (productOptional.isEmpty()) {
            log.warn("deleteFavouriteProductById: Product with id: {} was not found in favourites", productId);
            Optional<Product> existingProductOptional = productService.findById(productId);

            if (existingProductOptional.isEmpty()) {
                log.error("deleteFavouriteProductById: Product with ID {} does not exist", productId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            } else {
                log.info("Product with ID {} exists but is not in favourites.", productId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Product is not in favorites");
            }
        }

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<ProductDto>> getFavouriteProducts() {
        log.info("getFavouriteProducts: Received GET request");
        List<ProductDto> favouriteProducts = productService.getFavouriteProducts();

        log.info("getFavouriteProducts: Returning {} favourite products", favouriteProducts);
        return ResponseEntity.status(HttpStatus.OK).body(favouriteProducts);
    }
}