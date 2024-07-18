package com.gitlab.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "currencyClient", url = "https://www.cbr.ru")
public interface ExchangeRateClient {

    @GetMapping("/scripts/XML_dynamic.asp")
    String getExchangeRate(
            @RequestParam("date_req1") String startDate,
            @RequestParam("date_req2") String endDate,
            @RequestParam("VAL_NM_RQ") String currencyCode
    );
}

