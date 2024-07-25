package com.gitlab.service;

import com.gitlab.dto.ShoppingCartDto;
import com.gitlab.dto.UserDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.mapper.UserMapper;
import com.gitlab.model.User;
import com.gitlab.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.gitlab.TestUtil.*;
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
        when(userMapper.toDto(generateUser)).thenReturn(expectedResult);

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
}
