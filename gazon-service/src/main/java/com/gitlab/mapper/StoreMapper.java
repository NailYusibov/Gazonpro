package com.gitlab.mapper;

import com.gitlab.dto.StoreDto;
import com.gitlab.model.Product;
import com.gitlab.model.Store;
import com.gitlab.model.User;
import com.gitlab.service.ProductService;
import com.gitlab.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class StoreMapper {

//    @Autowired
//    @Lazy
//    private ProductService productService;
    @Autowired
    private UserService userService;

    @Mapping(target = "ownerId", source = "owner")
    @Mapping(target = "managersId", source = "managers")
//    @Mapping(target = "productsId", source = "products")
    public abstract StoreDto toDto(Store store);

    @Mapping(target = "owner", source = "ownerId")
    @Mapping(target = "managers", source = "managersId")
//    @Mapping(target = "products", source = "productsId")
    public abstract Store toEntity(StoreDto storeDto);

    public Long mapUserToOwnerId(User user) {
        return user != null ? user.getId() : null;
    }

    public User mapOwnerIdToUser(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        Optional<User> userById = userService.findUserById(ownerId);
        return userById.orElse(null);
    }

    public Set<User> mapUserToLong(Set<Long> managersId){
        if (managersId == null) {
            return Collections.emptySet();
        }
        return managersId.stream()
                .map(u -> userService.findUserById(u))
                .filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toSet());
    }
    public Set<Long> mapLongToUser(Set<User> managers){
        if (managers == null) {
            return Collections.emptySet();
        } else {
            return managers.stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
        }
    }

//    public Set<Long> mapProductsToLong(Set<Product> productSet) {
//        if (productSet == null) {
//            return Collections.emptySet();
//        } else {
//            return productSet.stream()
//                    .map(Product::getId)
//                    .collect(Collectors.toSet());
//        }
//    }
//
//    public Set<Product> mapLongToProducts(Set<Long> productsIds) {
//        if (productsIds == null) {
//            return Collections.emptySet();
//        } else {
//            return productsIds.stream()
//                    .map(p -> productService.findById(p))
//                    .filter(Optional::isPresent)
//                    .map(Optional::get)
//                    .collect(Collectors.toSet());
//        }
//    }

    public abstract List<StoreDto> toDtoList(List<Store> storeList);

    public abstract List<Store> toEntityList(List<StoreDto> storeDtoList);
}