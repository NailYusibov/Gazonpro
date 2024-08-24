package com.gitlab.mapper;

import com.gitlab.dto.PersonalAddressDto;
import com.gitlab.dto.PickupPointDto;
import com.gitlab.dto.PostomatDto;
import com.gitlab.dto.ShippingAddressDto;
import com.gitlab.dto.UserDto;
import com.gitlab.model.PersonalAddress;
import com.gitlab.model.PickupPoint;
import com.gitlab.model.Postomat;
import com.gitlab.model.Role;
import com.gitlab.model.ShippingAddress;
import com.gitlab.model.User;
import com.gitlab.service.RoleService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class UserMapper {

    @Autowired
    private RoleService roleService;

    @Mapping(source = "bankCardsSet", target = "bankCardDtos")
    @Mapping(source = "shippingAddressSet", target = "shippingAddressDtos")
    @Mapping(source = "passport", target = "passportDto")
    @Mapping(source = "rolesSet", target = "roles")
    public abstract UserDto toDto(User user);

    public Set<String> mapStringSetToRoleSet(Set<Role> roleSet) {
        Set<String> stringSet = new HashSet<>();
        if (roleSet == null) {
            return Collections.emptySet();
        }
        for (Role role : roleSet) {
            Optional<String> roleName = role.getName().describeConstable();
            roleName.ifPresent(stringSet::add);
        }
        return stringSet;
    }

    @Mapping(source = "bankCardDtos", target = "bankCardsSet")
    @Mapping(source = "shippingAddressDtos", target = "shippingAddressSet")
    @Mapping(source = "passportDto", target = "passport")
    @Mapping(source = "roles", target = "rolesSet")
    public abstract User toEntity(UserDto userDto);

    public Set<Role> mapRoleSetToStringSet(Set<String> stringSet) {
        Set<Role> roleSet = new HashSet<>();
        if (stringSet == null) {
            return Collections.emptySet();
        }
        for (String roleName : stringSet) {
            Optional<Role> roleOptional = roleService.findByName(roleName);
            if (roleOptional.isPresent()) {
                Role role = roleOptional.get();
                roleSet.add(role);
            }
        }
        return roleSet;
    }

    @Named("createShippingAddress")
    public ShippingAddress createShippingAddress(ShippingAddressDto dto) {
        if (dto instanceof PersonalAddressDto) {
            return createPersonalAddressEntity((PersonalAddressDto) dto);
        } else if (dto instanceof PostomatDto) {
            return createPostomatEntity((PostomatDto) dto);
        } else if (dto instanceof PickupPointDto) {
            return createPickupPointEntity((PickupPointDto) dto);
        } else {
            throw new IllegalArgumentException("Unknown ShippingAddressDto subtype");
        }
    }

    @Named("createShippingAddressDto")
    public ShippingAddressDto createShippingAddressDto(ShippingAddress shippingAddress) {
        if (shippingAddress instanceof PersonalAddress) {
            return createPersonalAddressDto((PersonalAddress) shippingAddress);
        } else if (shippingAddress instanceof Postomat) {
            return createPostomatDto((Postomat) shippingAddress);
        } else if (shippingAddress instanceof PickupPoint) {
            return createPickupPointDto((PickupPoint) shippingAddress);
        } else {
            throw new IllegalArgumentException("Unknown ShippingAddress subtype");
        }
    }

    public abstract PersonalAddressDto createPersonalAddressDto(PersonalAddress personalAddress);

    public abstract PostomatDto createPostomatDto(Postomat postomat);

    public abstract  PickupPointDto createPickupPointDto(PickupPoint pickupPoint);


    public abstract PersonalAddress createPersonalAddressEntity(PersonalAddressDto personalAddressDto);

    public abstract Postomat createPostomatEntity(PostomatDto postomatDto);

    public abstract  PickupPoint createPickupPointEntity(PickupPointDto pickupPointDto);

    public Set<ShippingAddressDto> mapShippingAddressEntityToShippingAddressDto(Set<ShippingAddress> shippingAddress) {
        if (shippingAddress == null || shippingAddress.isEmpty()) return new HashSet<>();
        return shippingAddress.stream()
                .map(this::createShippingAddressDto)
                .collect(Collectors.toSet());
    }

    public Set<ShippingAddress> mapShippingAddressDtoToShippingAddressSetEntity(Set<ShippingAddressDto> shippingAddressDtoSet) {
        if (shippingAddressDtoSet == null || shippingAddressDtoSet.isEmpty()) return new HashSet<>();
        return shippingAddressDtoSet.stream()
                .map(this::createShippingAddress)
                .collect(Collectors.toSet());
    }

    public abstract List<UserDto> toDtoList(List<User> userList);

    public abstract List<User> toEntityList(List<UserDto> userDtoList);
}