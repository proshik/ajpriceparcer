BEGIN TRANSACTION;

DROP TABLE IF EXISTS provier CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS assortment CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS subscriptions CASCADE;

DROP SEQUENCE IF EXISTS "provider_id_seq" CASCADE;
DROP SEQUENCE IF EXISTS "product_id_seq" CASCADE;
DROP SEQUENCE IF EXISTS "assortment_id_seq" CASCADE;
DROP SEQUENCE IF EXISTS "users_id_seq" CASCADE;
DROP SEQUENCE IF EXISTS "subscription_id_seq" CASCADE;

/* ------------------------------------------- */

CREATE SEQUENCE "provider_id_seq";
create table if not exists provider
(
  id           bigint primary key  DEFAULT "nextval"('provider_id_seq'),
  created_date timestamp           default now(),
  title        text UNIQUE     NOT NULL,
  url          text UNIQUE     NOT NULL,
  enabled      boolean         NOT NULL,
  type         text UNIQUE     NOT NULL
);

CREATE SEQUENCE "assortment_id_seq";
create table if not exists assortment
(
  id           bigint primary key  DEFAULT "nextval"('assortment_id_seq'),
  created_date timestamp           default now(),
  fetch_date   timestamp NOT NULL,
  provider_id  bigint    NOT NULL
    constraint assortment_provider_id_fk
    references provider
);

CREATE SEQUENCE "product_id_seq";
create table if not exists product
(
  id            bigint primary key  DEFAULT "nextval"('product_id_seq'),
  title         varchar(255) not NULL,
  description   varchar(255),
  available     boolean,
  price         numeric(19, 2),
  product_type  text,
  parameters    json,
  assortment_id bigint       not null
    constraint product_assortment_id_fk
    references assortment
);

CREATE SEQUENCE "users_id_seq";
create table if not exists users
(
  id           bigint primary key  DEFAULT "nextval"('users_id_seq'),
  chat_id      varchar(255) UNIQUE NOT NULL,
  created_date timestamp           default now()
);

CREATE SEQUENCE "subscription_id_seq";
create table if not exists subscriptions
(
  id           bigint primary key  DEFAULT "nextval"('subscription_id_seq'),
  product_type text,
  provider_id  bigint not null
    constraint subscription_provider_id_fk
    references provider,
  user_id      bigint not null
    constraint subscription_user_id_fk
    references users
);

END TRANSACTION;