package com.gitlab.dto;

import com.gitlab.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @ReadOnlyProperty
    private Long id;

    @Size(min = 1, max = 255, message = "Length of User's email should be between 1 and 255 characters")
    @NotEmpty(message = "User's email should have at least one character")
    private String email;

    @Size(min = 1, max = 16, message = "Length of User's password should be between 1 and 255 characters")
    @NotEmpty(message = "User's password should have at least one character")
    private String password;

    @Size(min = 1, max = 255, message = "Length of User's securityQuestion should be between 1 and 255 characters")
    @NotEmpty(message = "User's securityQuestion should have at least one character")
    private String securityQuestion;

    @Size(min = 1, max = 255, message = "Length of User's answerQuestion should be between 1 and 255 characters")
    @NotEmpty(message = "User's answerQuestion should have at least one character")
    private String answerQuestion;

    @Size(min = 1, max = 255, message = "Length of User's firstName should be between 1 and 255 characters")
    @NotEmpty(message = "User's firstName should have at least one character")
    private String firstName;

    @Size(min = 1, max = 255, message = "Length of User's lastName should be between 1 and 255 characters")
    @NotEmpty(message = "User's lastName should have at least one character")
    private String lastName;

    @NotNull(message = "User birthDate cannot be null")
    private LocalDate birthDate;

    @NotNull(message = "User gender cannot be null")
    private Gender gender;

    @Size(min = 1, max = 16, message = "Length of User's phoneNumber should be between 1 and 255 characters")
    @NotEmpty(message = "User's phoneNumber should have at least one character")
    private String phoneNumber;

    @NotNull(message = "User passport cannot be null")
    private PassportDto passportDto;

    private Set<ShippingAddressDto> shippingAddressDtos;

    @NotNull(message = "User bankCards cannot be null")
    private Set<BankCardDto> bankCardDtos;

    @NotNull(message = "User roles cannot be null")
    private Set<String> roles;
}
