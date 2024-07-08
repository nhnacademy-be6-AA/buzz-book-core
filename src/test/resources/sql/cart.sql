CREATE TABLE IF NOT EXISTS cart (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    user_id BIGINT NOT NULL UNIQUE,
                                    `uuid` BINARY(16) NOT NULL UNIQUE
    );