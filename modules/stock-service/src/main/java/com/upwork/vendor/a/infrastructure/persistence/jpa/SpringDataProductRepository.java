package com.upwork.vendor.a.infrastructure.persistence.jpa;

import com.upwork.vendor.a.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySkuAndVendor(String sku, String vendor);
    Boolean existsBySkuAndVendor(String sku, String vendor);

}
