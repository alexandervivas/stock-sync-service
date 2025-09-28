package com.upwork.stock.infrastructure.http;

import com.upwork.stock.application.dto.ExternalProductDto;
import com.upwork.stock.application.ports.VendorAClient;
import com.upwork.stock.config.StockIngestionProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
public class HttpVendorAClient implements VendorAClient {

    private final RestClient restClient;

    public HttpVendorAClient(RestClient.Builder restClientBuilder, StockIngestionProperties stockIngestionProperties) {
        this.restClient = restClientBuilder
                .baseUrl(stockIngestionProperties.vendorA().baseUrl())
                .build();
    }

    @Override
    public List<ExternalProductDto> fetchProducts() {
        ExternalProductDto[] response = restClient
                .get()
                .uri("/products")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ExternalProductDto[].class);

        return response == null ? List.of() : Arrays.asList(response);
    }
}
