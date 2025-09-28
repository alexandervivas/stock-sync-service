package com.upwork.vendor.a.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductsController {

    @GetMapping("/products")
    public List<ProductPayload> products() {
        return List.of(
                new ProductPayload("ABC123", "Product A", 8),
                new ProductPayload("LMN789", "Product C", 0)
        );
    }
}
