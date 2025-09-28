package com.upwork.vendor.a.application.mappers;

import com.upwork.vendor.a.application.view.ProductView;
import com.upwork.vendor.a.domain.product.Product;

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
