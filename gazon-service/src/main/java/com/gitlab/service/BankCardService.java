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
        log.info("Вызван метод findAll");
        return bankCardRepository.findAll();
    }

    public List<BankCardDto> findAllDto() {
        log.info("Вызван метод findAllDto");
        List<BankCard> bankCards = bankCardRepository.findAll();
        log.info("Найдено {} карт", bankCards.size());
        return bankCards.stream()
                .map(bankCardMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<BankCard> findById(Long id) {
        log.info("Вызван метод findById с id={}", id);
        return bankCardRepository.findById(id);
    }

    public Optional<BankCardDto> findByIdDto(Long id) {
        log.info("Вызван метод findByIdDto с id={}", id);
        return bankCardRepository.findById(id)
                .map(bankCardMapper::toDto);
    }

    public Page<BankCard> getPage(Integer page, Integer size) {
        log.info("Вызван метод getPage с параметрами: page={}, size={}", page, size);
        if (page == null || size == null || page < 0 || size < 1) {
            log.warn("Неверные параметры для страницы: page={}, size={}", page, size);
            return Page.empty();
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<BankCard> bankCardPage = bankCardRepository.findAll(pageRequest);
        log.info("Возвращена страница с {} картами", bankCardPage.getTotalElements());
        return bankCardPage;
    }

    public Page<BankCardDto> getPageDto(Integer page, Integer size) {
        log.info("Вызван метод getPageDto с параметрами: page={}, size={}", page, size);
        Page<BankCard> bankCardPage = getPage(page, size);
        return bankCardPage.map(bankCardMapper::toDto);
    }

    @Transactional
    public BankCardDto saveDto(BankCardDto bankCardDto) {
        log.info("Вызван метод saveDto с объектом BankCardDto: {}", bankCardDto);
        User user = userService.getAuthenticatedUser();
        BankCard savedBankCard = bankCardRepository.save(bankCardMapper.toEntity(bankCardDto));

        Set<BankCard> userBankCardSet = user.getBankCardsSet();
        userBankCardSet.add(savedBankCard);
        user.setBankCardsSet(userBankCardSet);
        userService.update(user.getId(), user);

        log.info("Карта сохранена: {}", savedBankCard);
        return bankCardMapper.toDto(savedBankCard);
    }

    public Optional<BankCardDto> updateDto(Long id, BankCardDto bankCardDto) {
        log.info("Вызван метод updateDto с id={} и объектом BankCardDto: {}", id, bankCardDto);
        Optional<BankCard> optionalSavedCard = findById(id);
        if (optionalSavedCard.isEmpty()) {
            log.warn("Карта с id={} не найдена", id);
            return Optional.empty();
        }

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
        log.info("Карта обновлена: {}", updatedCard);
        return Optional.ofNullable(bankCardMapper.toDto(updatedCard));
    }

    @Transactional
    public Optional<BankCardDto> deleteDto(Long id) {
        log.info("Вызван метод deleteDto с id={}", id);
        Optional<BankCard> optionalSavedCard = findById(id);
        if (optionalSavedCard.isPresent()) {
            BankCardDto deletedDto = bankCardMapper.toDto(optionalSavedCard.get());

            User user = userService.getAuthenticatedUser();
            Set<BankCard> userBankCardSet = user.getBankCardsSet();
            userBankCardSet.remove(optionalSavedCard.get());
            user.setBankCardsSet(userBankCardSet);
            userService.update(user.getId(), user);

            bankCardRepository.deleteById(id);
            log.info("Карта удалена: {}", deletedDto);
            return Optional.of(deletedDto);
        } else {
            log.warn("Карта с id={} не найдена", id);
            return Optional.empty();
        }
    }

    @Override
    public BankCardService clone() {
        try {
            return (BankCardService) super.clone();
        } catch (CloneNotSupportedException e) {
            log.error("Ошибка клонирования BankCardService", e);
            throw new AssertionError();
        }
    }
}
