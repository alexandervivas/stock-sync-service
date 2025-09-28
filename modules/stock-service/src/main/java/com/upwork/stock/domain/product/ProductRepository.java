package com.upwork.stock.domain.product;

import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);
    Optional<Product> findBySkuAndVendor(String sku, String vendor);
    Boolean existsBySkuAndVendor(String sku, String vendor);

}
