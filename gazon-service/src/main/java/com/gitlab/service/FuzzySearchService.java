package com.gitlab.service;

import com.gitlab.exception.handler.EmptySearchStringException;
import com.gitlab.model.Product;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
/**
 * Сервис для выполнения нечеткого (fuzzy) поиска продуктов в базе данных с использованием Hibernate Search.
 * Нечеткий поиск позволяет находить продукты, учитывая возможные опечатки или вариации написания названия.
 */
@Service
@RequiredArgsConstructor
public class FuzzySearchService {

    private final EntityManager entityManager;

    /**
     * Создает FullTextQuery для выполнения нечеткого поиска продуктов по названию.
     *
     * @param name Введенное название продукта в поиске, по которому будет выполняться нечеткий поиск.
     * @return FullTextQuery, представляющий запрос на нечеткий поиск.
     */
    public FullTextQuery getFullTextQuery(String name) throws InterruptedException {
        if (StringUtils.isEmpty(name)) throw new EmptySearchStringException();
        // Получаем FullTextEntityManager для выполнения поиска.
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        fullTextEntityManager.createIndexer().startAndWait();

        // Создаем QueryBuilder для создания запросов.
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Product.class)
                .get();

        // Разбиваем входное название продукта на ключевые слова.
        String[] keywords = name.split("\\s+");

        // Создаем список запросов для каждого ключевого слова.
        List<Query> queryList = new ArrayList<>();
        for (String keyword : keywords) {
            Query query = queryBuilder.keyword()
                    .fuzzy() // Используем нечеткий поиск
                    .withEditDistanceUpTo(2) // Разрешаем до 2 опечаток
                    .onField("name") // Производим поиск по полю "name" продукта
                    .matching(keyword) // Устанавливаем ключевое слово для поиска
                    .createQuery();
            queryList.add(query);
        }

        // Создаем логическое соединение запросов.
        BooleanJunction<BooleanJunction> finalQuery = queryBuilder.bool();
        for (Query query : queryList) {
            finalQuery.must(query);
        }

        // Возвращаем FullTextQuery, который объединяет несколько запросов по ключевым словам.
        return fullTextEntityManager.createFullTextQuery(finalQuery.createQuery(), Product.class);
    }
}
