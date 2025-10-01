
CREATE TABLE users (
                       id BIGINT PRIMARY KEY,
                       login VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255)
);


CREATE TABLE clients (
                         id BIGINT PRIMARY KEY,
                         client_id VARCHAR(20) NOT NULL UNIQUE,
                         user_id BIGINT,
                         first_name VARCHAR(100),
                         middle_name VARCHAR(100),
                         last_name VARCHAR(100),
                         date_of_birth DATE,
                         document_type VARCHAR(50),
                         document_id VARCHAR(100),
                         document_prefix VARCHAR(20),
                         document_suffix VARCHAR(20),
                         CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE products (
                          id BIGINT PRIMARY KEY,
                          name VARCHAR(100),
                          product_key VARCHAR(10),
                          create_date TIMESTAMP,
                          product_id VARCHAR(50) UNIQUE
);

CREATE TABLE client_products (
                                 id BIGINT PRIMARY KEY,
                                 client_id BIGINT,
                                 product_id BIGINT,
                                 open_date TIMESTAMP,
                                 close_date TIMESTAMP,
                                 status VARCHAR(20),
                                 CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES clients(id),
                                 CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE black_list (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE client_cards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    client_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    card_number VARCHAR(20) NOT NULL UNIQUE,
    issue_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_client_card_client FOREIGN KEY (client_id) REFERENCES clients(id)
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

