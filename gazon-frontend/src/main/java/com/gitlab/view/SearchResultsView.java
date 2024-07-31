package com.gitlab.view;

import com.gitlab.clients.ProductImageClient;
import com.gitlab.clients.ProductSearchClient;
import com.gitlab.clients.ReviewClient;
import com.gitlab.dto.ProductDto;
import com.gitlab.dto.ProductImageDto;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.util.List;

@Route("search")
public class SearchResultsView extends CommonView implements HasUrlParameter<String> {
    private final ProductSearchClient productSearchClient;
    private final ProductImageClient productImageClient;
    private final ReviewClient reviewClient;
    private final FlexLayout contentContainer;

    public SearchResultsView(ProductSearchClient productSearchClient, ProductImageClient productImageClient, ReviewClient reviewClient) {
        this.productSearchClient = productSearchClient;
        this.productImageClient = productImageClient;
        this.reviewClient = reviewClient;
        contentContainer = new FlexLayout();
        contentContainer.setWidth("1100px");
        contentContainer.setFlexDirection(FlexDirection.COLUMN);
        contentContainer.setFlexWrap(FlexWrap.WRAP);
        add(contentContainer);
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        if (parameter != null && !parameter.isEmpty()) {
            try {
                performSearch(parameter);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void performSearch(String query) throws InterruptedException {
        if (!query.isEmpty()) {
            ResponseEntity<List<ProductDto>> response = productSearchClient.searchOptionalPaginate(query,null,null);
            HttpStatus statusCode = response.getStatusCode();

            if (statusCode.is2xxSuccessful()) {
                List<ProductDto> products = response.getBody();

                if (products != null) {
                    displaySearchResults(products.stream().map(this::getProductView).toList());
                } else {
                    displayNoResults();
                }
            } else if (statusCode == HttpStatus.NO_CONTENT) {
                displayNoResults();
            } else {
                displayError();
            }
        }
    }

    private void displaySearchResults(List<ProductComponent> products) {
        contentContainer.removeAll();
        if (!products.isEmpty()) {
            for (ProductComponent product : products) {
                contentContainer.add(product);
            }
        } else {
            displayNoResults();
        }
    }

    private ProductComponent getProductView(ProductDto productDto) {
        return new ProductComponent(productDto, getImages(productDto), getReviewAmount(productDto));
    }

    private Image getImages(ProductDto productDto) {
        List<ProductImageDto> productImageDtos = productImageClient.getAllByProductId(productDto.getId()).getBody();
        Image resImage;
        if (productImageDtos != null && !productImageDtos.isEmpty()) {
            StreamResource sr = new StreamResource("image", () -> new ByteArrayInputStream(productImageDtos.get(0).getData()));
            sr.setContentType("image/png");
            resImage = new Image(sr, "profile-picture");

        } else {
            resImage = new Image("https://cdn-icons-png.flaticon.com/512/4054/4054617.png", "no image");
        }
        resImage.setWidth("250px");
        resImage.setHeight("250px");
        return resImage;
    }

    private Long getReviewAmount(ProductDto productDto) {
        ResponseEntity<Long> reviewAmount = reviewClient.getReviewAmount(productDto.getId());
        return reviewAmount.getBody();
    }

    private void displayNoResults() {
        contentContainer.removeAll();
        contentContainer.add(new H2("Ничего не найдено"));
    }

    private void displayError() {
        contentContainer.removeAll();
        contentContainer.add(new H2("Произошла ошибка при выполнении поиска"));
    }

    public static class ProductComponent extends Composite<HorizontalLayout> {
        public ProductComponent(ProductDto productDto, Image image, Long reviewAmount) {
            RouterLink productPageLink = new RouterLink("Страница товара", ProductPageView.class);
            Label productName = new Label(productDto.getName());
            Label productPrice = new Label(productDto.getPrice().toString() + " руб.");
            //нет структуры описания продуктов, поэтому пока просто берем первые 100 символов
            Label productDescription = new Label(productDto.getDescription()
                    .substring(0, Math.min(productDto.getDescription().length(), 100)) + "...");
            Label productRating = new Label(productDto.getRating());
            Label productReviews = new Label(reviewAmount + " отзывов");
            Icon ratingIcon = productDto.getRating().equals("Нет оценок") ? VaadinIcon.STAR_O.create() : VaadinIcon.STAR.create();
            Icon reviewIcon = VaadinIcon.CHAT.create();

            getContent().setPadding(true);
            getContent().setAlignItems(FlexComponent.Alignment.STRETCH);
            image.getElement().getStyle().set("cursor", "pointer");
//
            image.addClickListener(event -> {
                String searchResultsUrl = productPageLink.getHref() + "/" + productDto.getId();
                getUI().ifPresent(ui -> ui.navigate(searchResultsUrl));
            });

            getContent().add(image);
            getContent().setAlignSelf(FlexComponent.Alignment.START, image);

            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
            productName.getElement().getStyle().set("cursor", "text");
            productName.getElement().getStyle().set("font-weight", "bold");
            verticalLayout.add(productName);
            verticalLayout.add(productDescription);

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setPadding(true);
            horizontalLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
            horizontalLayout.add(ratingIcon, productRating);
            horizontalLayout.add(reviewIcon, productReviews);

            verticalLayout.add(horizontalLayout);

            getContent().add(verticalLayout);
            productPrice.getElement().getStyle().set("font-weight", "bold");
            getContent().add(productPrice);
        }
    }
}