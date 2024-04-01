package com.gitlab.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@Route
@PageTitle("Поиск")
public class SearchBar extends VerticalLayout {
    private final TextField searchField = new TextField();
    private final Button searchButton = new Button(VaadinIcon.SEARCH.create());

    private SearchListener searchListener;

    public interface SearchListener {
        void onSearch(String query);
    }

    public SearchBar() {
        addClassName("search-view");

        HorizontalLayout searchWrapper = new HorizontalLayout();
        searchWrapper.addClassName("search-bar-wrapper");

        searchField.setPlaceholder("Искать на Gazon");
        searchField.addClassName("search-input");
        searchField.setWidthFull();
        searchField.setWidth("600px");

        searchButton.addClickListener(event -> performSearch());
        searchButton.addClickShortcut(Key.ENTER);

        HorizontalLayout searchFieldContainer = new HorizontalLayout(searchField, searchButton);
        searchFieldContainer.addClassName("search-field-container");

        searchWrapper.add(searchFieldContainer);
        add(searchWrapper);
    }

    public void setSearchListener(SearchListener listener) {
        this.searchListener = listener;
    }

    private void performSearch() {
        String strQuery = searchField.getValue();

        if (!strQuery.isEmpty()) {
            String searchResultsUrl = "search/" + strQuery;
            getUI().ifPresent(ui -> ui.navigate(searchResultsUrl));
        }

    }
}