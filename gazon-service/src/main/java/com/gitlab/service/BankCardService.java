package com.gitlab.service;

import com.gitlab.dto.BankCardDto;
import com.gitlab.mapper.BankCardMapper;
import com.gitlab.model.BankCard;
import com.gitlab.model.User;
import com.gitlab.repository.BankCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankCardService implements Cloneable {

    private final UserService userService;
    private final BankCardRepository bankCardRepository;
    private final BankCardMapper bankCardMapper;

    public List<BankCard> findAll() {
        return bankCardRepository.findAll();
    }

    public List<BankCardDto> findAllDto() {
        List<BankCard> bankCards = bankCardRepository.findAll();
        return bankCards.stream()
                .map(bankCardMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<BankCard> findById(Long id) {
        return bankCardRepository.findById(id);
    }

    public Optional<BankCardDto> findByIdDto(Long id) {
        return bankCardRepository.findById(id)
                .map(bankCardMapper::toDto);
    }

    public Page<BankCard> getPage(Integer page, Integer size) {

        if (page == null || size == null) {
            var bankCards = findAll();
            if (bankCards.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(bankCards);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return bankCardRepository.findAll(pageRequest);
    }

    public Page<BankCardDto> getPageDto(Integer page, Integer size) {
        log.info("Запрос попал в метод сервиса getPageDto с параметрами: page={}, size={}", page, size);
        if (page == null || size == null) {
            var bankCards = findAllDto();
            if (bankCards.isEmpty()) {
                return Page.empty();
            }
            return new PageImpl<>(bankCards);
        }
        if (page < 0 || size < 1) {
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<BankCard> bankCardPage = bankCardRepository.findAll(pageRequest);
        return bankCardPage.map(bankCardMapper::toDto);
    }

    @Transactional
    public BankCardDto saveDto(BankCardDto bankCardDto) {
        User user = userService.getAuthenticatedUser();

        BankCard savedBankCard = bankCardRepository.save(bankCardMapper.toEntity(bankCardDto));

        Set<BankCard> userBankCardSet = user.getBankCardsSet();
        userBankCardSet.add(savedBankCard);
        user.setBankCardsSet(userBankCardSet);
        userService.update(user.getId(), user);

        return bankCardMapper.toDto(savedBankCard);
    }


    public Optional<BankCardDto> updateDto(Long id, BankCardDto bankCardDto) {
        Optional<BankCard> optionalSavedCard = findById(id);
        if (optionalSavedCard.isEmpty()) {
            return Optional.empty();
        } else {
            BankCard savedCard = optionalSavedCard.get();

            if (bankCardDto.getCardNumber() != null) {
                savedCard.setCardNumber(bankCardDto.getCardNumber());
            }
            if (bankCardDto.getDueDate() != null) {
                savedCard.setDueDate(bankCardDto.getDueDate());
            }
            if (bankCardDto.getSecurityCode() != null) {
                savedCard.setSecurityCode(bankCardDto.getSecurityCode());
            }

            BankCard updatedCard = bankCardRepository.save(savedCard);
            return Optional.ofNullable(bankCardMapper.toDto(updatedCard));
        }
    }

    @Transactional
    public Optional<BankCardDto> deleteDto(Long id) {
        Optional<BankCard> optionalSavedCard = findById(id);
        if (optionalSavedCard.isPresent()) {
            BankCardDto deletedDto = bankCardMapper.toDto(optionalSavedCard.get());

            // fixme отрефакторить - надо каскады нормальные использовать
            User user = userService.getAuthenticatedUser();
            Set<BankCard> userBankCardSet = user.getBankCardsSet();
            userBankCardSet.remove(optionalSavedCard.get());
            user.setBankCardsSet(userBankCardSet);
            userService.update(user.getId(), user);

            bankCardRepository.deleteById(id);
            return Optional.of(deletedDto);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public BankCardService clone() {
        try {
            return (BankCardService) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
