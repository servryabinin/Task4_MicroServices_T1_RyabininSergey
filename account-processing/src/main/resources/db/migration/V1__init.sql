CREATE TABLE accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    client_id BIGINT,
    product_id BIGINT,
    balance DOUBLE,
    interest_rate DOUBLE,
    is_recalc BOOLEAN,
    card_exist BOOLEAN,
    status VARCHAR(20)
);

CREATE TABLE cards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT,
    card_id VARCHAR(50),
    payment_system VARCHAR(50),
    status VARCHAR(20),
    CONSTRAINT fk_cards_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT,
    payment_date TIMESTAMP,
    amount DOUBLE,
    is_credit BOOLEAN,
    payed_at TIMESTAMP,
    type VARCHAR(50),
    expired BOOLEAN DEFAULT FALSE,
    installment_number BIGINT,
    CONSTRAINT fk_payments_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);


CREATE TABLE transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT,
    card_id BIGINT,
    type VARCHAR(50),
    amount DOUBLE,
    status VARCHAR(20),
    timestamp TIMESTAMP,
    message_uuid VARCHAR(100),
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT fk_transactions_card FOREIGN KEY (card_id) REFERENCES cards(id),
    CONSTRAINT ux_transactions_message_uuid UNIQUE (message_uuid) -- защита от дублей
);

CREATE TABLE error_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_name VARCHAR(255),
    type VARCHAR(50),
    method_signature VARCHAR(500),
    exception_text VARCHAR(2000),
    stack_trace CLOB,
    input_params CLOB,
    timestamp TIMESTAMP
);

CREATE TABLE http_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_name VARCHAR(255),
    type VARCHAR(50), -- INFO
    method_signature VARCHAR(500),
    input_params CLOB,
    response_body CLOB,
    uri VARCHAR(1000),
    timestamp TIMESTAMP,
    direction VARCHAR(10) -- IN или OUT
);

