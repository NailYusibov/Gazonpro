package com.gitlab.mapper;

import com.gitlab.controller.AbstractIntegrationTest;
import com.gitlab.dto.BankCardDto;
import com.gitlab.dto.PassportDto;
import com.gitlab.dto.PersonalAddressDto;
import com.gitlab.dto.ShippingAddressDto;
import com.gitlab.dto.UserDto;
import com.gitlab.enums.Citizenship;
import com.gitlab.enums.EntityStatus;
import com.gitlab.enums.Gender;
import com.gitlab.model.BankCard;
import com.gitlab.model.Passport;
import com.gitlab.model.PersonalAddress;
import com.gitlab.model.Role;
import com.gitlab.model.ShippingAddress;
import com.gitlab.model.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest extends AbstractIntegrationTest {

    @Autowired
    private UserMapper mapper;

    @Test
    void should_map_user_to_Dto() {
        User user = getUser(1L);

        UserDto actualResult = mapper.toDto(user);

        assertNotNull(actualResult);
        assertUser(user, actualResult);
    }

    @Test
    void should_map_userDto_to_Entity() {
        UserDto userDto = getUserDto(1L);

        User actualResult = mapper.toEntity(userDto);

        assertNotNull(actualResult);
        assertUserDto(userDto, actualResult);
    }

    @Test
    void should_map_userList_to_DtoList() {
        List<User> userList = List.of(getUser(1L), getUser(1L), getUser(1L));

        List<UserDto> userDtoList = mapper.toDtoList(userList);

        assertNotNull(userDtoList);
        assertEquals(userList.size(), userList.size());
        for (int i = 0; i < userDtoList.size(); i++) {
            UserDto dto = userDtoList.get(i);
            User entity = userList.get(i);
            assertUser(entity, dto);
        }
    }

    @Test
    void should_map_userDtoList_to_EntityList() {
        List<UserDto> userDtoList = List.of(getUserDto(1L), getUserDto(1L), getUserDto(1L));

        List<User> userList = mapper.toEntityList(userDtoList);

        assertNotNull(userList);
        assertEquals(userList.size(), userList.size());
        for (int i = 0; i < userList.size(); i++) {
            UserDto dto = userDtoList.get(i);
            User entity = userList.get(i);
            assertUserDto(dto, entity);
        }
    }

    @NotNull
    private User getUser(Long id) {
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role(1L, "ROLE_ADMIN", EntityStatus.ACTIVE));

        Set<BankCard> bankCardSet = new HashSet<>();
        bankCardSet.add(new BankCard(id, "0000000000000000", LocalDate.now(), 777));

        Set<ShippingAddress> personalAddresses = new HashSet<>();
        personalAddresses.add(new PersonalAddress(
                id,
                "apartment",
                "floor",
                "entrance",
                "doorCode",
                "postCode"));

        Passport passport = new Passport(
                id,
                Citizenship.RUSSIA,
                "user",
                "user",
                "patronym",
                LocalDate.now(),
                LocalDate.now(),
                "098765",
                "issuer",
                "issuerN",
                EntityStatus.ACTIVE);

        return new User(id,
                "user",
                "username",
                "user",
                "answer",
                "question",
                "user",
                "user",
                LocalDate.now(),
                Gender.MALE,
                "89007777777",
                passport,
                LocalDate.now(),
                bankCardSet,
                personalAddresses,
                roleSet,
                EntityStatus.ACTIVE,
                10L,
                new HashSet<>());
    }

    @NotNull
    private UserDto getUserDto(Long id) {
        Set<String> roleSet = new HashSet<>();
        roleSet.add("ROLE_ADMIN");

        Set<BankCardDto> bankCard = new HashSet<>();
        bankCard.add(new BankCardDto(
                id,
                "1111222233334444",
                LocalDate.now(),
                423
        ));

        Set<ShippingAddressDto> personalAddress = new HashSet<>();
        personalAddress.add(new PersonalAddressDto(
                id,
                "address",
                "directions",
                "apartment",
                "floor",
                "entrance",
                "doorCode",
                "postCode"));

        PassportDto passport = new PassportDto(
                id,
                Citizenship.RUSSIA,
                "user",
                "user",
                "patronym",
                LocalDate.now(),
                LocalDate.now(),
                "098765",
                "issuer",
                "issuerN");

        return new UserDto(
                id,
                "mail@mail.ru",
                "username",
                "user",
                "answer",
                "question",
                "user",
                "user",
                LocalDate.now(),
                Gender.MALE,
                "89007777777",
                passport,
                personalAddress,
                bankCard,
                roleSet);
    }

    private void assertUser(User user, UserDto actualResult) {
        assertEquals(user.getId(), actualResult.getId());
        assertEquals(user.getEmail(), actualResult.getEmail());
        assertEquals(user.getPassword(), actualResult.getPassword());
        assertEquals(user.getSecurityQuestion(), actualResult.getSecurityQuestion());
        assertEquals(user.getAnswerQuestion(), actualResult.getAnswerQuestion());
        assertEquals(user.getFirstName(), actualResult.getFirstName());
        assertEquals(user.getLastName(), actualResult.getLastName());
        assertEquals(user.getBirthDate(), actualResult.getBirthDate());
        assertEquals(user.getGender(), actualResult.getGender());
        assertEquals(user.getPhoneNumber(), actualResult.getPhoneNumber());
        assertEquals(user.getCreateDate(), LocalDate.now());

        //Test all collections of fields ShippingAddress & ShippingAddressDto
        assertEquals(user.getShippingAddressSet().stream().map(ShippingAddress::getAddress).collect(Collectors.toSet()), actualResult.getShippingAddressDtos().stream().map(ShippingAddressDto::getAddress).collect(Collectors.toSet()));
        assertEquals(user.getShippingAddressSet().stream().map(ShippingAddress::getDirections).collect(Collectors.toSet()), actualResult.getShippingAddressDtos().stream().map(ShippingAddressDto::getDirections).collect(Collectors.toSet()));

        //Test all collections of fields BankCard & BankCardDto
        assertEquals(user.getBankCardsSet().stream().map(BankCard::getCardNumber).collect(Collectors.toSet()), actualResult.getBankCardDtos().stream().map(BankCardDto::getCardNumber).collect(Collectors.toSet()));
        assertEquals(user.getBankCardsSet().stream().map(BankCard::getDueDate).collect(Collectors.toSet()), actualResult.getBankCardDtos().stream().map(BankCardDto::getDueDate).collect(Collectors.toSet()));
        assertEquals(user.getBankCardsSet().stream().map(BankCard::getSecurityCode).collect(Collectors.toSet()), actualResult.getBankCardDtos().stream().map(BankCardDto::getSecurityCode).collect(Collectors.toSet()));

        //Test all collections of field Role & String
        assertEquals(user.getRolesSet().stream().map(Role::getName).collect(Collectors.toSet()), actualResult.getRoles().stream().flatMap(String::lines).collect(Collectors.toSet()));

        //test all field custom object Passport & PassportDto
        assertEquals(user.getPassport().getCitizenship(), actualResult.getPassportDto().getCitizenship());
        assertEquals(user.getPassport().getId(), actualResult.getPassportDto().getId());
        assertEquals(user.getPassport().getFirstName(), actualResult.getPassportDto().getFirstName());
        assertEquals(user.getPassport().getLastName(), actualResult.getPassportDto().getLastName());
        assertEquals(user.getPassport().getPatronym(), actualResult.getPassportDto().getPatronym());
        assertEquals(user.getPassport().getBirthDate(), actualResult.getPassportDto().getBirthDate());
        assertEquals(user.getPassport().getIssueDate(), actualResult.getPassportDto().getIssueDate());
        assertEquals(user.getPassport().getPassportNumber(), actualResult.getPassportDto().getPassportNumber());
        assertEquals(user.getPassport().getIssuer(), actualResult.getPassportDto().getIssuer());
        assertEquals(user.getPassport().getIssuerNumber(), actualResult.getPassportDto().getIssuerNumber());

    }

    private void assertUserDto(UserDto userDto, User actualResult) {
        assertEquals(userDto.getId(), actualResult.getId());
        assertEquals(userDto.getEmail(), actualResult.getEmail());
        assertEquals(userDto.getPassword(), actualResult.getPassword());
        assertEquals(userDto.getSecurityQuestion(), actualResult.getSecurityQuestion());
        assertEquals(userDto.getAnswerQuestion(), actualResult.getAnswerQuestion());
        assertEquals(userDto.getFirstName(), actualResult.getFirstName());
        assertEquals(userDto.getLastName(), actualResult.getLastName());
        assertEquals(userDto.getBirthDate(), actualResult.getBirthDate());
        assertEquals(userDto.getGender(), actualResult.getGender());
        assertEquals(userDto.getPhoneNumber(), actualResult.getPhoneNumber());

        //test all field custom object Passport & PassportDto
        assertEquals(userDto.getPassportDto().getCitizenship(), actualResult.getPassport().getCitizenship());
        assertEquals(userDto.getPassportDto().getId(), actualResult.getPassport().getId());
        assertEquals(userDto.getPassportDto().getFirstName(), actualResult.getPassport().getFirstName());
        assertEquals(userDto.getPassportDto().getLastName(), actualResult.getPassport().getLastName());
        assertEquals(userDto.getPassportDto().getPatronym(), actualResult.getPassport().getPatronym());
        assertEquals(userDto.getPassportDto().getBirthDate(), actualResult.getPassport().getBirthDate());
        assertEquals(userDto.getPassportDto().getIssueDate(), actualResult.getPassport().getIssueDate());
        assertEquals(userDto.getPassportDto().getPassportNumber(), actualResult.getPassport().getPassportNumber());
        assertEquals(userDto.getPassportDto().getIssuer(), actualResult.getPassport().getIssuer());
        assertEquals(userDto.getPassportDto().getIssuerNumber(), actualResult.getPassport().getIssuerNumber());

        //Test all collections of fields ShippingAddress & ShippingAddressDto
        assertEquals(userDto.getShippingAddressDtos().stream().map(ShippingAddressDto::getAddress).collect(Collectors.toSet()), actualResult.getShippingAddressSet().stream().map(ShippingAddress::getAddress).collect(Collectors.toSet()));
        assertEquals(userDto.getShippingAddressDtos().stream().map(ShippingAddressDto::getDirections).collect(Collectors.toSet()), actualResult.getShippingAddressSet().stream().map(ShippingAddress::getDirections).collect(Collectors.toSet()));

        //Test all collections of fields BankCard & BankCardDto
        assertEquals(userDto.getBankCardDtos().stream().map(BankCardDto::getCardNumber).collect(Collectors.toSet()), actualResult.getBankCardsSet().stream().map(BankCard::getCardNumber).collect(Collectors.toSet()));
        assertEquals(userDto.getBankCardDtos().stream().map(BankCardDto::getDueDate).collect(Collectors.toSet()), actualResult.getBankCardsSet().stream().map(BankCard::getDueDate).collect(Collectors.toSet()));
        assertEquals(userDto.getBankCardDtos().stream().map(BankCardDto::getSecurityCode).collect(Collectors.toSet()), actualResult.getBankCardsSet().stream().map(BankCard::getSecurityCode).collect(Collectors.toSet()));
        //Test all collections of fields Role & String
        assertEquals(userDto.getRoles().stream().flatMap(String::lines).collect(Collectors.toSet()), actualResult.getRolesSet().stream().map(Role::getName).collect(Collectors.toSet()));

    }
}
