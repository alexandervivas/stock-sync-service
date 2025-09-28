package com.upwork.stock.application.services;

import com.upwork.stock.application.dto.ExternalProductDto;
import com.upwork.stock.application.ports.VendorAClient;
import com.upwork.stock.application.ports.VendorBReader;
import com.upwork.stock.config.StockIngestionProperties;
import com.upwork.stock.domain.product.Product;
import com.upwork.stock.domain.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StockSyncServiceTest {

    @Test
    void testSyncStock() {
        // arrange
        VendorAClient vendorAClient = mock(VendorAClient.class);
        VendorBReader vendorBReader = mock(VendorBReader.class);
        ProductRepository productRepository = mock(ProductRepository.class);

        when(vendorAClient.fetchProducts()).thenReturn(List.of(
                new ExternalProductDto("ABC123", "Product A", 8)
        ));
        when(vendorBReader.readProducts()).thenReturn(List.of(
                new ExternalProductDto("XYZ456", "Product B", 0)
        ));

        StockIngestionProperties stockIngestionProperties = new StockIngestionProperties(
                new StockIngestionProperties.VendorA("http://ignored"),
                new StockIngestionProperties.VendorB("ignored"),
                new StockIngestionProperties.Sync("0 */1 * * * *")
        );

        StockSyncService stockSyncService = new StockSyncService(vendorAClient, vendorBReader, productRepository, stockIngestionProperties);

        // act
        stockSyncService.syncOnce();

        // assert
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, atLeastOnce()).save(captor.capture());

        List<Product> savedProducts = captor.getAllValues();
        assertThat(savedProducts.size()).isEqualTo(2);
        assertThat(savedProducts).extracting(Product::getSku).containsExactly("ABC123", "XYZ456");
        assertThat(
                savedProducts
                        .stream()
                        .filter(product -> product.getSku().equalsIgnoreCase("XYZ456"))
                        .findFirst()
                        .get()
                        .getStockQuantity()
        ).isEqualTo(0);
    }

}