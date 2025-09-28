package com.upwork.stock.application.ports;

import com.upwork.stock.application.dto.ExternalProductDto;

import java.util.List;

public interface VendorAClient {
    List<ExternalProductDto> fetchProducts();
}
