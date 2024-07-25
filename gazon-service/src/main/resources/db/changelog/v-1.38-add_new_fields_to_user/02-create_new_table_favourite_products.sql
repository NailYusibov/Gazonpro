create table users_favourite_products
(
    user_id    bigint not null
        constraint fkq9118ymaj5saginr2gah1ukj2
            references users,
    product_id bigint not null
        constraint uk_qej44lljvi9weo4rqlqt07nk6
            unique
        constraint fk6k7jf0uaxtiq02lg8qqcs9gjg
            references product,
    primary key (user_id, product_id)
);

