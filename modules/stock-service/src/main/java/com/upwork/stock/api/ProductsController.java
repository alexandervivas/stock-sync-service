package com.upwork.stock.api;

import com.upwork.stock.application.queries.ListProductsQuery;
import com.upwork.stock.application.queries.ListProductsQueryHandler;
import com.upwork.stock.application.view.ProductView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Products", description = "Query products and current stock")
@RestController
public class ProductsController {

    private final ListProductsQueryHandler listProductsQueryHandler;

    public ProductsController(ListProductsQueryHandler listProductsQueryHandler) {
        this.listProductsQueryHandler = listProductsQueryHandler;
    }

    @Operation(
            summary = "List products",
            description = "Retrieves all products with current stock and vendor."
    )
    @GetMapping("/products")
    public List<ProductView> listProducts() {
        return listProductsQueryHandler.handle(new ListProductsQuery());
    }
}
