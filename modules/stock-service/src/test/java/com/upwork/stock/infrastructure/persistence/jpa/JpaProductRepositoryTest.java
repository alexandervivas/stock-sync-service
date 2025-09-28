package com.upwork.stock.infrastructure.persistence.jpa;

import com.upwork.stock.infrastructure.persistence.jpa.JpaProductRepository;
import com.upwork.stock.domain.product.Product;
import com.upwork.stock.domain.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(JpaProductRepository.class)
class JpaProductRepositoryTest {

    private final Product productA = new Product("ABC123", "Product A", 10, "VendorA");
    private final Product productB = new Product("XYZ456", "Product B", 5, "VendorB");

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void saveAndFindByCompositeKey() {
        productRepository.save(productA);

        assertThat(
                productRepository.existsBySkuAndVendor(
                        productA.getSku(),
                        productA.getVendor()
                )
        ).isTrue();

        assertThat(
                productRepository.findBySkuAndVendor(
                        productA.getSku(),
                        productA.getVendor()
                )
        )
                .isPresent()
                .get()
                .extracting(Product::getStockQuantity)
                .isEqualTo(10);
    }

    @Test
    public void uniqueConstraintOnSkuAndVendor() {
        productRepository.save(productB);

        Product anotherProductB = new Product("XYZ456", "Another name", 7, "VendorB");

        assertThrows(DataIntegrityViolationException.class, () -> productRepository.save(anotherProductB));
    }

}