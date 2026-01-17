-- Database Schema for RESTAUS (Restaurant POS System)
-- DBMS: MySQL
-- Date: 2026-01-08

CREATE DATABASE IF NOT EXISTS restaus_db;
USE restaus_db;

-- 1. Table: Users (Mencatat data karyawan)
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('admin', 'waiter', 'cashier', 'kitchen') NOT NULL,
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Table: Categories (Kategori Menu)
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL, -- e.g. 'Makanan Berat', 'Minuman', 'Dessert'
    icon VARCHAR(50), -- nama icon atau url
    sort_order INT DEFAULT 0
);

-- 3. Table: Menus (Daftar Menu Makanan/Minuman)
CREATE TABLE menus (
    id INT PRIMARY KEY AUTO_INCREMENT,
    category_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
);

-- 4. Table: Tables (Data Meja Fisik)
CREATE TABLE tables (
    id INT PRIMARY KEY AUTO_INCREMENT,
    table_number VARCHAR(10) NOT NULL UNIQUE,
    capacity INT NOT NULL,
    status ENUM('available', 'reserved', 'occupied') DEFAULT 'available',
    -- location_x / location_y bisa ditambahkan jika ada denah visual koordinat
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Table: Inventories (Stok Harian Menu)
-- Reset setiap hari atau di-manage manual
CREATE TABLE inventories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    menu_id INT NOT NULL,
    daily_stock INT DEFAULT 0, -- Stok awal hari ini
    remaining_stock INT DEFAULT 0, -- Stok tersisa saat ini
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE,
    UNIQUE KEY unique_menu_inventory (menu_id) -- Satu menu satu record inventory
);

-- 6. Table: Orders (Transaksi/Head Order)
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    table_id INT, -- Bisa NULL jika Take Away murni tanpa meja (opsional)
    user_id INT NOT NULL, -- Waiter yang membuat pesanan
    customer_name VARCHAR(50), -- Opsional
    order_type ENUM('dine_in', 'take_away') NOT NULL DEFAULT 'dine_in',
    status ENUM('pending', 'processing', 'delivered', 'completed', 'cancelled') DEFAULT 'pending',
    total_amount DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (table_id) REFERENCES tables(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 7. Table: Order Items (Detail Pesanan)
CREATE TABLE order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    menu_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price_at_time DECIMAL(10, 2) NOT NULL, -- Harga saat dipesan (snapshot)
    subtotal DECIMAL(10, 2) GENERATED ALWAYS AS (quantity * price_at_time) STORED,
    special_notes VARCHAR(255), -- "Tanpa pedas", dll
    status ENUM('pending', 'cooking', 'served') DEFAULT 'pending', -- Status per item
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menus(id)
);

-- 8. Table: Payments (Pembayaran)
CREATE TABLE payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL UNIQUE, -- Satu order satu pembayaran (asumsi simple)
    cashier_id INT NOT NULL, -- User yang memproses bayar
    payment_method ENUM('cash', 'qris', 'debit') DEFAULT 'cash',
    amount_paid DECIMAL(10, 2) NOT NULL,
    change_amount DECIMAL(10, 2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (cashier_id) REFERENCES users(id)
);

-- Indexes for Optimization
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created ON orders(created_at);
CREATE INDEX idx_tables_status ON tables(status);

-- Sample Seeding Data (Optional)
INSERT INTO categories (name, sort_order) VALUES ('Makanan Berat', 1), ('Minuman', 2), ('Dessert', 3);
INSERT INTO tables (table_number, capacity) VALUES ('T01', 4), ('T02', 2), ('T03', 6);
