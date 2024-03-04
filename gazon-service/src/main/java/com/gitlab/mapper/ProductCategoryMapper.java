package com.gitlab.mapper;

import com.gitlab.dto.ProductCategoryDto;
import com.gitlab.model.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductCategoryMapper {

    ProductCategoryDto toDto(ProductCategory productCategory);

    ProductCategory toEntity(ProductCategoryDto productCategoryDto);

    List<ProductCategoryDto> toDtoList(List<ProductCategory> productCategoryList);

    List<ProductCategory> toEntityList(List<ProductCategoryDto> productCategoryDtoList);
}