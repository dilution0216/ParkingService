CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       role VARCHAR(20) NOT NULL CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'))
);

CREATE TABLE parking_record (
                                id BIGSERIAL PRIMARY KEY,
                                vehicle_number VARCHAR(20) NOT NULL,
                                entry_time TIMESTAMP NOT NULL,
                                exit_time TIMESTAMP,
                                fee INT,
                                subscription_id BIGINT,
                                CONSTRAINT fk_parking_subscription FOREIGN KEY (subscription_id) REFERENCES subscription (id) ON DELETE SET NULL
);

CREATE TABLE subscription (
                              id BIGSERIAL PRIMARY KEY,
                              vehicle_number VARCHAR(20) NOT NULL UNIQUE,
                              start_date DATE NOT NULL,
                              end_date DATE NOT NULL,
                              user_id BIGINT NOT NULL UNIQUE,
                              CONSTRAINT fk_subscription_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE payment (
                         id BIGSERIAL PRIMARY KEY,
                         vehicle_number VARCHAR(20) NOT NULL,
                         amount INT NOT NULL,
                         discount_details VARCHAR(255),
                         timestamp TIMESTAMP NOT NULL,
                         CONSTRAINT fk_payment_vehicle FOREIGN KEY (vehicle_number) REFERENCES parking_record (vehicle_number) ON DELETE CASCADE
);

CREATE TABLE discount_coupon (
                                 id BIGSERIAL PRIMARY KEY,
                                 coupon_code VARCHAR(50) NOT NULL UNIQUE,
                                 discount_rate INT NOT NULL CHECK (discount_rate BETWEEN 1 AND 100)
);

CREATE TABLE pricing_policy (
                                id BIGSERIAL PRIMARY KEY,
                                base_fee INT NOT NULL,
                                extra_fee_per_10min INT NOT NULL,
                                daily_max_fee INT NOT NULL,
                                max_days_charged INT NOT NULL,
                                night_discount DOUBLE PRECISION NOT NULL CHECK (night_discount BETWEEN 0 AND 1),
                                weekend_discount DOUBLE PRECISION NOT NULL CHECK (weekend_discount BETWEEN 0 AND 1),
                                max_coupon_uses INT NOT NULL CHECK (max_coupon_uses >= 0),
                                max_discount_rate INT NOT NULL CHECK (max_discount_rate BETWEEN 0 AND 100)
);

CREATE TABLE api_logs (
                          id VARCHAR(255) PRIMARY KEY,
                          method VARCHAR(10) NOT NULL, -- GET, POST, PUT, DELETE
                          uri VARCHAR(255) NOT NULL,
                          headers TEXT,
                          request_body TEXT,
                          status_code INT NOT NULL,
                          response_body TEXT,
                          elapsed_time INT NOT NULL,
                          timestamp TIMESTAMP NOT NULL
);

CREATE INDEX idx_parking_vehicle ON parking_record (vehicle_number);
CREATE INDEX idx_payment_vehicle ON payment (vehicle_number);
CREATE INDEX idx_subscription_user ON subscription (user_id);
CREATE INDEX idx_api_logs_timestamp ON api_logs (timestamp);