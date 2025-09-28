package com.upwork.stock.api;

import com.upwork.stock.application.queries.ListProductsQuery;
import com.upwork.stock.application.queries.ListProductsQueryHandler;
import com.upwork.stock.application.view.ProductView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductsController {

    private final ListProductsQueryHandler listProductsQueryHandler;

    public ProductsController(ListProductsQueryHandler listProductsQueryHandler) {
        this.listProductsQueryHandler = listProductsQueryHandler;
    }

    @GetMapping("/products")
    public List<ProductView> listProducts() {
        return listProductsQueryHandler.handle(new ListProductsQuery());
    }
}
