BEGIN TRANSACTION;

DROP TABLE IF EXISTS shops CASCADE;
DROP TABLE IF EXISTS product_types CASCADE;
DROP TABLE IF EXISTS fetchs CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS subscriptions CASCADE;

DROP SEQUENCE IF EXISTS "shops_seq" CASCADE;
DROP SEQUENCE IF EXISTS "product_types_seq" CASCADE;
DROP SEQUENCE IF EXISTS "fetchs_seq" CASCADE;
DROP SEQUENCE IF EXISTS "products_seq" CASCADE;
DROP SEQUENCE IF EXISTS "users_seq" CASCADE;
DROP SEQUENCE IF EXISTS "subscriptions_seq" CASCADE;

/* ------------------------------------------- */

CREATE SEQUENCE "shops_seq";
CREATE TABLE shops
(
  id           BIGINT PRIMARY KEY          DEFAULT "nextval"('"shops_seq"'),
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  description  TEXT,
  title        TEXT UNIQUE NOT NULL,
  url          TEXT UNIQUE NOT NULL
);

CREATE SEQUENCE "product_types_seq";
CREATE TABLE product_types
(
  id    BIGINT PRIMARY KEY          DEFAULT "nextval"('"product_types_seq"'),
  type  TEXT NOT NULL  UNIQUE,
  title TEXT NOT NULL UNIQUE
);

CREATE SEQUENCE "fetchs_seq";
CREATE TABLE fetchs
(
  id           BIGINT PRIMARY KEY          DEFAULT "nextval"('"fetchs_seq"'),
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  fetch_date   TIMESTAMP WITH TIME ZONE,
  shop_id      BIGINT,
  CONSTRAINT fk_shop_id FOREIGN KEY (shop_id)
  REFERENCES shops (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE SEQUENCE "products_seq";
CREATE TABLE products
(
  id              BIGINT PRIMARY KEY          DEFAULT "nextval"('"products_seq"'),
  available       BOOLEAN,
  created_date    TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  description     TEXT,
  price           NUMERIC(19, 2),
  title           TEXT,
  fetch_id        BIGINT NOT NULL,
  product_type_id BIGINT,
  parameters      JSONB,
  CONSTRAINT fk_fetch_id FOREIGN KEY (fetch_id)
  REFERENCES fetchs (id) MATCH SIMPLE
  ON UPDATE NO ACTION,
  CONSTRAINT fk_product_type_id FOREIGN KEY (product_type_id)
  REFERENCES product_types (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE SEQUENCE "users_seq";
CREATE TABLE users
(
  id           BIGINT PRIMARY KEY          DEFAULT "nextval"('"users_seq"'),
  chat_id      TEXT,
  created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

CREATE SEQUENCE "subscriptions_seq";
CREATE TABLE subscriptions
(
  id              BIGINT PRIMARY KEY          DEFAULT "nextval"('"subscriptions_seq"'),
  product_type_id BIGINT,
  shop_id         BIGINT,
  user_id         BIGINT NOT NULL,
  CONSTRAINT fk_shop_id FOREIGN KEY (shop_id)
  REFERENCES shops (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_product_type_id FOREIGN KEY (product_type_id)
  REFERENCES product_types (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_user_id FOREIGN KEY (user_id)
  REFERENCES users (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE CASCADE
);

END TRANSACTION;