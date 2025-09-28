package com.upwork.stock.domain.product;

import jakarta.persistence.*;

@Entity
@Table(name = "products",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_product_sku_vendor",
                columnNames = {"sku", "vendor"}
        )
)
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String sku;

    @Column(nullable = false, length = 256)
    private String name;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false, length = 64)
    private String vendor;

    public Product(String sku, String name, Integer stockQuantity, String vendor) {
        this.sku = sku;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.vendor = vendor;
    }

    protected Product() {

    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public String getVendor() {
        return vendor;
    }
}
