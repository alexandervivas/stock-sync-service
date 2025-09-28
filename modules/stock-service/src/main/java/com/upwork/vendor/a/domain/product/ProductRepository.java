package com.upwork.vendor.a.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    void save(Product product);
    Optional<Product> findBySkuAndVendor(String sku, String vendor);
    Boolean existsBySkuAndVendor(String sku, String vendor);
    List<Product> findAll();

}
