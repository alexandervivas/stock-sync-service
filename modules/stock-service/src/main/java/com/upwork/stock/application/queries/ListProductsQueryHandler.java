package com.upwork.stock.application.queries;

import com.upwork.stock.application.mappers.ProductMapper;
import com.upwork.stock.application.view.ProductView;
import com.upwork.stock.domain.product.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListProductsQueryHandler {

    private final ProductRepository productRepository;

    public ListProductsQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductView> handle() {
        return productRepository
                .findAll()
                .stream()
                .map(ProductMapper::toProductView)
                .toList();
    }
}
