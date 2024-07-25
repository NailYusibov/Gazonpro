package com.gitlab.service;

import com.gitlab.dto.BankCardDto;
import com.gitlab.mapper.BankCardMapper;
import com.gitlab.model.BankCard;
import com.gitlab.repository.BankCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.gitlab.TestUtil.generateUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankCardServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private BankCardRepository bankCardRepository;
    @Spy
    private BankCardMapper bankCardMapper = Mappers.getMapper(BankCardMapper.class);
    @InjectMocks
    private BankCardService bankCardService;

    @Test
    void should_find_all_bankCards() {
        List<BankCard> expectedResult = generateBankCards();
        when(bankCardRepository.findAll()).thenReturn(generateBankCards());

        List<BankCard> actualResult = bankCardService.findAll();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void should_find_bankCard_by_id() {
        long id = 1L;
        BankCard expectedResult = generateBankCard();
        when(bankCardRepository.findById(id)).thenReturn(Optional.of(expectedResult));

        Optional<BankCard> actualResult = bankCardService.findById(id);

        assertEquals(expectedResult, actualResult.orElse(null));
    }

    @Test
    void should_save_bankCard() {
        Long id = 1L;
        BankCard expectedResult = generateBankCard();
        BankCardDto bankCardDto = generateBankCardDto();
        bankCardDto.setId(id);

        when(bankCardMapper.toDto(any(BankCard.class))).thenReturn(bankCardDto);
        when(bankCardMapper.toEntity(bankCardDto)).thenReturn(expectedResult);
        when(bankCardRepository.save(expectedResult)).thenReturn(expectedResult);
        when(userService.getAuthenticatedUser()).thenReturn(generateUser());

        BankCard actualResult = bankCardMapper.toEntity(bankCardService.saveDto(bankCardMapper.toDto(expectedResult)));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void should_update_bankCard() {
        long id = 1L;
        BankCardDto bankCardDtoUpdate = generateBankCardDto();
        bankCardDtoUpdate.setCardNumber(/*modified*/"12345678"/*modified*/);
        bankCardDtoUpdate.setDueDate(LocalDate.MIN);
        bankCardDtoUpdate.setSecurityCode(123);


        BankCard bankCardBeforeUpdate = new BankCard(id, /*unmodified*/"23456789"/*unmodified*/, LocalDate.MIN, 123);
        BankCard updatedBankCard = new BankCard(id, /*modified*/"12345678"/*modified*/, LocalDate.MIN, 123);

        when(bankCardMapper.toDto(any(BankCard.class))).thenReturn(bankCardDtoUpdate);
        when(bankCardMapper.toEntity(bankCardDtoUpdate)).thenReturn(updatedBankCard);

        when(bankCardRepository.findById(id)).thenReturn(Optional.of(bankCardBeforeUpdate));
        when(bankCardRepository.save(updatedBankCard)).thenReturn(updatedBankCard);

        Optional<BankCard> actualResult = Optional.of(bankCardMapper.toEntity(bankCardService.updateDto(id, bankCardDtoUpdate).get()));

        assertEquals(updatedBankCard, actualResult.orElse(null));
    }

    @Test
    void should_not_update_bankCard_when_entity_not_found() {
        long id = 1L;
        BankCardDto bankCardDtoToUpdate = generateBankCardDto();
        bankCardDtoToUpdate.setCardNumber(/*modified*/"12345678"/*modified*/);

        when(bankCardRepository.findById(id)).thenReturn(Optional.empty());

        Optional<BankCardDto> actualResult = bankCardService.updateDto(id, bankCardDtoToUpdate);

        verify(bankCardRepository, never()).save(any());
        assertNull(actualResult.orElse(null));
    }

    @Test
    void should_delete_bankCard() {
        long id = 1L;
        when(userService.getAuthenticatedUser()).thenReturn(generateUser());
        when(bankCardRepository.findById(id)).thenReturn(Optional.of(generateBankCard()));

        bankCardService.deleteDto(id);

        verify(bankCardRepository).deleteById(id);
    }

    @Test
    void should_not_delete_bankCard_when_entity_not_found() {
        long id = 1L;
        when(bankCardRepository.findById(id)).thenReturn(Optional.empty());

        bankCardService.deleteDto(id);

        verify(bankCardRepository, never()).deleteById(anyLong());
    }


    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void should_not_updated_cardNumber_field_if_null() {
        long id = 1L;
        BankCardDto bankCardDtoToUpdate = generateBankCardDto();
        bankCardDtoToUpdate.setCardNumber(null);

        BankCard bankCardBeforeUpdate = new BankCard(id, /*unmodified*/"12345678"/*unmodified*/, LocalDate.MIN, 123);

        when(bankCardMapper.toDto(any(BankCard.class))).thenReturn(bankCardDtoToUpdate);
        when(bankCardMapper.toEntity(bankCardDtoToUpdate)).thenReturn(bankCardBeforeUpdate);

        when(bankCardRepository.findById(id)).thenReturn(Optional.of(bankCardBeforeUpdate));
        when(bankCardRepository.save(bankCardBeforeUpdate)).thenReturn(bankCardBeforeUpdate);

        Optional<BankCard> actualResult = Optional.of(bankCardMapper.toEntity(bankCardService.updateDto(id, bankCardDtoToUpdate).get()));

        verify(bankCardRepository).save(bankCardBeforeUpdate);
        assertEquals(bankCardBeforeUpdate, actualResult.orElse(null));
        assertEquals("12345678", bankCardBeforeUpdate.getCardNumber());
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void should_not_updated_dueDate_field_if_null() {
        long id = 1L;
        BankCardDto bankCardDtoToUpdate = generateBankCardDto();
        bankCardDtoToUpdate.setDueDate(null);

        BankCard bankCardBeforeUpdate = new BankCard(id, "cardNumber", /*Unmodified*/ LocalDate.MIN /*Unmodified*/, 123);

        when(bankCardMapper.toDto(any(BankCard.class))).thenReturn(bankCardDtoToUpdate);
        when(bankCardMapper.toEntity(bankCardDtoToUpdate)).thenReturn(bankCardBeforeUpdate);

        when(bankCardRepository.findById(id)).thenReturn(Optional.of(bankCardBeforeUpdate));
        when(bankCardRepository.save(bankCardBeforeUpdate)).thenReturn(bankCardBeforeUpdate);

        Optional<BankCard> actualResult = Optional.of(bankCardMapper.toEntity(bankCardService.updateDto(id, bankCardDtoToUpdate).get()));

        verify(bankCardRepository).save(bankCardBeforeUpdate);
        assertEquals(bankCardBeforeUpdate, actualResult.orElse(null));
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void should_not_updated_securityCode_field_if_null() {
        long id = 1L;
        BankCardDto bankCardDtoToUpdate = generateBankCardDto();
        bankCardDtoToUpdate.setSecurityCode(null);

        BankCard bankCardBeforeUpdate = new BankCard(id, "cardNumber", LocalDate.MIN, /*Unmodified*/123/*Unmodified*/);

        when(bankCardMapper.toDto(any(BankCard.class))).thenReturn(bankCardDtoToUpdate);
        when(bankCardMapper.toEntity(bankCardDtoToUpdate)).thenReturn(bankCardBeforeUpdate);

        when(bankCardRepository.findById(id)).thenReturn(Optional.of(bankCardBeforeUpdate));
        when(bankCardRepository.save(bankCardBeforeUpdate)).thenReturn(bankCardBeforeUpdate);

        Optional<BankCard> actualResult = Optional.of(bankCardMapper.toEntity(bankCardService.updateDto(id, bankCardDtoToUpdate).get()));

        verify(bankCardRepository).save(bankCardBeforeUpdate);
        assertEquals(bankCardBeforeUpdate, actualResult.orElse(null));
    }

    private List<BankCard> generateBankCards() {
        return List.of(
                new BankCard(1L, "cardNumber1", LocalDate.MIN, 123),
                new BankCard(2L, "cardNumber2", LocalDate.MIN, 123),
                new BankCard(3L, "cardNumber3", LocalDate.MIN, 123),
                new BankCard(4L, "cardNumber4", LocalDate.MIN, 123),
                new BankCard(5L, "cardNumber5", LocalDate.MIN, 123));
    }

    private BankCard generateBankCard() {
        return new BankCard(1L, "cardNumber1", LocalDate.MIN, 123);
    }

    private BankCardDto generateBankCardDto() {
        return new BankCardDto();
    }

}
