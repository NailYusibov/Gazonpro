package com.gitlab.view;

import com.gitlab.clients.ProductImageClient;
import com.gitlab.clients.ProductPageClient;
import com.gitlab.clients.ReviewClient;
import com.gitlab.dto.ProductDto;
import com.gitlab.dto.ProductImageDto;
import com.gitlab.dto.ReviewDto;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;
import com.vaadin.flow.server.StreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Route(value = "product_page")
public class ProductPageView extends CommonView implements HasUrlParameter<String> {
    private final ProductImageClient productImageClient;
    private final ProductPageClient productPageClient;
    private static VerticalLayout contentContainer;
    private static ReviewClient reviewClient;
    private final Map<Long, Image> fullSizedImages = new HashMap<>();

    public ProductPageView(ProductImageClient productImageClient, ProductPageClient productPageClient, ReviewClient reviewClient) {
        this.productImageClient = productImageClient;
        this.productPageClient = productPageClient;
        ProductPageView.reviewClient = reviewClient;
        contentContainer = new VerticalLayout();
        contentContainer.setMargin(true);
        contentContainer.setWidth("1100px");
        add(contentContainer);
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        if (parameter != null && !parameter.isEmpty()) {
            try {
                getProductDtoById(Long.parseLong(parameter));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void getProductDtoById(Long id) throws InterruptedException {
        ResponseEntity<ProductDto> response = productPageClient.get(id);
        HttpStatus statusCode = response.getStatusCode();

        if (statusCode.is2xxSuccessful()) {
            displayProductPage(response.getBody());
        } else if (statusCode == HttpStatus.NO_CONTENT) {
            displayNoResult();
        } else {
            displayError();
        }
    }

    private void displayProductPage(ProductDto product) {
        contentContainer.removeAll();
        contentContainer.add(new ProductPage(product, getReviewAmount(product), fullSizedImages, getImagesPreview(product)));
    }

    private void displayNoResult() {
        contentContainer.removeAll();
        contentContainer.add(new H2("Ничего не найдено"));
    }

    private void displayError() {
        contentContainer.removeAll();
        contentContainer.add(new H2("Произошла ошибка при отображении товара"));
    }

    private Long getReviewAmount(ProductDto productDto) {
        ResponseEntity<Long> reviewAmount = reviewClient.getReviewAmount(productDto.getId());
        return reviewAmount.getBody();
    }

    private static List<ReviewDto> getReviewList(Long product_id) {
        return reviewClient.getAllReviewsForAProductByProductId(product_id).getBody();
    }

    /**
     * Попытка создать "карусель"
     * Метод создает map картинок-превью с ключами по их id и разрешением 50x50
     * Одновременно заполняется map с полноразмерными картинками 300x300, где в качестве ключей также выступает id картинки
     * Это позволит менять полноразмерную картинку в зависимости от того, на какое из превью нажали
     * В случае отсутствия картинок в map будет добавлена картинка-заглушка
     */
    private Map<Long, Image> getImagesPreview(ProductDto productDto) {
        List<ProductImageDto> productImageList = productImageClient.getAllByProductId(productDto.getId()).getBody();
        Map<Long, Image> previewImages = new HashMap<>();
        if (productImageList != null && !productImageList.isEmpty()) {
            //пока нет ограничения по загрузке картинок на один товар - лимитируем вручную
            productImageList = productImageList.stream().limit(5).toList();
            for (ProductImageDto imageDto : productImageList) {
                StreamResource sr = new StreamResource("image", () -> new ByteArrayInputStream(imageDto.getData()));
                sr.setContentType("image/png");

                Image image = new Image(sr, imageDto.getId().toString());
                image.setWidth("300px");
                image.setHeight("300px");
                fullSizedImages.put(imageDto.getId(), image);

                StreamResource srPreview = new StreamResource("image", () -> new ByteArrayInputStream(imageDto.getData()));
                srPreview.setContentType("image/png");

                Image imagePreview = new Image(sr, imageDto.getId().toString());
                imagePreview.setWidth("50px");
                imagePreview.setHeight("50px");
                previewImages.put(imageDto.getId(), imagePreview);
            }

        } else {
            Image image = new Image("https://cdn-icons-png.flaticon.com/512/4054/4054617.png", "no image");
            image.setWidth("300px");
            image.setHeight("300px");
            fullSizedImages.put(1L, image);

            Image imagePreview = new Image("https://cdn-icons-png.flaticon.com/512/4054/4054617.png", "no image");
            imagePreview.setWidth("50px");
            imagePreview.setHeight("50px");
            previewImages.put(1L, imagePreview);
        }

        return previewImages;
    }

    private static HorizontalLayout evaluateRating(ProductDto productDto) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        if (productDto.getRating().equals("Нет оценок")) {
            horizontalLayout.add(new Label(productDto.getRating()));
            return horizontalLayout;
        }

//        double rating = Double.parseDouble(productDto.getRating());
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        Number number;
        try {
            number = format.parse(productDto.getRating());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        double rating = number.doubleValue();
        if (rating > 4.5D) {
            horizontalLayout.add(VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR.create());
        } else if (rating > 4.0D) {
            horizontalLayout.add(VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR_HALF_LEFT.create());
        } else if (rating > 3.5D) {
            horizontalLayout.add(VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR_O.create());
        } else if (rating > 3.0D) {
            horizontalLayout.add(VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR_HALF_LEFT.create(), VaadinIcon.STAR_O.create());
        } else if (rating > 2.5D) {
            horizontalLayout.add(VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create());
        } else if (rating > 2.0D) {
            horizontalLayout.add(VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR_HALF_LEFT.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create());
        } else if (rating > 1.5D) {
            horizontalLayout.add(VaadinIcon.STAR.create(), VaadinIcon.STAR.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create());
        } else if (rating > 1.0D) {
            horizontalLayout.add(VaadinIcon.STAR.create(), VaadinIcon.STAR_HALF_LEFT.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create());
        } else if (rating > 0.5D) {
            horizontalLayout.add(VaadinIcon.STAR.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create());
        } else if (rating > 0.0D) {
            horizontalLayout.add(VaadinIcon.STAR_HALF_LEFT.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create());
        } else {
            horizontalLayout.add(VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create(), VaadinIcon.STAR_O.create());
        }

        horizontalLayout.add(new Label(productDto.getRating()));
        return horizontalLayout;
    }

    private static void getReviews(Long productId) {
        UI.getCurrent().access(() -> {
            List<ReviewDto> reviews = getReviewList(productId);
            if (reviews != null) {
//                  contentContainer.add(new Label("<b>Отзывы:</b>", ContentMode.HTML));
                contentContainer.add(new Label("Отзывы: "));
                reviews.forEach(review -> {
//                  contentContainer.add(new Label(reviews.toString()));
                    contentContainer.add(new Label("Рейтинг: "));
                    contentContainer.add(new Label(review.getRating().toString()));
                    contentContainer.add(new Label("Достоинства: "));
                    contentContainer.add(new Label(review.getPros()));
                    contentContainer.add(new Label("Недостатки: "));
                    contentContainer.add(new Label(review.getCons()));
                    contentContainer.add(new Label("Комментарии: "));
                    contentContainer.add(new Label(review.getComment()));
                    contentContainer.add(new Label(""));
                    contentContainer.add(new Label(""));
                });
            } else {
                contentContainer.add(new Label("Отзывы не найдены"));
            }
        });
    }

    private static VerticalLayout displayPreviewImages(Map<Long, Image> previewImages) {
        VerticalLayout verticalLayout = new VerticalLayout();
        for (Map.Entry<Long, Image> entry : previewImages.entrySet()) {
            verticalLayout.add(entry.getValue());
        }
        return verticalLayout;
    }

    public static class ProductPage extends Composite<VerticalLayout> {
        public ProductPage(ProductDto productDto, Long reviewAmount, Map<Long, Image> fullSizedImagesMap, Map<Long, Image> previewImagesMap) {
            VerticalLayout topPart = new VerticalLayout();
            Label productName = new Label(productDto.getName());
            Label productPrice = new Label(productDto.getPrice().toString() + " руб.");
            Label productDescription = new Label(productDto.getDescription());
            Label productReviewsAmount = new Label(reviewAmount + " отзывов");
            Button addToListButton = new Button("Добавить в корзину");
            HorizontalLayout rating = evaluateRating(productDto);
            VerticalLayout previewImages = displayPreviewImages(previewImagesMap);
            rating.add(productReviewsAmount);

            productName.getElement().getStyle().set("cursor", "text");
            productName.getElement().getStyle().set("font-weight", "bold");
            productName.getElement().getStyle().set("font-size", "24px");

            topPart.add(productName);
            topPart.add(rating);
            topPart.add(new Hr());

            HorizontalLayout bottomPart = new HorizontalLayout();
            VerticalLayout price = new VerticalLayout();
            price.setJustifyContentMode(JustifyContentMode.AROUND);
            addToListButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            price.add(productPrice);
            price.add(addToListButton);


            Scroller scroller = new Scroller(productDescription);
            scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
            scroller.setHeight("400px");
            scroller.getStyle()
                    .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
                    .set("padding", "var(--lumo-space-m)");

            bottomPart.setAlignItems(Alignment.STRETCH);
            bottomPart.add(previewImages);
            bottomPart.add(fullSizedImagesMap.get(1L));
            productDescription.setHeight("400px");
            productDescription.scrollIntoView();
            bottomPart.add(scroller);
            bottomPart.add(price);

            getContent().setPadding(true);
            getContent().setAlignItems(Alignment.STRETCH);
            getContent().add(topPart);
            getContent().add(bottomPart);
            getContent().add(new Hr());

            getReviews(productDto.getId());
        }
    }
}
