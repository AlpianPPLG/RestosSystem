# RESTOS - Java Desktop Application Planning

## 📋 Project Overview

**Nama Aplikasi:** Restos  
**Platform:** Desktop (Java Swing/JavaFX)  
**Database:** MySQL (sama dengan web app - `restaus_db`)  
**Target:** Aplikasi desktop POS (Point of Sale) untuk restoran yang identik dengan versi web

---

## 🎯 Objectives

- Membuat aplikasi desktop Java GUI yang memiliki fitur dan tampilan sama persis dengan aplikasi web RESTAUS
- Menggunakan database MySQL yang sama (`restaus_db`)
- Multi-user dengan role-based access (Admin, Waiter, Kitchen, Cashier)
- Real-time synchronization dengan aplikasi web

---

## 🗄️ Database Schema (Existing)

Database: `restaus_db`

### Tables:

| Table         | Keterangan                                                                    |
| ------------- | ----------------------------------------------------------------------------- |
| `users`       | Data karyawan (id, username, password_hash, role, full_name)                  |
| `categories`  | Kategori menu (id, name, icon, sort_order)                                    |
| `menus`       | Daftar menu (id, category_id, name, description, price, image_url, is_active) |
| `tables`      | Data meja (id, table_number, capacity, status)                                |
| `inventories` | Stok harian menu (id, menu_id, daily_stock, remaining_stock)                  |
| `orders`      | Header transaksi (id, table_id, user_id, order_type, status, total_amount)    |
| `order_items` | Detail pesanan (id, order_id, menu_id, quantity, price_at_time, status)       |
| `payments`    | Pembayaran (id, order_id, cashier_id, payment_method, amount_paid)            |

### Enum Values:

- **UserRole:** `admin`, `waiter`, `cashier`, `kitchen`
- **TableStatus:** `available`, `reserved`, `occupied`
- **OrderType:** `dine_in`, `take_away`
- **OrderStatus:** `pending`, `processing`, `delivered`, `completed`, `cancelled`
- **OrderItemStatus:** `pending`, `cooking`, `served`
- **PaymentMethod:** `cash`, `qris`, `debit`

---

## 🏗️ Technology Stack

### Core Technologies:

| Component      | Technology                               |
| -------------- | ---------------------------------------- |
| Language       | Java 17+                                 |
| GUI Framework  | **JavaFX** (recommended) atau Java Swing |
| Database       | MySQL 8.0+                               |
| JDBC Driver    | MySQL Connector/J                        |
| Build Tool     | Maven atau Gradle                        |
| ORM (Optional) | Hibernate / Plain JDBC                   |

### Libraries (Recommended):

| Library                | Purpose                      |
| ---------------------- | ---------------------------- |
| `mysql-connector-java` | MySQL JDBC Driver            |
| `javafx-controls`      | JavaFX UI Components         |
| `javafx-fxml`          | FXML Layout Support          |
| `controlsfx`           | Additional JavaFX Controls   |
| `jbcrypt`              | Password Hashing             |
| `itext`                | PDF Receipt Generation       |
| `jasperreports`        | Report Generation (Optional) |

---

## 📁 Project Structure

```
restos-desktop/
├── pom.xml                          # Maven configuration
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── restos/
│       │           ├── App.java                 # Main Application Entry
│       │           │
│       │           ├── config/
│       │           │   └── DatabaseConfig.java  # Database Connection
│       │           │
│       │           ├── model/                   # Entity/Model Classes
│       │           │   ├── User.java
│       │           │   ├── Category.java
│       │           │   ├── Menu.java
│       │           │   ├── Table.java
│       │           │   ├── Inventory.java
│       │           │   ├── Order.java
│       │           │   ├── OrderItem.java
│       │           │   └── Payment.java
│       │           │
│       │           ├── dao/                     # Data Access Objects
│       │           │   ├── UserDAO.java
│       │           │   ├── CategoryDAO.java
│       │           │   ├── MenuDAO.java
│       │           │   ├── TableDAO.java
│       │           │   ├── InventoryDAO.java
│       │           │   ├── OrderDAO.java
│       │           │   ├── OrderItemDAO.java
│       │           │   └── PaymentDAO.java
│       │           │
│       │           ├── service/                 # Business Logic Layer
│       │           │   ├── AuthService.java
│       │           │   ├── MenuService.java
│       │           │   ├── TableService.java
│       │           │   ├── OrderService.java
│       │           │   └── PaymentService.java
│       │           │
│       │           ├── controller/              # FXML Controllers
│       │           │   ├── LoginController.java
│       │           │   ├── AdminDashboardController.java
│       │           │   ├── MenuManagementController.java
│       │           │   ├── TableManagementController.java
│       │           │   ├── UserManagementController.java
│       │           │   ├── WaiterDashboardController.java
│       │           │   ├── NewOrderController.java
│       │           │   ├── KitchenDashboardController.java
│       │           │   ├── CashierDashboardController.java
│       │           │   └── ReceiptController.java
│       │           │
│       │           ├── util/                    # Utility Classes
│       │           │   ├── PasswordUtil.java
│       │           │   ├── SessionManager.java
│       │           │   ├── AlertUtil.java
│       │           │   ├── CurrencyFormatter.java
│       │           │   └── DateTimeUtil.java
│       │           │
│       │           └── component/               # Custom UI Components
│       │               ├── TableCard.java
│       │               ├── MenuCard.java
│       │               ├── OrderCard.java
│       │               ├── CartItem.java
│       │               └── StatusBadge.java
│       │
│       └── resources/
│           ├── fxml/                            # FXML Layout Files
│           │   ├── login.fxml
│           │   ├── admin/
│           │   │   ├── dashboard.fxml
│           │   │   ├── menu-management.fxml
│           │   │   ├── table-management.fxml
│           │   │   ├── user-management.fxml
│           │   │   └── settings.fxml
│           │   ├── waiter/
│           │   │   ├── dashboard.fxml
│           │   │   └── new-order.fxml
│           │   ├── kitchen/
│           │   │   └── dashboard.fxml
│           │   ├── cashier/
│           │   │   └── dashboard.fxml
│           │   └── receipt.fxml
│           │
│           ├── css/                             # Stylesheets
│           │   ├── main.css
│           │   ├── login.css
│           │   ├── dashboard.css
│           │   └── components.css
│           │
│           ├── images/                          # Image Assets
│           │   ├── logo.png
│           │   └── icons/
│           │
│           └── config/
│               └── database.properties          # DB Configuration
```

---

## 🖥️ Module & Feature Breakdown

### 1. Authentication Module (Login)

| Feature            | Deskripsi                               |
| ------------------ | --------------------------------------- |
| Login Form         | Username & Password input               |
| Role Detection     | Redirect berdasarkan role user          |
| Session Management | Simpan user session selama app berjalan |
| Logout             | Clear session & redirect ke login       |

**UI Components:**

- Logo dan branding
- Form input username/password
- Login button dengan loading state
- Error message display

---

### 2. Admin Dashboard

| Feature            | Deskripsi                                        |
| ------------------ | ------------------------------------------------ |
| Stats Overview     | Total revenue, orders, occupied tables, warnings |
| Weekly Sales Chart | Bar chart penjualan mingguan                     |
| Recent Orders      | List order terbaru                               |
| Quick Actions      | Navigation ke sub-modules                        |

**Sub-Modules:**

- **Menu Management:** CRUD menu, toggle active, update stock
- **Table Management:** CRUD meja, update status
- **User Management:** CRUD user, assign role
- **Settings:** App configuration

---

### 3. Waiter Module

| Feature         | Deskripsi                            |
| --------------- | ------------------------------------ |
| Table Grid      | Visual grid semua meja dengan status |
| Table Selection | Click meja untuk buat order baru     |
| Stats Cards     | Total, Available, Occupied, Warnings |
| Auto-Refresh    | Polling data setiap 10 detik         |

**New Order Page:**

- Menu catalog dengan kategori tabs
- Search & filter menu
- Add to cart functionality
- Cart summary dengan quantity adjustment
- Special notes per item
- Submit order

---

### 4. Kitchen Display System (KDS)

| Feature            | Deskripsi                           |
| ------------------ | ----------------------------------- |
| Order Queue        | List semua order pending/processing |
| Order Cards        | Info meja, items, waktu order       |
| Item Status Update | Toggle: pending → cooking → served  |
| Order Completion   | Mark order sebagai delivered        |
| Auto-Refresh       | Real-time update setiap 5 detik     |

**Visual Indicators:**

- Color coding berdasarkan status
- Time elapsed indicator
- Priority ordering (oldest first)

---

### 5. Cashier/POS Module

| Feature               | Deskripsi                      |
| --------------------- | ------------------------------ |
| Pending Payments List | Order yang sudah delivered     |
| Search & Filter       | Cari berdasarkan table/name/ID |
| Order Detail          | Items, subtotal, total         |
| Payment Form          | Method selection, amount input |
| Change Calculation    | Auto-calculate kembalian       |
| Receipt Generation    | Print/save receipt             |

**Payment Methods:**

- Cash
- QRIS
- Debit

---

### 6. Receipt Preview

| Feature         | Deskripsi                  |
| --------------- | -------------------------- |
| Receipt Display | Format struk kasir         |
| Order Info      | ID, table, date, waiter    |
| Item List       | Name, qty, price, subtotal |
| Payment Info    | Method, amount, change     |
| Print Button    | Print ke thermal printer   |
| Download PDF    | Save as PDF file           |

---

## 📐 UI/UX Design Specifications

### Color Palette (dari Web App):

| Element          | Color                 |
| ---------------- | --------------------- |
| Primary (Orange) | `#F97316` → `#D97706` |
| Success (Green)  | `#22C55E`             |
| Warning (Yellow) | `#EAB308`             |
| Danger (Red)     | `#EF4444`             |
| Background       | `#FFF7ED` → `#FFFBEB` |
| Text Primary     | `#111827`             |
| Text Secondary   | `#6B7280`             |

### Typography:

- **Heading:** Bold, larger font
- **Body:** Regular weight
- **Labels:** Medium weight, smaller size

### Component Styles:

- Rounded corners (8px radius)
- Subtle shadows for cards
- Gradient backgrounds for buttons
- Smooth transitions/animations

---

## 📊 Implementation Phases

### Phase 1: Foundation (Week 1-2)

- [x] Setup project structure (Maven/Gradle + JavaFX)
- [x] Configure database connection
- [x] Create all Model classes
- [x] Implement DAO layer
- [x] Build utility classes

### Phase 2: Authentication (Week 2)

- [x] Login page UI
- [x] AuthService implementation
- [x] Session management
- [x] Role-based routing

### Phase 3: Admin Module (Week 3-4)

- [ ] Admin dashboard UI
- [ ] Menu management (CRUD)
- [ ] Table management (CRUD)
- [ ] User management (CRUD)
- [ ] Charts integration

### Phase 4: Waiter Module (Week 4-5)

- [ ] Table grid component
- [ ] New order page
- [ ] Menu catalog with categories
- [ ] Cart system
- [ ] Order submission

### Phase 5: Kitchen Module (Week 5-6)

- [ ] KDS dashboard
- [ ] Order queue display
- [ ] Status update functionality
- [ ] Auto-refresh mechanism

### Phase 6: Cashier Module (Week 6-7)

- [ ] POS dashboard
- [ ] Payment form
- [ ] Receipt preview
- [ ] PDF generation

### Phase 7: Polish & Testing (Week 7-8)

- [ ] UI/UX refinement
- [ ] Bug fixes
- [ ] Performance optimization
- [ ] User acceptance testing

---

## 🔧 Development Guidelines

### Code Standards:

```java
// Package naming: lowercase
package com.restos.model;

// Class naming: PascalCase
public class OrderItem { }

// Method naming: camelCase
public void calculateSubtotal() { }

// Constant naming: UPPER_SNAKE_CASE
public static final String DB_URL = "jdbc:mysql://localhost:3306/restaus_db";
```

### DAO Pattern Example:

```java
public interface MenuDAO {
    List<Menu> findAll();
    Menu findById(int id);
    List<Menu> findByCategory(int categoryId);
    boolean insert(Menu menu);
    boolean update(Menu menu);
    boolean delete(int id);
    boolean updateStatus(int id, boolean isActive);
}
```

### Service Layer Example:

```java
public class OrderService {
    private final OrderDAO orderDAO;
    private final OrderItemDAO itemDAO;
    private final InventoryDAO inventoryDAO;

    public Order createOrder(int tableId, int userId, List<CartItem> items) {
        // Business logic: validate stock, calculate total, etc.
    }
}
```

---

## 📝 Database Connection Config

```properties
# database.properties
db.url=jdbc:mysql://localhost:3306/restaus_db
db.username=root
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
db.pool.size=10
```

---

## 🚀 Build & Run Commands

### Maven:

```bash
# Build
mvn clean package

# Run
mvn javafx:run

# Create executable JAR
mvn clean package shade:shade
```

### Gradle:

```bash
# Build
./gradlew build

# Run
./gradlew run

# Create distribution
./gradlew jlink
```

---

## 📋 Checklist Summary

### Models (8 files)

- [ ] User.java
- [ ] Category.java
- [ ] Menu.java
- [ ] Table.java
- [ ] Inventory.java
- [ ] Order.java
- [ ] OrderItem.java
- [ ] Payment.java

### DAOs (8 files)

- [ ] UserDAO.java + UserDAOImpl.java
- [ ] CategoryDAO.java + CategoryDAOImpl.java
- [ ] MenuDAO.java + MenuDAOImpl.java
- [ ] TableDAO.java + TableDAOImpl.java
- [ ] InventoryDAO.java + InventoryDAOImpl.java
- [ ] OrderDAO.java + OrderDAOImpl.java
- [ ] OrderItemDAO.java + OrderItemDAOImpl.java
- [ ] PaymentDAO.java + PaymentDAOImpl.java

### Services (5 files)

- [ ] AuthService.java
- [ ] MenuService.java
- [ ] TableService.java
- [ ] OrderService.java
- [ ] PaymentService.java

### Controllers (10 files)

- [ ] LoginController.java
- [ ] AdminDashboardController.java
- [ ] MenuManagementController.java
- [ ] TableManagementController.java
- [ ] UserManagementController.java
- [ ] WaiterDashboardController.java
- [ ] NewOrderController.java
- [ ] KitchenDashboardController.java
- [ ] CashierDashboardController.java
- [ ] ReceiptController.java

### FXML Views (12 files)

- [ ] login.fxml
- [ ] admin/dashboard.fxml
- [ ] admin/menu-management.fxml
- [ ] admin/table-management.fxml
- [ ] admin/user-management.fxml
- [ ] admin/settings.fxml
- [ ] waiter/dashboard.fxml
- [ ] waiter/new-order.fxml
- [ ] kitchen/dashboard.fxml
- [ ] cashier/dashboard.fxml
- [ ] receipt.fxml

### Utilities (5 files)

- [x] DatabaseConfig.java
- [x] PasswordUtil.java
- [x] SessionManager.java
- [x] AlertUtil.java
- [x] CurrencyFormatter.java

---

## 📌 Notes

1. **Sinkronisasi Database:** Karena menggunakan database yang sama dengan web app, perubahan data akan langsung terlihat di kedua platform.

2. **Concurrent Access:** Perlu handle concurrent access jika web dan desktop digunakan bersamaan.

3. **Offline Mode:** (Future) Consider implementing offline-first dengan sync mechanism.

4. **Auto-update:** (Future) Implement auto-update mechanism untuk desktop app.

5. **Thermal Printer:** Integrasi dengan ESC/POS printer untuk cetak struk.

---

_Last Updated: January 17, 2026_
