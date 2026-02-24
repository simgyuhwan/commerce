-- ============================================================
-- V1 : 전체 스키마 초기화
-- ============================================================

-- -----------------------------------------------------------
-- Member Context
-- -----------------------------------------------------------
CREATE TABLE member (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    email         VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    role          VARCHAR(20)  NOT NULL,  -- ADMIN / SELLER / BUYER
    status        VARCHAR(20)  NOT NULL,  -- PENDING / APPROVED / REJECTED
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_member_email (email)
);

CREATE TABLE member_document (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    member_id  BIGINT       NOT NULL,
    type       VARCHAR(30)  NOT NULL,  -- BUSINESS_LICENSE / BANK_ACCOUNT
    file_url   VARCHAR(500) NOT NULL,
    status     VARCHAR(20)  NOT NULL,  -- PENDING / APPROVED / REJECTED
    created_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    KEY ix_member_document_member_id (member_id)
);

-- -----------------------------------------------------------
-- Commerce Context
-- -----------------------------------------------------------
CREATE TABLE product (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    seller_id   BIGINT        NOT NULL,
    name        VARCHAR(200)  NOT NULL,
    description TEXT,
    price       DECIMAL(12,2) NOT NULL,
    status      VARCHAR(20)   NOT NULL,  -- ACTIVE / INACTIVE
    created_at  DATETIME(6)   NOT NULL,
    updated_at  DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    KEY ix_product_seller_id (seller_id)
);

CREATE TABLE stock (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    product_id BIGINT      NOT NULL,
    available  INT         NOT NULL DEFAULT 0,
    reserved   INT         NOT NULL DEFAULT 0,
    sold       INT         NOT NULL DEFAULT 0,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_stock_product_id (product_id)
);

CREATE TABLE product_image (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    product_id BIGINT       NOT NULL,
    image_url  VARCHAR(500) NOT NULL,
    sort_order INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY ix_product_image_product_id (product_id)
);

-- -----------------------------------------------------------
-- Broadcast Context
-- -----------------------------------------------------------
CREATE TABLE broadcast (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    seller_id   BIGINT       NOT NULL,
    title       VARCHAR(200) NOT NULL,
    status      VARCHAR(20)  NOT NULL,  -- SCHEDULED / LIVE / ENDED
    stream_key  VARCHAR(100),
    started_at  DATETIME(6),
    ended_at    DATETIME(6),
    created_at  DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    KEY ix_broadcast_seller_id (seller_id),
    KEY ix_broadcast_status (status)
);

CREATE TABLE broadcast_product (
    id           BIGINT NOT NULL AUTO_INCREMENT,
    broadcast_id BIGINT NOT NULL,
    product_id   BIGINT NOT NULL,
    sort_order   INT    NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY ix_broadcast_product_broadcast_id (broadcast_id)
);

CREATE TABLE chat_message (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    broadcast_id BIGINT       NOT NULL,
    member_id    BIGINT       NOT NULL,
    content      VARCHAR(200) NOT NULL,
    created_at   DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    KEY ix_chat_message_broadcast_created (broadcast_id, created_at)
);

-- -----------------------------------------------------------
-- Order Context
-- -----------------------------------------------------------
CREATE TABLE orders (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    buyer_id     BIGINT        NOT NULL,
    broadcast_id BIGINT        NOT NULL,
    status       VARCHAR(20)   NOT NULL,  -- PENDING / PAID / CANCELLED / EXPIRED
    total_amount DECIMAL(12,2) NOT NULL,
    expires_at   DATETIME(6)   NOT NULL,
    created_at   DATETIME(6)   NOT NULL,
    updated_at   DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    KEY ix_orders_buyer_id_created (buyer_id, created_at),
    KEY ix_orders_broadcast_id (broadcast_id),
    KEY ix_orders_status_expires (status, expires_at)
);

CREATE TABLE order_item (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    order_id       BIGINT        NOT NULL,
    product_id     BIGINT        NOT NULL,
    quantity       INT           NOT NULL,
    price_snapshot DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id),
    KEY ix_order_item_order_id (order_id)
);

-- -----------------------------------------------------------
-- Payment Context
-- -----------------------------------------------------------
CREATE TABLE payment (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    order_id          BIGINT        NOT NULL,
    pg_transaction_id VARCHAR(100),
    amount            DECIMAL(12,2) NOT NULL,
    status            VARCHAR(20)   NOT NULL,  -- REQUESTED / COMPLETED / FAILED / REFUNDED
    paid_at           DATETIME(6),
    created_at        DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_payment_order_id (order_id),
    UNIQUE KEY uk_payment_pg_transaction_id (pg_transaction_id)
);

CREATE TABLE payment_idempotency (
    idempotency_key VARCHAR(100) NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    created_at      DATETIME(6)  NOT NULL,
    PRIMARY KEY (idempotency_key)
);

-- -----------------------------------------------------------
-- Settlement Context
-- -----------------------------------------------------------
CREATE TABLE settlement (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    seller_id    BIGINT        NOT NULL,
    settled_date DATE          NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    fee_amount   DECIMAL(12,2) NOT NULL,
    net_amount   DECIMAL(12,2) NOT NULL,
    status       VARCHAR(20)   NOT NULL,  -- CALCULATED / CONFIRMED
    created_at   DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_settlement_seller_date (seller_id, settled_date)
);

CREATE TABLE settlement_item (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    settlement_id BIGINT        NOT NULL,
    order_id      BIGINT        NOT NULL,
    amount        DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id),
    KEY ix_settlement_item_settlement_id (settlement_id)
);

CREATE TABLE withdrawal (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    seller_id    BIGINT        NOT NULL,
    amount       DECIMAL(12,2) NOT NULL,
    status       VARCHAR(20)   NOT NULL,  -- REQUESTED / COMPLETED / REJECTED
    requested_at DATETIME(6)   NOT NULL,
    completed_at DATETIME(6),
    PRIMARY KEY (id),
    KEY ix_withdrawal_seller_id (seller_id)
);

-- -----------------------------------------------------------
-- Common
-- -----------------------------------------------------------
CREATE TABLE outbox (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    aggregate_type VARCHAR(50)  NOT NULL,
    aggregate_id   VARCHAR(50)  NOT NULL,
    event_type     VARCHAR(100) NOT NULL,
    payload        JSON         NOT NULL,
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',  -- PENDING / PUBLISHED
    created_at     DATETIME(6)  NOT NULL,
    published_at   DATETIME(6),
    PRIMARY KEY (id),
    KEY ix_outbox_status_created (status, created_at)
);
