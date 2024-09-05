package com.gitlab.util;

import com.gitlab.dto.ProductImageDto;
import com.gitlab.dto.ReviewImageDto;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@UtilityClass
public class ImageUtils {

    public static byte[] compressImage(byte[] data) {
        var deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        var outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }

    public static byte[] decompressImage(byte[] data) {
        var inflater = new Inflater();
        inflater.setInput(data);
        var outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }

    public static ProductImageDto decompressAndReturnDto(ProductImageDto productImageDto) {
        productImageDto.setData(decompressImage(productImageDto.getData()));
        return productImageDto;
    }

    public static ReviewImageDto reviewImageDtoDecompressed(ReviewImageDto reviewImageDto) {
        reviewImageDto.setData(decompressImage(reviewImageDto.getData()));
        return reviewImageDto;
    }
}