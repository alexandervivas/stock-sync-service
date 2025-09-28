package com.upwork.vendor.b.infrastructure.fs;

import com.upwork.vendor.b.application.ports.CsvWriterPort;
import com.upwork.vendor.b.domain.ProductRow;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Component
public class FileCsvWriter implements CsvWriterPort {
    @Override
    public void write(Path path, List<ProductRow> productRows) {
        try {
            Path parent = path.getParent();
            if (parent != null) Files.createDirectories(parent);

            StringBuilder sb = new StringBuilder();
            sb.append("sku,name,stockQuantity\n");
            for (ProductRow productRow : productRows) {
                sb
                        .append(productRow.sku()).append(",")
                        .append(productRow.name()).append(",")
                        .append(productRow.stockQuantity()).append("\n");
            }

            Files.writeString(
                    path,
                    sb.toString(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV to " + path, e);
        }
    }
}
