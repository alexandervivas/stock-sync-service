package com.upwork.stock.application.services;

import com.upwork.stock.application.dto.ExternalProductDto;
import com.upwork.stock.application.ports.VendorAClient;
import com.upwork.stock.application.ports.VendorBReader;
import com.upwork.stock.config.StockIngestionProperties;
import com.upwork.stock.domain.product.Product;
import com.upwork.stock.domain.product.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class StockSyncService {

    private static final Logger log = LoggerFactory.getLogger(StockSyncService.class);

    private final VendorAClient vendorAClient;
    private final VendorBReader vendorBReader;
    private final ProductRepository productRepository;

    private final ReentrantLock running = new ReentrantLock();
    private final StockIngestionProperties stockIngestionProperties;

    public StockSyncService(VendorAClient vendorAClient, VendorBReader vendorBReader, ProductRepository productRepository, StockIngestionProperties stockIngestionProperties) {
        this.vendorAClient = vendorAClient;
        this.vendorBReader = vendorBReader;
        this.productRepository = productRepository;
        this.stockIngestionProperties = stockIngestionProperties;
    }

    public void syncOnce() {
        List<ExternalProductDto> productsVendorA = vendorAClient.fetchProducts();
        List<ExternalProductDto> productsVendorB = vendorBReader.readProducts();

        productsVendorA.forEach(product -> upsert("VendorA", product));
        productsVendorB.forEach(product -> upsert("VendorB", product));
    }

    @Scheduled(cron = "${ingestion.sync.cron}")
    @Transactional
    public void scheduledSync() {
        if (!running.tryLock()) {
            log.warn("Previous sync is still running, skipping this tick (cron={})", stockIngestionProperties.sync().cron());
            return;
        }

        try {
            log.info("Starting scheduled sync (cron={})", stockIngestionProperties.sync().cron());

            syncOnce();
            log.info("Finished scheduled sync (cron={})", stockIngestionProperties.sync().cron());
        } finally {
            running.unlock();
        }

    }

    private void upsert(String vendor, ExternalProductDto externalProductDto) {
        productRepository.findBySkuAndVendor(externalProductDto.sku(), vendor).ifPresentOrElse(existing -> {
            if (productRanOutOfStock(externalProductDto, existing)) {
                log.info("OUT-OF-STOCK detected sku={} vendor={} prev={} now=0", externalProductDto.sku(), vendor, existing.getStockQuantity());
            }

            productRepository.save(
                    existing
                            .rename(externalProductDto.name())
                            .withStockQuantity(externalProductDto.stockQuantity())
            );
        }, () -> {
            Product product = new Product(
                    externalProductDto.sku(),
                    externalProductDto.name(),
                    externalProductDto.stockQuantity(),
                    vendor
            );

            productRepository.save(product);
        });
    }

    private boolean productRanOutOfStock(ExternalProductDto externalProductDto, Product existing) {
        return existing.getStockQuantity() != null
                && existing.getStockQuantity() > 0
                && externalProductDto.stockQuantity() == 0;
    }
}
