//package com.gitlab.controller;
//
//import com.gitlab.dto.ProductDto;
//import com.gitlab.dto.ProductImageDto;
//import com.gitlab.mapper.ProductMapper;
//import com.gitlab.model.Product;
//import com.gitlab.model.ProductImage;
//import com.gitlab.service.ProductImageService;
//import com.gitlab.service.ProductService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.testcontainers.shaded.org.hamcrest.CoreMatchers.equalTo;
//import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;
//
//class ProductRestControllerIT extends AbstractIntegrationTest {
//
//    private static final String PRODUCT_URN = "/api/product";
//    private static final String PRODUCT_URI = URL + PRODUCT_URN;
//    @Autowired
//    private ProductService productService;
//    @Autowired
//    private ProductImageService productImageService;
//    @Autowired
//    private ProductMapper productMapper;
//
//    @Test
//    @Transactional(readOnly = true)
//    void should_get_all_products() throws Exception {
//        var response = productService.getPageDto(null, null);
//
//        var expected = objectMapper.writeValueAsString(response.getContent());
//
//        mockMvc.perform(get(PRODUCT_URI))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().json(expected));
//    }
//
//    @Test
//    @Transactional(readOnly = true)
//    void should_get_page() throws Exception {
//        int page = 0;
//        int size = 2;
//        String parameters = "?page=" + page + "&size=" + size;
//
//        var response = productService.getPage(page, size);
//        assertFalse(response.getContent().isEmpty());
//
//        var expected = objectMapper.writeValueAsString(productMapper.toDtoList(response.getContent()));
//
//        mockMvc.perform(get(PRODUCT_URI + parameters))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().json(expected));
//    }
//
//    @Test
//    void should_get_page_with_incorrect_parameters() throws Exception {
//        int page = 0;
//        int size = -2;
//        String parameters = "?page=" + page + "&size=" + size;
//
//        mockMvc.perform(get(PRODUCT_URI + parameters))
//                .andDo(print())
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void should_get_page_without_content() throws Exception {
//        int page = 10;
//        int size = 100;
//        String parameters = "?page=" + page + "&size=" + size;
//
//        mockMvc.perform(get(PRODUCT_URI + parameters))
//                .andDo(print())
//                .andExpect(status().isNoContent());
//    }
//
//
//    @Test
//    @Transactional
//    void should_get_product_by_id() throws Exception {
//        ProductDto productDto = generateProductDTO();
//        ProductDto savedProductDto = productService.saveDto(productDto);
//
//        String expected = objectMapper.writeValueAsString(
//                productService.findByIdDto(savedProductDto.getId()).orElse(null)
//        );
//
//        mockMvc.perform(get(PRODUCT_URI + "/{id}", savedProductDto.getId()))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().json(expected));
//    }
//
//    @Test
//    void should_return_not_found_when_get_product_by_non_existent_id() throws Exception {
//        long id = 9000L;
//        mockMvc.perform(get(PRODUCT_URI + "/{id}", id))
//                .andDo(print())
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    void should_create_product() throws Exception {
//        ProductDto productDto = generateProductDTO();
//        String jsonProductDto = objectMapper.writeValueAsString(productDto);
//
//        mockMvc.perform(post(PRODUCT_URI)
//                        .content(jsonProductDto)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isCreated());
//    }
//    @Test
//    @Transactional
//    void should_return_bad_request_when_creating_product_with_invalid_data() throws Exception {
//        ProductDto productDto = generateProductDTO();
//        productDto.setName("");
//
//        String jsonProductDto = objectMapper.writeValueAsString(productDto);
//
//        mockMvc.perform(post(PRODUCT_URI)
//                        .content(jsonProductDto)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Transactional
//    void should_update_product_by_id() throws Exception {
//        ProductDto productDto = generateProductDTO();
//        ProductDto savedProduct = productService.saveDto(productDto);
//
//        ProductDto updatedProductDto = generateProductDTO();
//        updatedProductDto.setRating(productService.findByIdDto(savedProduct.getId()).get().getRating());
//        updatedProductDto.setId(savedProduct.getId());
//
//        int numberOfEntitiesExpected = productService.findAll().size();
//
//        String jsonProductDto = objectMapper.writeValueAsString(updatedProductDto);
//        String expected = objectMapper.writeValueAsString(updatedProductDto);
//
//
//        mockMvc.perform(patch(PRODUCT_URI + "/{id}", savedProduct.getId())
//                        .content(jsonProductDto)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().json(expected))
//                .andExpect(result -> assertThat(productService.findAll().size(),
//                        equalTo(numberOfEntitiesExpected)));
//    }
//
//    @Test
//    @Transactional
//    void should_return_not_found_when_update_product_by_non_existent_id() throws Exception {
//        long id = 9000L;
//        ProductDto productDto = generateProductDTO();
//        String jsonProductDto = objectMapper.writeValueAsString(productDto);
//
//        mockMvc.perform(patch(PRODUCT_URI + "/{id}", id)
//                        .content(jsonProductDto)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    void should_delete_product_by_id() throws Exception {
//        ProductDto productDto = productService.saveDto(generateProductDTO());
//        long id = productDto.getId();
//        mockMvc.perform(delete(PRODUCT_URI + "/{id}", id))
//                .andDo(print())
//                .andExpect(status().isOk());
//        mockMvc.perform(get(PRODUCT_URI + "/{id}", id))
//                .andDo(print())
//                .andExpect(status().isNotFound());
//    }
//
//
//    @Test
//    void should_get_images_ids_by_product_id() throws Exception {
//        long id = 3L;
//        Optional<Product> product = productService.findById(id);
//        assert product.orElse(null) != null;
//
//        String expected = objectMapper.writeValueAsString(
//                product.orElse(null).getProductImages().stream()
//                        .map(ProductImage::getId).mapToLong(Long::valueOf).toArray()
//        );
//
//        mockMvc.perform(get(PRODUCT_URI + "/{id}" + "/images", id))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().json(expected));
//    }
//
//    @Test
//    void should_create_multiple_productImages_by_product_id() throws Exception {
//        long id = 1L;
//
//        mockMvc.perform(multipart(PRODUCT_URI + "/{id}" + "/images", id)
//                        .file(new MockMultipartFile("files",
//                                "hello.txt", MediaType.TEXT_PLAIN_VALUE, "hello".getBytes()))
//                        .file(new MockMultipartFile("files",
//                                "bye.txt", MediaType.TEXT_PLAIN_VALUE, "bye".getBytes()))
//
//                        .accept(MediaType.ALL))
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    void should_delete_all_productImages_by_product_id() throws Exception {
//        long id = 10L;
//        productImageService.saveDto(generateProductImageDto(id));
//
//        mockMvc.perform(delete(PRODUCT_URI + "/{id}" + "/images", id))
//                .andDo(print())
//                .andExpect(status().isOk());
//        mockMvc.perform(get(PRODUCT_URI + "/{id}" + "/images", id))
//                .andDo(print())
//                .andExpect(status().isNoContent());
//    }
//
//    private ProductDto generateProductDTO() {
//        ProductDto productDto = new ProductDto();
//        productDto.setName("name1");
//        productDto.setStockCount(1);
//        productDto.setImagesId(productMapper.toDto(productService.findById(1L).get()).getImagesId());
//        productDto.setDescription("name");
//        productDto.setIsAdult(true);
//        productDto.setCode("name");
//        productDto.setWeight(1L);
//        productDto.setPrice(BigDecimal.ONE);
//        return productDto;
//    }
//
//    private ProductImageDto generateProductImageDto(Long productId) {
//        ProductImageDto productImageDto = new ProductImageDto();
//        productImageDto.setProductId(productId);
//        productImageDto.setName("file.txt");
//        productImageDto.setData(new byte[]{1, 2, 3});
//
//        return productImageDto;
//    }
//}
