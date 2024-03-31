package com.gitlab.service;

import com.gitlab.dto.*;
import com.gitlab.enums.Citizenship;
import com.gitlab.enums.EntityStatus;
import com.gitlab.enums.Gender;
import com.gitlab.mapper.UserMapper;
import com.gitlab.model.*;
import com.gitlab.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ShoppingCartService shoppingCartService;
    @InjectMocks
    private UserService userService;

    @Test
    void should_find_all_users() {
        List<User> expectedResult = generateUsers();
        when(userRepository.findAll()).thenReturn(generateUsers());

        List<User> actualResult = userService.findAll();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_find_user_by_id() {
        long id = 1L;
        UserDto expectedResult = generateUserDto();
        User generateUser = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(generateUser));
        when(userMapper.toDto(generateUser)).thenReturn(generateUserDto());

        Optional<UserDto> actualResult = userService.findById(id);

        assertEquals(expectedResult, actualResult.orElse(null));
    }

    @Test
    void should_save_user() {
        User expectedResult = generateUser();
        when(userRepository.save(expectedResult)).thenReturn(expectedResult);

        User actualResult = userService.save(expectedResult);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void test_saveDto_create_shoppingCart() {
        UserDto newUserDto = generateUserDto();
        newUserDto.setId(1L);

        ShoppingCartDto expectedShoppingCartDto = new ShoppingCartDto();
        expectedShoppingCartDto.setUserId(newUserDto.getId());

        shoppingCartService.saveDto(expectedShoppingCartDto);

        verify(shoppingCartService, times(1))
                .saveDto(argThat(arg -> arg.getUserId().equals(newUserDto.getId())));
    }

    @Test
    void should_update_user() {
        long id = 1L;
        User userToUpdate = generateUser();

        User userBeforeUpdate = generateUserBefore();

        User updatedUser = generateUser();

        when(userRepository.findById(id)).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        Optional<User> actualResult = userService.update(id, userToUpdate);

        assertEquals(updatedUser, actualResult.orElse(null));

    }

    @Test
    void should_not_update_user_when_entity_not_found() {
        long id = 1L;

        User userToUpdate = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Optional<User> actualResult = userService.update(id, userToUpdate);

        verify(userRepository, never()).save(any());
        assertNull(actualResult.orElse(null));
    }

    @Test
    void should_not_updated_user_field_if_null_email() {
        long id = 1L;

        User userToUpdate = generateUser(id);
        userToUpdate.setEmail(null);
        User userBeforeUpdate = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(userBeforeUpdate)).thenReturn(userBeforeUpdate);

        Optional<User> actualResult = userService.update(id, userToUpdate);

        verify(userRepository).save(userBeforeUpdate);
        assertEquals(userBeforeUpdate, actualResult.orElse(null));
        assertNotNull(actualResult.orElse(userBeforeUpdate).getEmail());
    }
    @Test
    void should_not_updated_user_field_if_null_password() {
        long id = 1L;

        User userToUpdate = generateUser(id);
        userToUpdate.setPassword(null);
        User userBeforeUpdate = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(userBeforeUpdate)).thenReturn(userBeforeUpdate);

        Optional<User> actualResult = userService.update(id, userToUpdate);

        verify(userRepository).save(userBeforeUpdate);
        assertEquals(userBeforeUpdate, actualResult.orElse(null));
        assertNotNull(actualResult.orElse(userBeforeUpdate).getPassword());
    }
    @Test
    void should_not_updated_user_field_if_null_securityQuestion() {
        long id = 1L;

        User userToUpdate = generateUser(id);
        userToUpdate.setSecurityQuestion(null);
        User userBeforeUpdate = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(userBeforeUpdate)).thenReturn(userBeforeUpdate);

        Optional<User> actualResult = userService.update(id, userToUpdate);

        verify(userRepository).save(userBeforeUpdate);
        assertEquals(userBeforeUpdate, actualResult.orElse(null));
        assertNotNull(actualResult.orElse(userBeforeUpdate).getSecurityQuestion());
    }
    @Test
    void should_not_updated_user_field_if_null_answerQuestion() {
        long id = 1L;

        User userToUpdate = generateUser(id);
        userToUpdate.setAnswerQuestion(null);
        User userBeforeUpdate = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(userBeforeUpdate)).thenReturn(userBeforeUpdate);

        Optional<User> actualResult = userService.update(id, userToUpdate);

        verify(userRepository).save(userBeforeUpdate);
        assertEquals(userBeforeUpdate, actualResult.orElse(null));
        assertNotNull(actualResult.orElse(userBeforeUpdate).getAnswerQuestion());
    }
    @Test
    void should_not_updated_user_field_if_null_lastName() {
        long id = 1L;

        User userToUpdate = generateUser(id);
        userToUpdate.setLastName(null);
        User userBeforeUpdate = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(userBeforeUpdate)).thenReturn(userBeforeUpdate);

        Optional<User> actualResult = userService.update(id, userToUpdate);

        verify(userRepository).save(userBeforeUpdate);
        assertEquals(userBeforeUpdate, actualResult.orElse(null));
        assertNotNull(actualResult.orElse(userBeforeUpdate).getLastName());
    }

    @Test
    void should_not_updated_user_field_if_null_firstName() {
        long id = 1L;

        User userToUpdate = generateUser(id);
        userToUpdate.setFirstName(null);
        User userBeforeUpdate = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(userBeforeUpdate)).thenReturn(userBeforeUpdate);

        Optional<User> actualResult = userService.update(id, userToUpdate);

        verify(userRepository).save(userBeforeUpdate);
        assertEquals(userBeforeUpdate, actualResult.orElse(null));
        assertNotNull(actualResult.orElse(userBeforeUpdate).getFirstName());
    }

    @Test
    void should_not_updated_user_field_if_null_phoneNumber() {
        long id = 1L;

        User userToUpdate = generateUser(id);
        userToUpdate.setPhoneNumber(null);
        User userBeforeUpdate = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(userBeforeUpdate)).thenReturn(userBeforeUpdate);

        Optional<User> actualResult = userService.update(id, userToUpdate);

        verify(userRepository).save(userBeforeUpdate);
        assertEquals(userBeforeUpdate, actualResult.orElse(null));
        assertNotNull(actualResult.orElse(userBeforeUpdate).getPhoneNumber());
    }

    @Test
    void should_not_updated_user_field_if_null_createData() {
        long id = 1L;

        User userToUpdate = generateUser(id);
        userToUpdate.setCreateDate(null);
        User userBeforeUpdate = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(userBeforeUpdate)).thenReturn(userBeforeUpdate);

        Optional<User> actualResult = userService.update(id, userToUpdate);

        verify(userRepository).save(userBeforeUpdate);
        assertEquals(userBeforeUpdate, actualResult.orElse(null));
        assertNotNull(actualResult.orElse(userBeforeUpdate).getCreateDate());
    }

    @Test
    void should_updated_user_field_if_null_passport() {
        long id = 1L;

        User userToUpdate = generateUser(id);
        userToUpdate.setPassport(null);
        User userBeforeUpdate = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(userBeforeUpdate)).thenReturn(userBeforeUpdate);

        Optional<User> actualResult = userService.update(id, userToUpdate);

        verify(userRepository).save(userBeforeUpdate);
        assertNotNull(actualResult.orElse(userBeforeUpdate).getPassport());
    }

    @Test
    void should_updated_user_field_if_null_personal_address() {
        long id = 1L;

        User userToUpdate = generateUser(id);
        userToUpdate.setPassport(null);
        User userBeforeUpdate = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(userBeforeUpdate)).thenReturn(userBeforeUpdate);

        Optional<User> actualResult = userService.update(id, userToUpdate);

        verify(userRepository).save(userBeforeUpdate);
        assertNotNull(actualResult.orElse(userBeforeUpdate).getShippingAddressSet());
    }

    @Test
    void should_updated_user_field_if_null_bank_cards() {
        long id = 1L;

        User userToUpdate = generateUser(id);
        userToUpdate.setPassport(null);
        User userBeforeUpdate = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(userBeforeUpdate)).thenReturn(userBeforeUpdate);

        Optional<User> actualResult = userService.update(id, userToUpdate);

        verify(userRepository).save(userBeforeUpdate);
        assertNotNull(actualResult.orElse(userBeforeUpdate).getBankCardsSet());
    }

    @Test
    void should_updated_user_field_if_null_roles() {
        long id = 1L;

        User userToUpdate = generateUser(id);
        userToUpdate.setPassport(null);
        User userBeforeUpdate = generateUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(userBeforeUpdate));
        when(userRepository.save(userBeforeUpdate)).thenReturn(userBeforeUpdate);

        Optional<User> actualResult = userService.update(id, userToUpdate);

        verify(userRepository).save(userBeforeUpdate);
        assertNotNull(actualResult.orElse(userBeforeUpdate).getRolesSet());
    }

    @Test
    void should_delete_user() {
        long id = 1L;
        User deletedUser = generateUser(id);
        deletedUser.setEntityStatus(EntityStatus.DELETED);

        when(userRepository.findById(id)).thenReturn(Optional.of(generateUser()));

        userService.delete(id);
        
        verify(userRepository).save(deletedUser);
    }

    @Test
    void should_not_delete_user_when_entity_not_found() {
        long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        userService.delete(id);

        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void should_be_connected_with_tables_bank_card_shipping_address_passport(){
        long id = 1L;

        User user = generateUser();

        when(userRepository.save(user)).thenReturn(user);

        userService.save(user);

        verify(userRepository).save(user);

        assertNotNull(user.getBankCardsSet().stream().findAny().get().getId());
        assertNotNull(user.getShippingAddressSet().stream().findAny().get().getId());
        assertNotNull(user.getPassport().getId());

    }


    private List<User> generateUsers() {

        return List.of(
                generateUser(1L),
                generateUser(2L),
                generateUser(3L),
                generateUser(4L)
        );
    }

    private User generateUser(Long id) {
        User user = generateUser();
        user.setId(id);
        return user;
    }

    private User generateUser() {
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role(1L, "ROLE_ADMIN", EntityStatus.ACTIVE));

        Set<BankCard> bankCardSet = new HashSet<>();
        bankCardSet.add(new BankCard(1L, "0000000000000", LocalDate.of(1900, 1, 1), 777));

        Set<ShippingAddress> personalAddresses = new HashSet<>();

        personalAddresses.add(new PersonalAddress(
                1L,
                "apartment",
                "floor",
                "entance",
                "doorode",
                "postode"));

        Passport passport = new Passport(
                1L,
                Citizenship.RUSSIA,
                "user",
                "user",
                "paonym",
                LocalDate.of(2000, 5, 15),
                LocalDate.of(2000, 5, 15),
                "09865",
                "isuer",
                "issurN",
                EntityStatus.ACTIVE);

        return new User(1L,
                "user",
                "user",
                "anwer",
                "queion",
                "user",
                "user",
                LocalDate.of(1900, 1, 1),
                Gender.MALE,
                "890077777",
                passport,
                LocalDate.now(),
                bankCardSet,
                personalAddresses,
                roleSet,
                EntityStatus.ACTIVE);
    }

    private User generateUserBefore() {
        User user = new User();
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role(1L, "ROLE_USER", EntityStatus.ACTIVE));

        Set<BankCard> bankCardSet = new HashSet<>();
        bankCardSet.add(new BankCard(1L, "1111222233444", LocalDate.of(1905, 6, 7), 888));

        Set<ShippingAddress> personalAddresses = new HashSet<>();

        for(ShippingAddress shippingAddress : personalAddresses){
            for (ShippingAddress address: user.getShippingAddressSet()){
                Long sa = address.getId();
                shippingAddress.setId(sa);
            }
            personalAddresses.add(shippingAddress);

        }

        personalAddresses.add(new PersonalAddress(
                1L,
                "apmentBef",
                "floBef",
                "enanceBef",
                "doooeBef",
                "posodeBef"));

        Passport passport = new Passport(
                1L,
                Citizenship.RUSSIA,
                "userBef",
                "userBef",
                "patroBef",
                LocalDate.of(2010, 6, 25),
                LocalDate.of(2015, 8, 25),
                "09466",
                "issrS",
                "issrP",
                EntityStatus.ACTIVE);

        return new User(1L,
                "userBef",
                "useBef",
                "ansrBef",
                "quesonBef",
                "userBef",
                "userBef",
                LocalDate.of(2010, 4, 4),
                Gender.MALE,
                "89007777",
                passport,
                LocalDate.now(),
                bankCardSet,
                personalAddresses,
                roleSet,
                EntityStatus.ACTIVE);
    }

    private UserDto generateUserDto() {
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role(1L, "ROLE_ADMIN", EntityStatus.ACTIVE));

        Set<BankCardDto> bankCardSet = new HashSet<>();
        bankCardSet.add(new BankCardDto(1L, "0000000000000", LocalDate.of(1900, 1, 1), 777));

        Set<ShippingAddressDto> personalAddresses = new HashSet<>();
        personalAddresses.add(new PersonalAddressDto(1L,
                "address",
                "direction",
                "apartment",
                "floor",
                "entance",
                "doorode",
                "postode"));

        PassportDto passportDto = new PassportDto(
                1L,
                Citizenship.RUSSIA,
                "user",
                "user",
                "paonym",
                LocalDate.of(2000, 5, 15),
                LocalDate.of(2000, 5, 15),
                "09865",
                "isuer",
                "issurN");

        return new UserDto(1L,
                "user",
                "user",
                "anwer",
                "queion",
                "user",
                "user",
                LocalDate.of(1900, 1, 1),
                Gender.MALE,
                "890077777",
                passportDto,
                personalAddresses,
                bankCardSet,
                roleSet.stream().map(Role::toString).collect(Collectors.toSet()));
    }
}

