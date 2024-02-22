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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductRestApi {

    private final ProductService productService;

    private final ProductImageService productImageService;

    public ResponseEntity<List<ProductDto>> getPage(Integer page, Integer size, Long storeId) {
        var productPage = productService.getPage(page, size, storeId);
        if (productPage == null || productPage.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productPage.getContent());
    }

    @Override
    public ResponseEntity<ProductDto> get(Long id) {
        Optional<ProductDto> productDtoOptional = productService.findByIdDto(id);

        return productDtoOptional.map(productDto -> ResponseEntity.status(HttpStatus.OK).body(productDto))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Override
    public ResponseEntity<ProductDto> create(ProductDto productDto) {
        ProductDto createdProductDto = productService.create(productDto).get();
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductDto);
    }

    @Override
    public ResponseEntity<ProductDto> update(Long id, ProductDto productDto) {
        Optional<ProductDto> updatedProductDtoOptional = productService.update(id, productDto);

        return updatedProductDtoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        Optional<ProductDto> product = productService.delete(id);
        return product.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<long[]> getImagesIDsByProductId(Long id) {
        Optional<Product> product = productService.findById(id);

        if (product.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        if (product.get().getProductImages().isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        long[] images = product.orElse(null).getProductImages().stream()
                .map(ProductImage::getId).mapToLong(Long::valueOf).toArray();
        return ResponseEntity.status(HttpStatus.OK).body(images);
    }

    @Override
    public ResponseEntity<String> uploadImagesByProductId(MultipartFile[] files, Long id) throws IOException {
        Optional<Product> product = productService.findById(id);

        if (product.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("There is no product with such id");
        if (files.length == 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("At least one file should be included");

        List<ProductImage> imageList = new ArrayList<>();
        for (MultipartFile file : files) {
            var image = new ProductImage();
            image.setSomeProduct(product.get());
            image.setName(file.getOriginalFilename());
            image.setData(ImageUtils.compressImage(file.getBytes()));
            imageList.add(image);
        }
        productImageService.saveAll(imageList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<String> deleteAllImagesByProductId(Long id) {
        Optional<Product> product = productService.findById(id);

        if (product.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no product with such id");
        if (product.get().getProductImages().isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Product with such id has no images");

        product.get().getProductImages().stream().map(ProductImage::getId).forEach(productImageService::delete);
        return ResponseEntity.ok().build();
    }
}