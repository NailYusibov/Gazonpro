package com.gitlab.service;

//import com.gitlab.mapper.BankCardMapper;
//import com.gitlab.mapper.PassportMapper;
import com.gitlab.model.User;
import com.gitlab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
        import org.springframework.stereotype.Service;

        import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /*private final UserMapper userMapper;
    private final BankCardMapper bankCardMapper;
    private final PassportMapper passportMapper;
    private ShoppingCartService shoppingCartService;

    @Autowired
    public void setShoppingCartService(@Lazy ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<UserDto> findAllDto() {
        return findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }*/

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    /*public Optional<UserDto> findById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent() && optionalUser.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            return Optional.empty();
        }
        return optionalUser.map(userMapper::toDto);
    }

    public Page<User> getPage(Integer page, Integer size) {
        if (page == null || size == null) {
            var users = findAll();
            if (users.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(users);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findAll(pageRequest);
    }

    public Page<UserDto> getPageDto(Integer page, Integer size) {

        if (page == null || size == null) {
            var users = findAllDto();
            if (users.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(users);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageRequest);
        return userPage.map(userMapper::toDto);
    }

    @Transactional
    public User save(User user) {
        user.setCreateDate(LocalDate.from(LocalDateTime.now()));
        user.setEntityStatus(EntityStatus.ACTIVE);
        user.getPassport().setEntityStatus(EntityStatus.ACTIVE);
        return userRepository.save(user);
    }

    @Transactional
    public UserDto saveDto(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setCreateDate(LocalDate.from(LocalDateTime.now()));
        user.setEntityStatus(EntityStatus.ACTIVE);
        user.getPassport().setEntityStatus(EntityStatus.ACTIVE);

        UserDto newUserDto = userMapper.toDto(userRepository.save(user));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setUserId(newUserDto.getId());
        shoppingCartService.saveDto(shoppingCartDto);

        return newUserDto;
    }

    @Transactional
    public Optional<User> update(Long id, User user) {
        Optional<User> optionalSavedUser = userRepository.findById(id);
        User savedUser;
        if (optionalSavedUser.isEmpty() || optionalSavedUser.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            return Optional.empty();
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

        savedUser.setEntityStatus(EntityStatus.ACTIVE);
        savedUser.getPassport().setEntityStatus(EntityStatus.ACTIVE);

        return Optional.of(userRepository.save(savedUser));
    }

    @Transactional
    public Optional<User> delete(Long id) {
        Optional<User> optionalDeletedUser = userRepository.findById(id);
        if (optionalDeletedUser.isEmpty() || optionalDeletedUser.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            return Optional.empty();
        }

        User deletedUser = optionalDeletedUser.get();
        deletedUser.setEntityStatus(EntityStatus.DELETED);
        userRepository.save(deletedUser);

        return optionalDeletedUser;
    }

    @Transactional
    public Optional<UserDto> updateDto(Long id, UserDto userDto) {
        Optional<User> optionalSavedUser = userRepository.findById(id);
        if (optionalSavedUser.isEmpty() || optionalSavedUser.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            return Optional.empty();
        }
        User savedUser = optionalSavedUser.get();

        updateUserFields(savedUser, userDto, bankCardMapper);
        User updatedUser = userRepository.save(savedUser);
        return Optional.of(userMapper.toDto(updatedUser));
    }

    @Transactional
    public UserDto deleteDto(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty() || optionalUser.get().getEntityStatus().equals(EntityStatus.DELETED)) {
            return null;
        }

        User deletedUser = optionalUser.get();
        deletedUser.setEntityStatus(EntityStatus.DELETED);
        userRepository.save(deletedUser);

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

        user.getPassport().setEntityStatus(EntityStatus.ACTIVE);

        return user;
    }*/

}