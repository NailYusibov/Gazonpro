package com.gitlab.service;

import com.gitlab.client.ExchangeRateClient;
import com.gitlab.model.CurrencyCode;
import com.gitlab.model.ValCurs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ExchangeRateService {

    @Autowired
    private ExchangeRateClient exchangeRateClient;

    private String getCurrentDate() {
        LocalDate today = LocalDate.now();
        if (today.getDayOfWeek() == DayOfWeek.SUNDAY) {
            today = today.minusDays(1);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return today.format(formatter);
    }

    public Double getCurrentExchangeRate(CurrencyCode currencyCode) throws JAXBException {
        String todayStr = getCurrentDate();
        String xmlResponse = exchangeRateClient.getExchangeRate(todayStr, todayStr, currencyCode.getCode());
        JAXBContext jaxbContext = JAXBContext.newInstance(ValCurs.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        ValCurs valCurs = (ValCurs) unmarshaller.unmarshal(new StringReader(xmlResponse));
        return Double.parseDouble(valCurs.getRecord().get(0).getValue().replace(",", "."));

    }
}

