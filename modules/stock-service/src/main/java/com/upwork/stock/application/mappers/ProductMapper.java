package com.upwork.stock.application.mappers;

import com.upwork.stock.application.view.ProductView;
import com.upwork.stock.domain.product.Product;

public final class ProductMapper {
    private ProductMapper() {

    }

    public static ProductView toProductView(Product product) {
        return new ProductView(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getStockQuantity(),
                product.getVendor()
        );
    }
}
