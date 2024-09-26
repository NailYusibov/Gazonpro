package com.gitlab.service;

import com.gitlab.dto.ShoppingCartDto;
import com.gitlab.dto.UserDto;
import com.gitlab.enums.EntityStatus;
import com.gitlab.exception.handler.UserNotAuthenticatedException;
import com.gitlab.mapper.BankCardMapper;
import com.gitlab.mapper.PassportMapper;
import com.gitlab.mapper.UserMapper;
import com.gitlab.model.*;
import com.gitlab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gitlab.util.ServiceUtils.updateFieldIfNotNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements Cloneable {

    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final BankCardMapper bankCardMapper;
    private final PassportMapper passportMapper;
    private ShoppingCartService shoppingCartService;

    @Autowired
    public void setShoppingCartService(@Lazy ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    public User getAuthenticatedUser() {
        log.info("getAuthenticatedUser");
        var authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        log.info("getAuthenticatedUser: Returning user with username: {}", authenticationToken.getName());
        return userRepository.findByUsername(authenticationToken.getName())
                .orElseThrow(() -> new UserNotAuthenticatedException(HttpStatus.UNAUTHORIZED, "Пользователь не аутентифицирован"));
    }

    public List<User> findAll() {
        var allUsers = userRepository.findAll();
        log.info("findAll: Returning {} users", allUsers.size());
        return allUsers;
    }

    public List<UserDto> findAllDto() {
        var allUsers = findAll();
        log.info("findAllDto: Returning {} users", allUsers.size());
        return findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<User> findUserById(Long id) {
        log.info("findUserById: Returning user with id: {}", id);
        return userRepository.findById(id);
    }

    public Optional<UserDto> findById(Long id) {
        log.info("findById: id: {}", id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent() && optionalUser.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            log.warn("findById: User with id: {} is deleted", id);
            return Optional.empty();
        }
        log.info("findById: Returning user with id: {}", id);
        return optionalUser.map(userMapper::toDto);
    }

    public Page<User> getPage(Integer page, Integer size) {
        log.info("getPage: Page: {} Size: {}", page, size);
        if (page == null || size == null) {
            var users = findAll();
            if (users.isEmpty()) {
                log.warn("getPage: Page is empty");
                return Page.empty();
            }
            log.warn("getPage: Returning {} users for page number: {}, page size: {}", users.size(), page, size);
            return new PageImpl<>(users);
        }
        if (page < 0 || size < 1) {
            log.warn("getPage: Page or size is not valid");
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("getPage: Returning {} users for page number: {}, page size: {}", userRepository.count(), page, size);
        return userRepository.findAll(pageRequest);
    }

    public Page<UserDto> getPageDto(Integer page, Integer size) {
        log.info("getPageDto: Page: {} Size: {}", page, size);
        if (page == null || size == null) {
            var users = findAllDto();
            if (users.isEmpty()) {
                log.warn("getPageDto: Page is empty");
                return Page.empty();
            }
            log.warn("getPageDto: Returning {} users for page number: {}, page size: {}", users.size(), page, size);
            return new PageImpl<>(users);
        }
        if (page < 0 || size < 1) {
            log.warn("getPageDto: page or size is not valid");
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageRequest);
        log.info("getPageDto: Returning {} users for page number: {}, page size: {}", userPage.getContent().size(), page, size);
        return userPage.map(userMapper::toDto);
    }

    @Transactional
    public User save(User user) {
        log.info("save: User: {}", user);
        user.setCreateDate(LocalDate.from(LocalDateTime.now()));
        user.setEntityStatus(EntityStatus.ACTIVE);
        user.getPassport().setEntityStatus(EntityStatus.ACTIVE);
        log.info("save: Returning user with id: {}", user.getId());
        return userRepository.save(user);
    }

    @Transactional
    public UserDto saveDto(UserDto userDto) {
        log.info("saveDto: UserDto: {}", userDto);
        User user = userMapper.toEntity(userDto);
        user.setCreateDate(LocalDate.from(LocalDateTime.now()));
        user.setEntityStatus(EntityStatus.ACTIVE);
        user.getPassport().setEntityStatus(EntityStatus.ACTIVE);

        UserDto newUserDto = userMapper.toDto(userRepository.save(user));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setUserId(newUserDto.getId());
        shoppingCartService.saveDto(shoppingCartDto);

        log.info("saveDto: Returning user with id: {}", user.getId());
        return newUserDto;
    }

    @Transactional
    public Optional<User> update(Long id, User user) {
        log.info("update: id: {} User: {}", id, user);
        Optional<User> optionalSavedUser = userRepository.findById(id);
        User savedUser;
        if (optionalSavedUser.isEmpty() || optionalSavedUser.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            log.warn("update: User with id: {} is deleted", id);
            return Optional.empty();
        } else {
            savedUser = optionalSavedUser.get();
        }

        updateFieldIfNotNull(savedUser::setEmail, user.getEmail());
        updateFieldIfNotNull(savedUser::setPassword, user.getPassword());
        updateFieldIfNotNull(savedUser::setSecurityQuestion, user.getSecurityQuestion());
        updateFieldIfNotNull(savedUser::setAnswerQuestion, user.getAnswerQuestion());
        updateFieldIfNotNull(savedUser::setFirstName, user.getFirstName());
        updateFieldIfNotNull(savedUser::setLastName, user.getLastName());
        updateFieldIfNotNull(savedUser::setBirthDate, user.getBirthDate());
        updateFieldIfNotNull(savedUser::setGender, user.getGender());
        updateFieldIfNotNull(savedUser::setPhoneNumber, user.getPhoneNumber());
        updateFieldIfNotNull(savedUser::setRolesSet, user.getRolesSet());
        updatePassport(user, savedUser);
        updateShippingAddress(user, savedUser);
        updateBankCards(user, savedUser);

        savedUser.setEntityStatus(EntityStatus.ACTIVE);
        savedUser.getPassport().setEntityStatus(EntityStatus.ACTIVE);
        log.info("update: Returning user with id: {}", savedUser.getId());
        return Optional.of(userRepository.save(savedUser));
    }

    @Transactional
    public Optional<User> delete(Long id) {
        log.info("delete: id: {}", id);
        Optional<User> optionalDeletedUser = userRepository.findById(id);
        if (optionalDeletedUser.isEmpty() || optionalDeletedUser.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            log.info("delete: User with id: {} is deleted", id);
            return Optional.empty();
        }

        User deletedUser = optionalDeletedUser.get();
        deletedUser.setEntityStatus(EntityStatus.DELETED);
        userRepository.save(deletedUser);
        log.info("delete: User with id: {} is deleted", id);
        return optionalDeletedUser;
    }

    @Transactional
    public Optional<UserDto> updateDto(Long id, UserDto userDto) {
        log.info("updateDto: UserDto: {}", userDto);
        Optional<User> optionalSavedUser = userRepository.findById(id);
        if (optionalSavedUser.isEmpty() || optionalSavedUser.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            log.info("updateDto: User with id: {} is deleted", id);
            return Optional.empty();
        }
        User savedUser = optionalSavedUser.get();

        updateUserFields(savedUser, userDto, bankCardMapper);
        User updatedUser = userRepository.save(savedUser);
        log.info("updateDto: Returning user with id: {}", id);
        return Optional.of(userMapper.toDto(updatedUser));
    }

    @Transactional
    public UserDto deleteDto(Long id) {
        log.info("deleteDto: id: {}", id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty() || optionalUser.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            log.info("deleteDto: User with id: {} is deleted", id);
            return null;
        }

        User deletedUser = optionalUser.get();
        deletedUser.setEntityStatus(EntityStatus.DELETED);
        userRepository.save(deletedUser);
        log.info("deleteDto: User with id: {} is deleted", id);
        return userMapper.toDto(optionalUser.get());
    }

    private User updateUserFields(User user, UserDto userDto, BankCardMapper bankCardMapper) {
        log.info("updateUserFields: user: {} userDto: {} bankCardMapper: {}", user, userDto, bankCardMapper);
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

        user.getPassport().setEntityStatus(EntityStatus.ACTIVE);
        log.info("updateUserFields: User {} fields updated", user);
        return user;
    }

    private void updatePassport(User user, User savedUser) {
        log.info("updatePassport: user: {}", user);
        updateFieldIfNotNull(newPassport -> {
            var savePassport = savedUser.getPassport();
            if (savePassport != null) {
                newPassport.setId(savedUser.getPassport().getId());
            }
            savedUser.setPassport(newPassport);
        }, user.getPassport());
    }

    private void updateShippingAddress(User user, User savedUser) {
        log.info("updateShippingAddress: user: {}", user);
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
    }

    private void updateBankCards(User user, User savedUser) {
        log.info("updateBankCards: user: {}", user);
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
    }

    @Override
    public UserService clone() {
        try {
            return (UserService) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
