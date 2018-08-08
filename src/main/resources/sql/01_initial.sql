BEGIN TRANSACTION;

DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS assortment CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS subscriptions CASCADE;

DROP SEQUENCE IF EXISTS "product_id_seq" CASCADE;
DROP SEQUENCE IF EXISTS "assortment_id_seq" CASCADE;
DROP SEQUENCE IF EXISTS "users_id_seq" CASCADE;
DROP SEQUENCE IF EXISTS "subscriptions_id_seq" CASCADE;

/* ------------------------------------------- */

CREATE SEQUENCE "assortment_id_seq";
create table if not exists assortment
(
  id           bigint primary key  DEFAULT "nextval"('assortment_id_seq'),
  created_date timestamp           default now(),
  fetch_date   timestamp,
  shop_type    text
);

CREATE SEQUENCE "product_id_seq";
create table if not exists product
(
  id            bigint primary key  DEFAULT "nextval"('product_id_seq'),
  title         varchar(255),
  description   varchar(255),
  available     boolean,
  price         numeric(19, 2),
  product_type  text,
  parameters    json,
  assortment_id bigint not null
    constraint fkgro52whfqfbay9774bev0qinr
    references assortment
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
  product_type text,
  shop_type    text,
  user_id      bigint not null
    constraint fkhro52ohfqfbay9774bev0qinr
    references users
);

END TRANSACTION;