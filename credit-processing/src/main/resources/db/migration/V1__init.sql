CREATE TABLE product_registry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    client_id BIGINT,
    account_id BIGINT,
    product_id BIGINT,
    interest_rate DOUBLE,
    open_date TIMESTAMP,
    month_count BIGINT
);

CREATE TABLE payment_registry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_registry_id BIGINT,
    payment_date TIMESTAMP,
    amount DOUBLE,
    interest_rate_amount DOUBLE,
    debt_amount DOUBLE,
    expired BOOLEAN,
    payment_expiration_date TIMESTAMP,
    CONSTRAINT fk_product_registry FOREIGN KEY (product_registry_id) REFERENCES product_registry(id)
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


