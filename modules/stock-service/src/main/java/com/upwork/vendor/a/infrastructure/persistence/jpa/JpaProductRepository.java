package com.upwork.vendor.a.infrastructure.persistence.jpa;

import com.upwork.vendor.a.domain.product.Product;
import com.upwork.vendor.a.domain.product.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaProductRepository implements ProductRepository {
    private final SpringDataProductRepository delegate;

    public JpaProductRepository(SpringDataProductRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public void save(Product product) {
        delegate.save(product);
    }

    @Override
    public Optional<Product> findBySkuAndVendor(String sku, String vendor) {
        return delegate.findBySkuAndVendor(sku, vendor);
    }

    @Override
    public Boolean existsBySkuAndVendor(String sku, String vendor) {
        return delegate.existsBySkuAndVendor(sku, vendor);
    }

    // TODO: 10/12/2020 Add pagination
    @Override
    public List<Product> findAll() {
        return delegate.findAll();
    }
}
