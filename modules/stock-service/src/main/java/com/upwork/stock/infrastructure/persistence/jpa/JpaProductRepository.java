package com.upwork.stock.infrastructure.persistence.jpa;

import com.upwork.stock.domain.product.Product;
import com.upwork.stock.domain.product.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaProductRepository implements ProductRepository {
    @Override
    public Product save(Product product) {
        return null;
    }

    @Override
    public Optional<Product> findBySkuAndVendor(String sku, String vendor) {
        return Optional.empty();
    }

    @Override
    public Boolean existsBySkuAndVendor(String sku, String vendor) {
        return null;
    }
}
