package com.upwork.stock.infrastructure.persistence.jpa;

import com.upwork.stock.domain.product.Product;
import com.upwork.stock.domain.product.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaProductRepository implements ProductRepository {
    private final SpringDataProductRepository delegate;

    public JpaProductRepository(SpringDataProductRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public Product save(Product product) {
        return delegate.save(product);
    }

    @Override
    public Optional<Product> findBySkuAndVendor(String sku, String vendor) {
        return delegate.findBySkuAndVendor(sku, vendor);
    }

    @Override
    public Boolean existsBySkuAndVendor(String sku, String vendor) {
        return delegate.existsBySkuAndVendor(sku, vendor);
    }
}
