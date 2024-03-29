package com.gitlab.dto;

import com.gitlab.enums.Citizenship;
import com.gitlab.enums.Gender;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoTest extends AbstractDtoTest {

    @Test
    void test_valid_text_length() {
        UserDto userDto = generateUser();
        assertTrue(validator.validate(userDto).isEmpty());
    }


    @Test
    void test_invalid_text_length_email() {
        UserDto userDto = generateUser();
        userDto.setEmail("");
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_length_password() {
        UserDto userDto = generateUser();
        userDto.setPassword("");
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_length_securityQuestion() {
        UserDto userDto = generateUser();
        userDto.setSecurityQuestion("");
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_length_answerQuestion() {
        UserDto userDto = generateUser();
        userDto.setAnswerQuestion("");
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_length_firstName() {
        UserDto userDto = generateUser();
        userDto.setFirstName("");
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_length_lastName() {
        UserDto userDto = generateUser();
        userDto.setLastName("");
        assertFalse(validator.validate(userDto).isEmpty());
    }
    @Test
    void test_invalid_text_length_phoneNumber() {
        UserDto userDto = generateUser();
        userDto.setPhoneNumber("");
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_max_email() {
        UserDto userDto = generateUser();

        userDto.setEmail("a".repeat(256));
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_max_password() {
        UserDto userDto = generateUser();

        userDto.setPassword("a".repeat(17));
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_max_securityQuestion() {
        UserDto userDto = generateUser();

        userDto.setSecurityQuestion("a".repeat(256));
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_max_answerQuestion() {
        UserDto userDto = generateUser();

        userDto.setAnswerQuestion("a".repeat(256));
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_max_firstName() {
        UserDto userDto = generateUser();

        userDto.setFirstName("a".repeat(256));
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_max_lastName() {
        UserDto userDto = generateUser();

        userDto.setLastName("a".repeat(256));
        assertFalse(validator.validate(userDto).isEmpty());
    }
    @Test
    void test_invalid_text_max_phoneNumber() {
        UserDto userDto = generateUser();

        userDto.setPhoneNumber("a".repeat(17));
        assertFalse(validator.validate(userDto).isEmpty());
    }


    @Test
    void test_invalid_text_null_email() {
        UserDto userDto = generateUser();

        userDto.setEmail(null);
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_null_password() {
        UserDto userDto = generateUser();

        userDto.setPassword(null);
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_null_securityQuestion() {
        UserDto userDto = generateUser();

        userDto.setSecurityQuestion(null);
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_null_answerQuestion() {
        UserDto userDto = generateUser();

        userDto.setAnswerQuestion(null);
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_null_firstName() {
        UserDto userDto = generateUser();

        userDto.setFirstName(null);
        assertFalse(validator.validate(userDto).isEmpty());
    }

    @Test
    void test_invalid_text_null_lastName() {
        UserDto userDto = generateUser();

        userDto.setLastName(null);
        assertFalse(validator.validate(userDto).isEmpty());
    }
    @Test
    void test_invalid_text_null_phoneNumber() {
        UserDto userDto = generateUser();

        userDto.setPhoneNumber(null);
        assertFalse(validator.validate(userDto).isEmpty());
    }


    @Test
    void test_default_message_invalid_length_email() {
        UserDto userDto = generateUser();

        userDto.setEmail("a".repeat(256));
        String expectedMessage = "Length of User's email should be between 1 and 255 characters";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_invalid_length_password() {
        UserDto userDto = generateUser();

        userDto.setPassword("a".repeat(256));
        String expectedMessage = "Length of User's password should be between 1 and 255 characters";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_invalid_length_securityQuestion() {
        UserDto userDto = generateUser();

        userDto.setSecurityQuestion("a".repeat(256));
        String expectedMessage = "Length of User's securityQuestion should be between 1 and 255 characters";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_invalid_length_answerQuestion() {
        UserDto userDto = generateUser();

        userDto.setAnswerQuestion("a".repeat(256));
        String expectedMessage = "Length of User's answerQuestion should be between 1 and 255 characters";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_invalid_length_firstName() {
        UserDto userDto = generateUser();

        userDto.setFirstName("a".repeat(256));
        String expectedMessage = "Length of User's firstName should be between 1 and 255 characters";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_invalid_length_lastName() {
        UserDto userDto = generateUser();

        userDto.setLastName("a".repeat(256));
        String expectedMessage = "Length of User's lastName should be between 1 and 255 characters";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_invalid_length_phoneNumber() {
        UserDto userDto = generateUser();

        userDto.setPhoneNumber("a".repeat(256));
        String expectedMessage = "Length of User's phoneNumber should be between 1 and 255 characters";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_null_text_email() {
        UserDto userDto = generateUser();

        userDto.setEmail(null);
        String expectedMessage = "User's email should have at least one character";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_null_text_password() {
        UserDto userDto = generateUser();

        userDto.setPassword(null);
        String expectedMessage = "User's password should have at least one character";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_null_text_securityQuestion() {
        UserDto userDto = generateUser();

        userDto.setSecurityQuestion(null);
        String expectedMessage = "User's securityQuestion should have at least one character";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_null_text_answerQuestion() {
        UserDto userDto = generateUser();

        userDto.setAnswerQuestion(null);
        String expectedMessage = "User's answerQuestion should have at least one character";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_null_text_firstName() {
        UserDto userDto = generateUser();

        userDto.setFirstName(null);
        String expectedMessage = "User's firstName should have at least one character";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_null_text_lastName() {
        UserDto userDto = generateUser();

        userDto.setLastName(null);
        String expectedMessage = "User's lastName should have at least one character";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_null_object_birthDate() {
        UserDto userDto = generateUser();

        userDto.setBirthDate(null);
        String expectedMessage = "User birthDate cannot be null";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_null_object_gender() {
        UserDto userDto = generateUser();

        userDto.setGender(null);
        String expectedMessage = "User gender cannot be null";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_null_text_phoneNumber() {
        UserDto userDto = generateUser();

        userDto.setPhoneNumber(null);
        String expectedMessage = "User's phoneNumber should have at least one character";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_null_object_passport() {
        UserDto userDto = generateUser();

        userDto.setPassportDto(null);
        String expectedMessage = "User passport cannot be null";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_null_object_bankCards() {
        UserDto userDto = generateUser();

        userDto.setBankCardDtos(null);
        String expectedMessage = "User bankCards cannot be null";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void test_default_message_null_object_roles() {
        UserDto userDto = generateUser();

        userDto.setRoles(null);
        String expectedMessage = "User roles cannot be null";
        String actualMessage = validator.validate(userDto).iterator().next().getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    private UserDto generateUser() {
        Set<String> roleSet = new HashSet<>();
        roleSet.add("ROLE_ADMIN");

        Set<BankCardDto> bankCard = new HashSet<>();
        bankCard.add(new BankCardDto(
                1L,
                "1111222233334444",
                LocalDate.of(1900, 1, 1),
                423
        ));

        Set<ShippingAddressDto> personalAddress = new HashSet<>();
        personalAddress.add(new PersonalAddressDto(1L,
                "address",
                "directions",
                "apartment",
                "floor",
                "entrance",
                "doorCode",
                "postCode"));

        PassportDto passport = new PassportDto(
                1L,
                Citizenship.RUSSIA,
                "user",
                "user",
                "patronym",
                LocalDate.of(2000, 5, 15),
                LocalDate.of(2000, 5, 15),
                "098765",
                "issuer",
                "issuerN");


        return new UserDto(
                1L,
                "mail@mail.ru",
                "username",
                "user",
                "answer",
                "question",
                "user",
                "user",
                LocalDate.of(1900, 1, 1),
                Gender.MALE,
                "89007777777",
                passport,
                personalAddress,
                bankCard,
                roleSet);
    }
}
