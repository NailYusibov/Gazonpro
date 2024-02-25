package com.gitlab.view;

import com.gitlab.clients.ProductClient;
import com.gitlab.dto.ProductDto;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("catalog")
public class CatalogView extends VerticalLayout {

    private final ProductClient productClient;

    private Grid<ProductDto> productGrid = new Grid<>(ProductDto.class);

    @Autowired
    public CatalogView(ProductClient productClient) {
        this.productClient = productClient;
        initView();
        loadData();
    }

    private void initView() {
        productGrid.setColumns("name", "description", "stockCount", "isAdult", "code", "weight", "price", "rating");
        productGrid.setHeightByRows(true);
        add(productGrid);
    }

    private void loadData() {
        List<ProductDto> products = productClient.getPage(null, null, null).getBody();
        if (products != null) {
            productGrid.setItems(products);
        }
    }
}