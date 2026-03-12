-- Create database if not exists (application.properties handles this)
-- CREATE DATABASE IF NOT EXISTS stock_trading_db;
-- USE stock_trading_db;

-- Create stocks table (JPA will create this, but here's the SQL)
CREATE TABLE IF NOT EXISTS stocks (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      symbol VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_symbol (symbol)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create stock_orders table
CREATE TABLE IF NOT EXISTS stock_orders (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            order_id VARCHAR(50) UNIQUE NOT NULL,
    stock_symbol VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    order_type VARCHAR(10) NOT NULL,
    total_amount DOUBLE,
    status VARCHAR(20) DEFAULT 'PROCESSED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    stock_id BIGINT,
    FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE SET NULL,
    INDEX idx_order_id (order_id),
    INDEX idx_stock_symbol (stock_symbol),
    INDEX idx_created_at (created_at)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;