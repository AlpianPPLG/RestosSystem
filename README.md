# 🍽️ RESTOS - Restaurant Point of Sale System

![Java](https://img.shields.io/badge/Java-17+-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21.0.1-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

**Restos** adalah aplikasi Point of Sale (POS) desktop untuk restoran yang dibangun dengan Java dan JavaFX. Aplikasi ini dirancang untuk memudahkan pengelolaan operasional restoran sehari-hari.

---

## 📋 Daftar Isi

- [Fitur](#-fitur)
- [Screenshot](#-screenshot)
- [Teknologi](#-teknologi)
- [Persyaratan Sistem](#-persyaratan-sistem)
- [Instalasi](#-instalasi)
- [Konfigurasi Database](#-konfigurasi-database)
- [Menjalankan Aplikasi](#-menjalankan-aplikasi)
- [Panduan Penggunaan](#-panduan-penggunaan)
- [Struktur Proyek](#-struktur-proyek)
- [Akun Default](#-akun-default)
- [Keyboard Shortcuts](#-keyboard-shortcuts)
- [Kontribusi](#-kontribusi)

---

## ✨ Fitur

### 🔐 Autentikasi

- Login dengan role-based access
- Registrasi akun baru
- Session management

### 👨‍💼 Admin Dashboard

- Statistik penjualan real-time
- Grafik pendapatan mingguan
- Manajemen menu (CRUD)
- Manajemen meja restoran
- Manajemen pengguna
- Manajemen inventaris/stok harian

### 🍽️ Waiter Module

- Grid meja dengan status visual (Tersedia/Terisi/Dipesan)
- Pembuatan pesanan baru
- Katalog menu dengan pencarian
- Keranjang belanja dengan modifikasi
- Riwayat pesanan

### 👨‍🍳 Kitchen Display System (KDS)

- Antrian pesanan real-time
- Filter berdasarkan status (Pending/Cooking/Served)
- Update status item per item
- Timer waktu tunggu
- Notifikasi pesanan baru

### 💰 Cashier POS

- Daftar pesanan siap bayar
- Proses pembayaran (Cash/Card/E-Wallet)
- Kalkulasi kembalian otomatis
- Preview struk pembayaran
- Cetak/Export struk ke PDF
- Statistik penjualan harian

### 🎨 UI/UX

- Desain modern dengan tema orange (#F97316)
- Responsive layout
- Toast notifications
- Loading states
- Animasi interaktif
- Keyboard shortcuts

---

## 📸 Screenshot

| Login                                | Admin Dashboard                      |
| ------------------------------------ | ------------------------------------ |
| ![Login](docs/screenshots/login.png) | ![Admin](docs/screenshots/admin.png) |

| Waiter - Table Grid                    | Kitchen Display                          |
| -------------------------------------- | ---------------------------------------- |
| ![Waiter](docs/screenshots/waiter.png) | ![Kitchen](docs/screenshots/kitchen.png) |

| Cashier POS                              |
| ---------------------------------------- |
| ![Cashier](docs/screenshots/cashier.png) |

---

## 🛠️ Teknologi

| Komponen         | Teknologi                |
| ---------------- | ------------------------ |
| Bahasa           | Java 17+                 |
| GUI Framework    | JavaFX 21.0.1            |
| Build Tool       | Maven 3.9+               |
| Database         | MySQL 8.0+               |
| JDBC Driver      | mysql-connector-j 8.0.33 |
| PDF Generation   | iText 5.5.13.3           |
| Password Hashing | BCrypt (jbcrypt 0.4)     |

---

## 💻 Persyaratan Sistem

- **OS:** Windows 10/11, macOS, atau Linux
- **Java:** JDK 17 atau lebih tinggi
- **RAM:** Minimum 4GB
- **Storage:** 500MB ruang kosong
- **Database:** MySQL Server 8.0+

---

## 📥 Instalasi

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/restos.git
cd restos
```

### 2. Install Dependencies

Pastikan Maven sudah terinstall, atau gunakan Maven Wrapper:

```bash
# Menggunakan Maven Wrapper (recommended)
./mvnw clean install

# Atau menggunakan Maven global
mvn clean install
```

---

## 🗄️ Konfigurasi Database

### 1. Buat Database

```sql
CREATE DATABASE restaus_db;
USE restaus_db;
```

### 2. Import Schema

Jalankan script SQL yang ada di folder `sql/`:

```bash
mysql -u root -p restaus_db < sql/query.sql
```

Atau import manual melalui phpMyAdmin.

### 3. Konfigurasi Koneksi

Edit file `src/main/resources/config/database.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/restaus_db
db.username=root
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
```

---

## 🚀 Menjalankan Aplikasi

### Menggunakan Maven Wrapper

```bash
# Windows
.\mvnw.cmd clean javafx:run

# Linux/macOS
./mvnw clean javafx:run
```

### Menggunakan Maven Global

```bash
mvn clean javafx:run
```

### Menggunakan IDE

1. Import project sebagai Maven project
2. Jalankan class `com.restos.App`

---

## 📖 Panduan Penggunaan

### Login

1. Buka aplikasi
2. Masukkan username dan password
3. Klik tombol "Masuk" atau tekan Enter
4. Sistem akan mengarahkan ke dashboard sesuai role

### Registrasi Akun Baru

1. Di halaman login, klik "Daftar di sini"
2. Isi form registrasi:
   - Nama Lengkap (wajib)
   - Username (wajib, min 4 karakter)
   - Email (opsional)
   - No. Telepon (opsional)
   - Role/Posisi (Waiter/Kitchen/Cashier)
   - Password (wajib, min 6 karakter)
   - Konfirmasi Password
3. Klik "Daftar"
4. Setelah berhasil, akan redirect ke halaman login

### Admin - Manajemen Menu

1. Login sebagai Admin
2. Klik menu "Menu" di sidebar
3. Untuk menambah menu: klik tombol "Tambah Menu"
4. Isi nama, kategori, harga, dan deskripsi
5. Klik "Simpan"

### Waiter - Membuat Pesanan

1. Login sebagai Waiter
2. Pilih meja yang tersedia (hijau)
3. Cari dan pilih menu dari katalog
4. Atur jumlah dan tambahkan ke keranjang
5. Review pesanan dan klik "Kirim Pesanan"

### Kitchen - Memproses Pesanan

1. Login sebagai Kitchen
2. Lihat antrian pesanan yang masuk
3. Klik item untuk mengubah status:
   - Pending → Cooking (mulai memasak)
   - Cooking → Served (selesai dimasak)
4. Pesanan akan otomatis refresh

### Cashier - Proses Pembayaran

1. Login sebagai Cashier
2. Pilih pesanan yang sudah "Delivered"
3. Pilih metode pembayaran
4. Masukkan jumlah yang dibayar
5. Klik "Proses Pembayaran"
6. Cetak atau simpan struk

---

## 📁 Struktur Proyek

```
Restos/
├── src/
│   └── main/
│       ├── java/com/restos/
│       │   ├── App.java                 # Main entry point
│       │   ├── config/
│       │   │   └── DatabaseConfig.java  # Database configuration
│       │   ├── controller/              # FXML Controllers
│       │   │   ├── AdminDashboardController.java
│       │   │   ├── CashierDashboardController.java
│       │   │   ├── KitchenDashboardController.java
│       │   │   ├── LoginController.java
│       │   │   ├── RegisterController.java
│       │   │   ├── WaiterDashboardController.java
│       │   │   └── ...
│       │   ├── dao/                     # Data Access Objects
│       │   │   ├── MenuDAO.java
│       │   │   ├── OrderDAO.java
│       │   │   ├── TableDAO.java
│       │   │   ├── UserDAO.java
│       │   │   └── ...Impl.java
│       │   ├── model/                   # Entity models
│       │   │   ├── Menu.java
│       │   │   ├── Order.java
│       │   │   ├── OrderItem.java
│       │   │   ├── Table.java
│       │   │   ├── User.java
│       │   │   └── ...
│       │   ├── service/                 # Business logic
│       │   │   └── AuthService.java
│       │   └── util/                    # Utilities
│       │       ├── AlertUtil.java
│       │       ├── CurrencyFormatter.java
│       │       ├── DateTimeUtil.java
│       │       ├── PasswordUtil.java
│       │       ├── SessionManager.java
│       │       ├── UIFeedback.java
│       │       └── ValidationUtil.java
│       └── resources/
│           ├── config/
│           │   └── database.properties
│           ├── css/
│           │   ├── main.css
│           │   ├── components.css
│           │   ├── login.css
│           │   ├── dashboard.css
│           │   ├── waiter.css
│           │   ├── kitchen.css
│           │   ├── cashier.css
│           │   └── management.css
│           └── fxml/
│               ├── login.fxml
│               ├── register.fxml
│               ├── admin/
│               ├── waiter/
│               ├── kitchen/
│               └── cashier/
├── sql/
│   └── query.sql                        # Database schema
├── plan/
│   └── planning.md                      # Development planning
├── pom.xml                              # Maven configuration
└── README.md                            # This file
```

---

## 👤 Akun Default

| Username | Password   | Role    |
| -------- | ---------- | ------- |
| admin    | admin123   | Admin   |
| waiter   | waiter123  | Waiter  |
| kitchen  | kitchen123 | Kitchen |
| cashier  | cashier123 | Cashier |

> **Note:** Password di-hash menggunakan SHA-256 sebelum disimpan di database untuk keamanan.

---

## ⌨️ Keyboard Shortcuts

| Shortcut | Fungsi                |
| -------- | --------------------- |
| `Enter`  | Submit form / Login   |
| `F5`     | Refresh data          |
| `F11`    | Toggle fullscreen     |
| `Ctrl+Q` | Keluar aplikasi       |
| `Escape` | Tutup dialog / Cancel |

---

## 🗃️ Database Schema

### Tabel Utama

| Tabel             | Deskripsi                                                        |
| ----------------- | ---------------------------------------------------------------- |
| `users`           | Data karyawan (id, username, password_hash, role, full_name)     |
| `tables`          | Data meja (id, table_number, capacity, status)                   |
| `categories`      | Kategori menu (id, name, description)                            |
| `menus`           | Data menu (id, name, category_id, price, description, available) |
| `orders`          | Data pesanan (id, table_id, user_id, status, total_amount)       |
| `order_items`     | Item pesanan (id, order_id, menu_id, quantity, price, status)    |
| `payments`        | Data pembayaran (id, order_id, amount, payment_method, paid_at)  |
| `daily_inventory` | Stok harian (id, menu_id, available_stock, date)                 |

### Status Order

| Status       | Deskripsi                    |
| ------------ | ---------------------------- |
| `pending`    | Pesanan baru, belum diproses |
| `processing` | Sedang diproses di dapur     |
| `delivered`  | Sudah diantar ke meja        |
| `completed`  | Sudah dibayar                |
| `cancelled`  | Pesanan dibatalkan           |

### Status Meja

| Status      | Deskripsi     |
| ----------- | ------------- |
| `available` | Meja tersedia |
| `occupied`  | Meja terisi   |
| `reserved`  | Meja dipesan  |

---

## 🎨 Design System

### Color Palette

| Warna            | Hex       | Penggunaan          |
| ---------------- | --------- | ------------------- |
| Primary Orange   | `#F97316` | Tombol utama, aksen |
| Secondary Orange | `#D97706` | Hover state         |
| Success Green    | `#22C55E` | Status sukses       |
| Warning Yellow   | `#EAB308` | Status warning      |
| Danger Red       | `#EF4444` | Status error        |
| Background       | `#F9FAFB` | Background utama    |
| Dark Text        | `#111827` | Teks heading        |
| Gray Text        | `#6B7280` | Teks secondary      |

### Typography

- **Font Family:** System default (Segoe UI, SF Pro, etc.)
- **Heading:** Bold, dark color
- **Body:** Regular, gray color

---

## 🔧 Troubleshooting

### Error: "Database connection failed"

1. Pastikan MySQL server berjalan
2. Cek konfigurasi di `database.properties`
3. Pastikan database `restaus_db` sudah dibuat

### Error: "Invalid target release: 17"

1. Pastikan Java 17+ terinstall
2. Set JAVA_HOME ke JDK 17:

   ```bash
   # Windows
   set JAVA_HOME=C:\Program Files\Java\jdk-17

   # Linux/macOS
   export JAVA_HOME=/path/to/jdk-17
   ```

### Login gagal

1. Pastikan username dan password benar
2. Cek data di tabel `users`
3. Password disimpan plain text (bukan hash)

---

## 🤝 Kontribusi

Kontribusi sangat diterima! Silakan:

1. Fork repository
2. Buat branch baru (`git checkout -b feature/AmazingFeature`)
3. Commit perubahan (`git commit -m 'Add some AmazingFeature'`)
4. Push ke branch (`git push origin feature/AmazingFeature`)
5. Buat Pull Request

---

## 📄 Lisensi

Proyek ini dilisensikan di bawah [MIT License](LICENSE).

---

## 👨‍💻 Author

**Restos Team**

- GitHub: [@yourusername](https://github.com/yourusername)

---

## 🙏 Acknowledgments

- [JavaFX](https://openjfx.io/)
- [MySQL](https://www.mysql.com/)
- [iText PDF](https://itextpdf.com/)
- [BCrypt](https://www.mindrot.org/projects/jBCrypt/)

---

<p align="center">
  Made with ❤️ using Java & JavaFX
</p>
