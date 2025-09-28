package com.upwork.stock.domain.product;

public class Product {

    private Long id;
    private String sku;
    private String name;
    private Integer stockQuantity;
    private String vendor;

    public Product(String sku, String name, Integer stockQuantity, String vendor) {
        this.sku = sku;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.vendor = vendor;
    }

}
