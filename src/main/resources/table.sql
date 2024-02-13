CREATE TABLE MEMBER (
    INDEX BIGINT AUTO_INCREMENT,
    USER_ID VARCHAR(255) UNIQUE NOT NULL,
    PASSWORD VARCHAR(255) NOT NULL,
    NAME VARCHAR(255) NOT NULL,
    ID_TYPE VARCHAR(255) NOT NULL,
    ID_VALUE VARCHAR(255) NOT NULL
);

CREATE TABLE remittance_quote (
    quote_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_amount BIGINT NOT NULL,
    fee DOUBLE NOT NULL,
    usd_exchange_rate DOUBLE NOT NULL,
    usd_amount DOUBLE NOT NULL,
    target_currency VARCHAR(255) NOT NULL,
    exchange_rate DOUBLE NOT NULL,
    target_amount DOUBLE NOT NULL,
    expire_time TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE remittance_log (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_amount BIGINT NOT NULL,
    fee double NOT NULL,
    usd_exchange_rate DOUBLE NOT NULL,
    usd_amount DOUBLE NOT NULL,
    target_currency VARCHAR(255) NOT NULL,
    exchange_rate DOUBLE NOT NULL,
    target_amount DOUBLE NOT NULL,
    requested_date TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES member(user_id)
);


select * from member;

select * from remittance_quote;

select * from remittance_log;

DROP ALL OBJECTS;

