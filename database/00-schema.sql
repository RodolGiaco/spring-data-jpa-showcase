-- =============================================================================
-- Schema: tables mapped by the JPA entities in
-- src/main/java/com/rodo_pizzeria/persistence/entity
--
-- Equivalent to what Hibernate generates with spring.jpa.hibernate.ddl-auto=update.
-- Usage: mysql -u pizzeria_user -p pizzeria < database/00-schema.sql
-- =============================================================================

CREATE TABLE IF NOT EXISTS `customer` (
  `id_customer`  VARCHAR(15)  NOT NULL,
  `address`      VARCHAR(100) DEFAULT NULL,
  `email`        VARCHAR(50)  NOT NULL,
  `name`         VARCHAR(60)  NOT NULL,
  `phone_number` VARCHAR(20)  DEFAULT NULL,
  PRIMARY KEY (`id_customer`),
  UNIQUE KEY `uk_customer_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `pizza` (
  `id_pizza`      INT          NOT NULL AUTO_INCREMENT,
  `available`     TINYINT      NOT NULL,
  `description`   VARCHAR(150) NOT NULL,
  `name`          VARCHAR(30)  NOT NULL,
  `price`         DECIMAL(5,2) NOT NULL,
  `vegan`         TINYINT      DEFAULT NULL,
  `vegetarian`    TINYINT      DEFAULT NULL,
  `created_date`  DATETIME(6)  DEFAULT NULL,
  `modified_date` DATETIME(6)  DEFAULT NULL,
  PRIMARY KEY (`id_pizza`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `pizza_order` (
  `id_order`         INT          NOT NULL AUTO_INCREMENT,
  `additional_notes` VARCHAR(200) DEFAULT NULL,
  `date`             DATETIME     NOT NULL,
  `id_customer`      VARCHAR(15)  NOT NULL,
  `method`           CHAR(1)      NOT NULL COMMENT 'D = delivery, C = carryout, S = on site',
  `total`            DECIMAL(5,2) NOT NULL,
  PRIMARY KEY (`id_order`),
  KEY `fk_order_customer` (`id_customer`),
  CONSTRAINT `fk_order_customer` FOREIGN KEY (`id_customer`) REFERENCES `customer` (`id_customer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `order_item` (
  `id_item`  INT          NOT NULL,
  `id_order` INT          NOT NULL,
  `id_pizza` INT          NOT NULL,
  `price`    DECIMAL(5,2) NOT NULL,
  `quantity` DECIMAL(2,1) NOT NULL,
  PRIMARY KEY (`id_item`, `id_order`),
  KEY `fk_item_order` (`id_order`),
  KEY `fk_item_pizza` (`id_pizza`),
  CONSTRAINT `fk_item_order` FOREIGN KEY (`id_order`) REFERENCES `pizza_order` (`id_order`),
  CONSTRAINT `fk_item_pizza` FOREIGN KEY (`id_pizza`) REFERENCES `pizza` (`id_pizza`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
