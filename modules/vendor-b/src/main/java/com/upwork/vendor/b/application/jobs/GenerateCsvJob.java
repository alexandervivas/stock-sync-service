package com.upwork.vendor.b.application.jobs;

import com.upwork.vendor.b.application.ports.CsvWriterPort;
import com.upwork.vendor.b.config.VendorBProperties;
import com.upwork.vendor.b.domain.ProductRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class GenerateCsvJob {
    private static final Logger log = LoggerFactory.getLogger(GenerateCsvJob.class);

    private final CsvWriterPort csvWriterPort;
    private final VendorBProperties vendorBProperties;

    private final AtomicBoolean toggle = new AtomicBoolean(false);

    public GenerateCsvJob(CsvWriterPort csvWriterPort, VendorBProperties vendorBProperties) {
        this.csvWriterPort = csvWriterPort;
        this.vendorBProperties = vendorBProperties;
    }

    @Scheduled(cron = "${vendorb.schedule}")
    public void run() {

        Path csv = Path.of(vendorBProperties.csvPath());

        List<ProductRow> productRows = sampleRows();

        csvWriterPort.write(csv, productRows);

        log.info("Vendor-B wrote {} rows to {}", productRows.size(), csv);

    }

    private List<ProductRow> sampleRows() {
        if (!vendorBProperties.generateSample()) {
            return List.of();
        }

        // Alternate values of “Product B” (0 ↔ 5) to trigger detections “>0 -> 0”

        boolean on = toggle.getAndSet(!toggle.get());
        Integer alt = on ? 0 : 5;
        return List.of(
                new ProductRow("ABC123", "Product A", 10),
                new ProductRow("XYZ456", "Product B", alt)
        );
    }
}
