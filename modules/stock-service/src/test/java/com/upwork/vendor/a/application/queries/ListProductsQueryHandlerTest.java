package com.upwork.vendor.a.application.queries;

import com.upwork.vendor.a.application.view.ProductView;
import com.upwork.vendor.a.domain.product.Product;
import com.upwork.vendor.a.domain.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

class ListProductsQueryHandlerTest {

    @Test
    public void returnsViewsMappedFromDomain() {
        // arrange
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        ListProductsQueryHandler handler = new ListProductsQueryHandler(productRepository);

        when(productRepository.findAll()).thenReturn(List.of(
                new Product("ABC", "A", 10, "VendorA"),
                new Product("XYZ", "B",  0, "VendorB")
        ));

        // act
        List<ProductView> products = handler.handle(new ListProductsQuery());

        // assert
        assertThat(products).hasSize(2);
        assertThat(products.getFirst().sku()).isEqualTo("ABC");
        assertThat(products.get(1).stockQuantity()).isZero();
    }

}