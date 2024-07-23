package com.gitlab.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.gitlab.client.ExchangeRateClient;
import com.gitlab.model.ExchangeRateModel.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.bind.JAXBException;

public class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateClient exchangeRateClient;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    public ExchangeRateServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void USD_exchange_rate_should_not_be_null() throws JAXBException {
        String xmlResponse = "<ValCurs ID=\"R01235\" DateRange1=\"02.03.2024\" DateRange2=\"02.03.2024\" name=\"Foreign Currency Market Dynamic\">" +
                "<Record Date=\"02.03.2024\" Id=\"R01235\">" +
                "<Nominal>1</Nominal>" +
                "<Value>91,3336</Value>" +
                "<VunitRate>91,3336</VunitRate>" +
                "</Record>" +
                "</ValCurs>";
        when(exchangeRateClient.getExchangeRate(anyString(), anyString(), anyString())).thenReturn(xmlResponse);
        Double rate = exchangeRateService.getCurrentExchangeRate(CurrencyCode.USD);
        assertNotNull(rate);
    }
}
