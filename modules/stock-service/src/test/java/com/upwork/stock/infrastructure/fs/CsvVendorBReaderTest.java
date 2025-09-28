package com.upwork.stock.infrastructure.fs;

import com.upwork.stock.application.dto.ExternalProductDto;
import com.upwork.stock.config.StockIngestionProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CsvVendorBReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void readCsvFile() throws Exception {
        // arrange
        Path file = tempDir.resolve("stock.csv");
        Files.writeString(file, """
                                sku,name,stockQuantity
                                ABC123,Product A,10
                                XYZ456,Product B,0
                """);

        StockIngestionProperties stockIngestionProperties = new StockIngestionProperties(
                new StockIngestionProperties.VendorA("http://localhost:8081"),
                new StockIngestionProperties.VendorB(file.toString()),
                new StockIngestionProperties.Sync("0 */1 * * * *")
        );

        CsvVendorBReader csvVendorBReader = new CsvVendorBReader(stockIngestionProperties);

        // act
        List<ExternalProductDto> list = csvVendorBReader.readProducts();

        // assert
        assertThat(list).hasSize(2);
        assertThat(list.getFirst().sku()).isEqualTo("ABC123");
        assertThat(list.get(1).stockQuantity()).isZero();
    }

}