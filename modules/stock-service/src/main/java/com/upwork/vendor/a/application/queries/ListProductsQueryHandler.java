package com.upwork.vendor.a.application.queries;

import com.upwork.vendor.a.application.mappers.ProductMapper;
import com.upwork.vendor.a.application.view.ProductView;
import com.upwork.vendor.a.domain.product.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListProductsQueryHandler {

    private final ProductRepository productRepository;

    public ListProductsQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductView> handle(ListProductsQuery query) {
        return productRepository
                .findAll()
                .stream()
                .map(ProductMapper::toProductView)
                .toList();
    }
}
