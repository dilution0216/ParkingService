CREATE TABLE parking_record (
                                id BIGSERIAL PRIMARY KEY,
                                vehicle_number VARCHAR(20) NOT NULL,
                                entry_time TIMESTAMP NOT NULL,
                                exit_time TIMESTAMP,
                                fee INT,
                                subscription_id BIGINT,
                                FOREIGN KEY (subscription_id) REFERENCES subscription(id)
);

CREATE TABLE payment (
                         id BIGSERIAL PRIMARY KEY,
                         vehicle_number VARCHAR(20) NOT NULL,
                         amount INT NOT NULL,
                         discount_details VARCHAR(255),
                         timestamp TIMESTAMP NOT NULL
);

CREATE TABLE subscription (
                              id BIGSERIAL PRIMARY KEY,
                              vehicle_number VARCHAR(20) NOT NULL UNIQUE,
                              start_date DATE NOT NULL,
                              end_date DATE NOT NULL
);

CREATE TABLE discount_coupon (
                                 id BIGSERIAL PRIMARY KEY,
                                 coupon_code VARCHAR(50) UNIQUE NOT NULL,
                                 discount_rate INT NOT NULL
);

CREATE TABLE pricing_policy (
                                id BIGSERIAL PRIMARY KEY,
                                base_fee INT NOT NULL,
                                extra_fee_per_10min INT NOT NULL,
                                daily_max_fee INT NOT NULL,
                                max_days_charged INT NOT NULL,
                                night_discount DOUBLE PRECISION NOT NULL,
                                weekend_discount DOUBLE PRECISION NOT NULL,
                                max_coupon_uses INT NOT NULL,
                                max_discount_rate INT NOT NULL
);

ALTER TABLE payment ADD CONSTRAINT fk_payment_parking_record FOREIGN KEY (vehicle_number) REFERENCES parking_record(vehicle_number);
ALTER TABLE parking_record ADD CONSTRAINT fk_parking_subscription FOREIGN KEY (subscription_id) REFERENCES subscription(id);
ALTER TABLE payment ADD CONSTRAINT fk_payment_discount FOREIGN KEY (discount_details) REFERENCES discount_coupon(coupon_code);