package com.gitlab.service;

import com.gitlab.dto.UserDto;
import com.gitlab.mapper.BankCardMapper;
import com.gitlab.mapper.PassportMapper;
import com.gitlab.mapper.UserMapper;
import com.gitlab.model.*;
import com.gitlab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final BankCardMapper bankCardMapper;

    private final PassportMapper passportMapper;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<UserDto> findAllDto() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public UserDto findByIdDto(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.map(userMapper::toDto).orElse(null);
    }

    @Transactional
    public User save(User user) {
        user.setCreateDate(LocalDate.from(LocalDateTime.now()));
        return userRepository.save(user);
    }

    @Transactional
    public UserDto saveDto(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setCreateDate(LocalDate.from(LocalDateTime.now()));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public Optional<User> update(Long id, User user) {
        Optional<User> optionalSavedUser = findById(id);
        User savedUser;
        if (optionalSavedUser.isEmpty()) {
            return optionalSavedUser;
        } else {
            savedUser = optionalSavedUser.get();
        }
        if (user.getEmail() != null) {
            savedUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            savedUser.setPassword(user.getPassword());
        }
        if (user.getSecurityQuestion() != null) {
            savedUser.setSecurityQuestion(user.getSecurityQuestion());
        }
        if (user.getAnswerQuestion() != null) {
            savedUser.setAnswerQuestion(user.getAnswerQuestion());
        }
        if (user.getFirstName() != null) {
            savedUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            savedUser.setLastName(user.getLastName());
        }
        if (user.getBirthDate() != null) {
            savedUser.setBirthDate(user.getBirthDate());
        }
        if (user.getGender() != null) {
            savedUser.setGender(user.getGender());
        }
        if (user.getPhoneNumber() != null) {
            savedUser.setPhoneNumber(user.getPhoneNumber());
        }

        if (user.getPassport() != null) {
            var newPassport = user.getPassport();
            var savePassport = savedUser.getPassport();
            if (savePassport != null) {
                newPassport.setId(savedUser.getPassport().getId());
            }
            savedUser.setPassport(newPassport);
        }

        if (user.getShippingAddressSet() != null) {
            Set<ShippingAddress> newShippAddr = new HashSet<>();
            Set<ShippingAddress> savedShippAddr = savedUser.getShippingAddressSet();
            if (savedShippAddr != null) {
                for (ShippingAddress address : user.getShippingAddressSet()) {
                    for (ShippingAddress addressId : savedShippAddr) {
                        Long shippAddress = addressId.getId();
                        address.setId(shippAddress);
                        address.setAddress(address.getAddress());
                        address.setDirections(address.getDirections());
                    }
                    newShippAddr.add(address);
                }
            }
            savedUser.setShippingAddressSet(newShippAddr);
        }

        if (user.getBankCardsSet() != null) {
            Set<BankCard> newCard = new HashSet<>();
            Set<BankCard> savedCard = savedUser.getBankCardsSet();
            if (savedCard != null) {
                for (BankCard bankCard : user.getBankCardsSet()) {
                    for (BankCard cardId : savedCard) {
                        Long bankCardId = cardId.getId();
                        bankCard.setId(bankCardId);
                    }
                    newCard.add(bankCard);
                }
            }
            savedUser.setBankCardsSet(newCard);
        }

        if (user.getRolesSet() != null) {
            savedUser.setRolesSet(user.getRolesSet());
        }
        return Optional.of(userRepository.save(savedUser));
    }

    @Transactional
    public Optional<User> delete(Long id) {
        Optional<User> optionalSavedExample = findById(id);
        if (optionalSavedExample.isEmpty()) {
            return optionalSavedExample;
        } else {
            userRepository.deleteById(id);
            return optionalSavedExample;
        }
    }

    @Transactional
    public UserDto updateDto(Long id, UserDto userDto) {
        Optional<User> optionalSavedUser = userRepository.findById(id);
        if (optionalSavedUser.isEmpty()) {
            return null;
        }
        User savedUser = optionalSavedUser.get();

        savedUser = updateUserFields(savedUser, userDto, bankCardMapper);
        User updatedUser = userRepository.save(savedUser);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public UserDto deleteDto(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }
        userRepository.deleteById(id);
        return userMapper.toDto(optionalUser.get());
    }

    private User updateUserFields(User user, UserDto userDto, BankCardMapper bankCardMapper) {
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setSecurityQuestion(userDto.getSecurityQuestion());
        user.setAnswerQuestion(userDto.getAnswerQuestion());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setBirthDate(userDto.getBirthDate());
        user.setGender(userDto.getGender());
        user.setPhoneNumber(userDto.getPhoneNumber());

        Set<ShippingAddress> shippingAddresses = userMapper.mapShippingAddressDtoToShippingAddressSetEntity(userDto.getShippingAddressDtos());
        user.setShippingAddressSet(shippingAddresses);

        if (bankCardMapper != null) {
            Set<BankCard> bankCards = userDto.getBankCardDtos().stream().map(bankCardMapper::toEntity).collect(Collectors.toSet());

            user.getBankCardsSet().clear();
            user.getBankCardsSet().addAll(bankCards);
        }

        Passport passport = passportMapper.toEntity(userDto.getPassportDto());
        user.setPassport(passport);

        Set<Role> roles = userMapper.mapRoleSetToStringSet(userDto.getRoles());
        user.setRolesSet(roles);

        return user;
    }

}