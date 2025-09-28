package com.upwork.stock.infrastructure.persistence.jpa;

import com.upwork.stock.domain.product.Product;
import com.upwork.stock.domain.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(JpaProductRepository.class)
class JpaProductRepositoryTest {

    private final Product product = new Product("ABC123", "Product A", 10, "VendorA");

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void saveAndFindByCompositeKey() {
        productRepository.save(product);

        assertThat(
                productRepository.existsBySkuAndVendor(
                        product.getSku(),
                        product.getVendor()
                )
        ).isTrue();

        assertThat(
                productRepository.findBySkuAndVendor(
                        product.getSku(),
                        product.getVendor()
                )
        )
                .isPresent()
                .get()
                .extracting(Product::getStockQuantity)
                .isEqualTo(10);
    }

}