BEGIN TRANSACTION;

DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS subscriptions CASCADE;

DROP SEQUENCE IF EXISTS "products_id_seq" CASCADE;
DROP SEQUENCE IF EXISTS "users_id_seq" CASCADE;
DROP SEQUENCE IF EXISTS "subscriptions_id_seq" CASCADE;

/* ------------------------------------------- */

CREATE SEQUENCE "products_id_seq";
create table if not exists products
(
  id           bigint primary key  DEFAULT "nextval"('products_id_seq'),
  available    boolean,
  created_date timestamp           default now(),
  description  varchar(255),
  fetch_date   timestamp,
  parameters   varchar(255),
  price        numeric(19, 2),
  product_type integer,
  shop_type    integer,
  title        varchar(255)
);

CREATE SEQUENCE "users_id_seq";
create table if not exists users
(
  id           bigint primary key  DEFAULT "nextval"('users_id_seq'),
  chat_id      varchar(255),
  created_date timestamp           default now()
);

CREATE SEQUENCE "subscriptions_id_seq";
create table if not exists subscriptions
(
  id           bigint primary key  DEFAULT "nextval"('subscriptions_id_seq'),
  product_type integer,
  shop_type    integer,
  user_id      bigint not null
    constraint fkhro52ohfqfbay9774bev0qinr
    references users
);

END TRANSACTION;