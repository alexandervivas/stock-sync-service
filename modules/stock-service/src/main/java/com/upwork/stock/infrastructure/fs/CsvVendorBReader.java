package com.upwork.stock.infrastructure.fs;

import com.upwork.stock.application.dto.ExternalProductDto;
import com.upwork.stock.application.ports.VendorBReader;
import com.upwork.stock.config.StockIngestionProperties;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class CsvVendorBReader implements VendorBReader {

    private final Path csvPath;

    public CsvVendorBReader(StockIngestionProperties stockIngestionProperties) {
        this.csvPath = Path.of(stockIngestionProperties.vendorB().csvPath());
    }

    @Override
    public List<ExternalProductDto> readProducts() {
        if (!Files.exists(csvPath)) {
            return List.of();
        }

        try(BufferedReader bufferedReader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {

            CSVParser csvParser = new CSVParser(
                    bufferedReader,
                    CSVFormat
                            .DEFAULT
                            .builder()
                            .setHeader()
                            .setTrim(true)
                            .setIgnoreEmptyLines(true)
                            .setSkipHeaderRecord(true)
                            .build()
            );

            return csvParser.getRecords().stream()
                    .map(record -> new ExternalProductDto(
                            record.get("sku"),
                            record.get("name"),
                            Integer.parseInt(record.get("stockQuantity"))
                    ))
                    .toList();

        } catch (IOException exception) {
            throw new RuntimeException("Error reading CSV from " + csvPath, exception);
        }
    }
}
