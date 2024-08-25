package com.gitlab.clients;

import com.gitlab.controllers.api.rest.ProductSearchRestApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "${app.feign.config.name}", contextId = "SearchProduct", url = "http://localhost:8089")
public interface ProductSearchClient extends ProductSearchRestApi {
}