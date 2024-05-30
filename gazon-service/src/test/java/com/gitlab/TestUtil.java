package com.gitlab;

import com.gitlab.dto.ProductDto;
import com.gitlab.dto.ReviewDto;
import com.gitlab.dto.ReviewImageDto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class TestUtil {

    public static ReviewImageDto generateReviewImageDto(Long reviewId) {
        ReviewImageDto reviewImageDto = new ReviewImageDto();
        reviewImageDto.setReviewId(reviewId);
        reviewImageDto.setName("test.txt");
        reviewImageDto.setData(new byte[]{1, 2, 3});

        return reviewImageDto;
    }

    public static ReviewDto generateReviewDto(Long productId) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setProductId(productId);
        reviewDto.setPros("test");
        reviewDto.setCons("test");
        reviewDto.setComment("test");
        reviewDto.setRating((byte) 2);
        reviewDto.setHelpfulCounter(11);
        reviewDto.setNotHelpfulCounter(1);
        return reviewDto;
    }

    public static ProductDto generateProductDto(String uniqueCode) {
        var productDto = new ProductDto();
        productDto.setName("test");
        productDto.setStockCount(1);
        productDto.setDescription("test");
        productDto.setIsAdult(true);
        productDto.setCode(uniqueCode);
        productDto.setWeight(1L);
        productDto.setPrice(BigDecimal.ONE);
        return productDto;
    }

    public static byte[] getBytesFromImage() throws IOException {
        BufferedImage image = ImageIO.read(new File("src/test/resources/image/product.png"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    public static String generateUniqueCode(List<ProductDto> products) {

        String code = "10";

        int count;

        do {
            count = 0;

            for (ProductDto productDto : products) {
                if (productDto.getCode().equals(code)) {
                    code = String.valueOf(Integer.parseInt(code) + 1);
                    count = 1;
                }
            }

        } while (count != 0);

        return code;
    }
}
