package com.upwork.stock.api;

import com.upwork.stock.api.ProductsController;
import com.upwork.stock.application.queries.ListProductsQuery;
import com.upwork.stock.application.queries.ListProductsQueryHandler;
import com.upwork.stock.application.view.ProductView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductsController.class)
class ProductsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListProductsQueryHandler listProductsQueryHandler;

    @Test
    public void listProducts() throws Exception {
        when(listProductsQueryHandler.handle(any(ListProductsQuery.class))).thenReturn(List.of(
                new ProductView(1L, "ABC", "A", 10, "VendorA"),
                new ProductView(2L, "XYZ", "B",  0, "VendorB")
        ));

        mockMvc
                .perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].sku", is("ABC")))
                .andExpect(jsonPath("$[1].stockQuantity", is(0)));
    }
}