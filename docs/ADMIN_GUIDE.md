# 👨‍💼 Admin Role - Panduan Lengkap

## 📋 Daftar Isi
1. [Pengenalan Role Admin](#pengenalan-role-admin)
2. [Akses dan Login](#akses-dan-login)
3. [Fitur-Fitur Admin](#fitur-fitur-admin)
4. [Dashboard Admin](#dashboard-admin)
5. [Manajemen Menu](#manajemen-menu)
6. [Manajemen Meja](#manajemen-meja)
7. [Manajemen User](#manajemen-user)
8. [Manajemen Inventaris](#manajemen-inventaris)
9. [Best Practices](#best-practices)

---

## Pengenalan Role Admin

**Role Admin** adalah role dengan akses penuh ke sistem RESTOS POS. Admin bertanggung jawab untuk:

- ✅ Mengelola menu makanan dan minuman
- ✅ Mengelola meja restoran
- ✅ Mengelola user/karyawan
- ✅ Mengelola inventaris/stok harian
- ✅ Melihat laporan dan statistik penjualan
- ✅ Monitoring operasional restoran secara real-time

---

## Akses dan Login

### Cara 1: Login dengan Akun Default

Gunakan akun admin yang sudah tersedia:

```
Username: admin
Password: admin123
```

### Cara 2: Registrasi Akun Admin Baru

1. Buka aplikasi RESTOS
2. Klik **"Daftar di sini"** di halaman login
3. Isi form registrasi:
   - Nama Lengkap
   - Username (minimal 4 karakter)
   - Email (opsional)
   - No. Telepon (opsional)
   - **Role/Posisi: Pilih "Admin"** ⭐
   - Password (minimal 6 karakter)
   - Konfirmasi Password
4. Klik **"Daftar"**
5. Login dengan username dan password yang baru dibuat

---

## Fitur-Fitur Admin

### 🎯 Dashboard Overview
- Statistik real-time (pendapatan, pesanan, meja)
- Grafik pendapatan mingguan
- Daftar pesanan terbaru
- Menu populer

### 🍽️ Manajemen Menu
- CRUD (Create, Read, Update, Delete) menu
- Kategorisasi menu (Makanan Berat, Minuman, Dessert)
- Set harga dan deskripsi
- Toggle status aktif/nonaktif menu

### 🪑 Manajemen Meja
- CRUD meja restoran
- Set nomor meja dan kapasitas
- Update status meja (Available, Occupied, Reserved)
- Monitoring status meja real-time

### 👥 Manajemen User
- CRUD user/karyawan
- Assign role (Admin, Waiter, Kitchen, Cashier)
- Update informasi user
- Hapus user yang tidak aktif

### 📦 Manajemen Inventaris
- Set stok harian untuk setiap menu
- Update stok tersisa
- Monitoring menu yang hampir habis
- Auto-disable menu dengan stok 0

---

## Dashboard Admin

### Tampilan Utama

Setelah login sebagai Admin, Anda akan melihat dashboard dengan:

#### 📊 Statistik Cards (Top)
```
┌─────────────────┬─────────────────┬─────────────────┐
│ Total Pendapatan│  Total Pesanan  │   Status Meja   │
│   Rp 2.450.000  │       45        │  12/20 Terisi   │
└─────────────────┴─────────────────┴─────────────────┘
```

#### 📈 Grafik Pendapatan Mingguan
Menampilkan tren pendapatan 7 hari terakhir dalam bentuk line chart.

#### 📋 Pesanan Terbaru
Tabel yang menampilkan 10 pesanan terakhir dengan informasi:
- Order ID
- Nomor Meja
- Total Amount
- Status
- Waktu

#### 🏆 Menu Populer
Daftar 5 menu terlaris berdasarkan jumlah penjualan.

### Navigation Sidebar

```
┌─────────────────────┐
│  👤 Admin Name      │
│  📧 admin@restos    │
├─────────────────────┤
│  📊 Dashboard       │
│  🍽️ Menu            │
│  🪑 Meja            │
│  👥 Pengguna        │
│  📦 Inventaris      │
│  📈 Laporan         │
├─────────────────────┤
│  🚪 Logout          │
└─────────────────────┘
```

---

## Manajemen Menu

### Melihat Daftar Menu

1. Klik **"Menu"** di sidebar
2. Tabel menu akan ditampilkan dengan kolom:
   - ID
   - Nama Menu
   - Kategori
   - Harga
   - Status (Aktif/Nonaktif)
   - Aksi (Edit/Hapus)

### Menambah Menu Baru

1. Klik tombol **"+ Tambah Menu"** (pojok kanan atas)
2. Dialog form akan muncul
3. Isi data menu:
   ```
   Nama Menu    : [Contoh: Nasi Goreng Spesial]
   Kategori     : [Pilih: Makanan Berat/Minuman/Dessert]
   Harga        : [Contoh: 25000]
   Deskripsi    : [Contoh: Nasi goreng dengan telur...]
   Status       : [✓ Aktif]
   ```
4. Klik **"Simpan"**
5. Menu baru akan muncul di tabel

### Mengedit Menu

1. Klik tombol **"Edit"** (icon pensil) pada baris menu
2. Dialog edit akan muncul dengan data terisi
3. Ubah data yang diperlukan
4. Klik **"Simpan"**

### Menghapus Menu

1. Klik tombol **"Hapus"** (icon trash) pada baris menu
2. Konfirmasi dialog akan muncul:
   ```
   ⚠️ Konfirmasi Hapus
   Apakah Anda yakin ingin menghapus menu "Nasi Goreng Spesial"?
   
   [Batal]  [Hapus]
   ```
3. Klik **"Hapus"** untuk konfirmasi

### Toggle Status Menu

- Klik toggle switch di kolom "Status"
- Menu nonaktif tidak akan muncul di katalog Waiter

---

## Manajemen Meja

### Melihat Daftar Meja

1. Klik **"Meja"** di sidebar
2. Grid/Tabel meja akan ditampilkan dengan info:
   - Nomor Meja
   - Kapasitas (jumlah kursi)
   - Status (Available/Occupied/Reserved)

### Menambah Meja Baru

1. Klik **"+ Tambah Meja"**
2. Isi form:
   ```
   Nomor Meja   : [Contoh: T04]
   Kapasitas    : [Contoh: 4]
   Status       : [Available]
   ```
3. Klik **"Simpan"**

### Mengedit Meja

1. Klik tombol **"Edit"** pada meja
2. Ubah nomor, kapasitas, atau status
3. Klik **"Simpan"**

### Menghapus Meja

1. Klik tombol **"Hapus"**
2. Konfirmasi penghapusan
3. ⚠️ **Catatan:** Meja dengan pesanan aktif tidak bisa dihapus

### Status Meja

| Status      | Warna  | Keterangan                          |
|-------------|--------|-------------------------------------|
| Available   | 🟢 Hijau | Meja kosong, siap digunakan        |
| Occupied    | 🟠 Orange | Meja terisi, ada pesanan aktif     |
| Reserved    | 🔵 Biru  | Meja sudah dipesan (reservasi)     |

---

## Manajemen User

### Melihat Daftar User

1. Klik **"Pengguna"** di sidebar
2. Tabel user menampilkan:
   - ID
   - Username
   - Nama Lengkap
   - Role
   - Email
   - Aksi

### Menambah User Baru

1. Klik **"+ Tambah User"**
2. Isi form:
   ```
   Nama Lengkap : [Contoh: John Doe]
   Username     : [Contoh: johndoe]
   Email        : [Contoh: john@restos.com]
   No. Telepon  : [Contoh: 081234567890]
   Role         : [Pilih: Admin/Waiter/Kitchen/Cashier]
   Password     : [Minimal 6 karakter]
   ```
3. Klik **"Simpan"**

### Mengedit User

1. Klik **"Edit"** pada user
2. Ubah data (kecuali username)
3. Klik **"Simpan"**

### Menghapus User

1. Klik **"Hapus"**
2. Konfirmasi penghapusan
3. ⚠️ **Catatan:** User yang sedang login tidak bisa dihapus

### Role Permissions

| Role    | Dashboard | Menu | Meja | User | Inventory | Orders | Payment |
|---------|-----------|------|------|------|-----------|--------|---------|
| Admin   | ✅        | ✅   | ✅   | ✅   | ✅        | ✅     | ✅      |
| Waiter  | ❌        | 👁️   | 👁️   | ❌   | ❌        | ✅     | ❌      |
| Kitchen | ❌        | 👁️   | ❌   | ❌   | ❌        | 👁️     | ❌      |
| Cashier | ❌        | 👁️   | ❌   | ❌   | ❌        | 👁️     | ✅      |

*Legend: ✅ Full Access | 👁️ Read Only | ❌ No Access*

---

## Manajemen Inventaris

### Melihat Inventaris

1. Klik **"Inventaris"** di sidebar
2. Tabel inventaris menampilkan:
   - Menu
   - Kategori
   - Stok Awal Hari Ini
   - Stok Tersisa
   - Status

### Set Stok Harian

1. Klik **"Set Stok Harian"** atau edit item
2. Isi stok untuk menu:
   ```
   Menu         : Nasi Goreng Spesial
   Stok Awal    : [Contoh: 50]
   Stok Tersisa : [Auto-calculated]
   ```
3. Klik **"Simpan"**

### Update Stok Tersisa

- Stok akan otomatis berkurang saat ada pesanan
- Admin bisa manual update jika ada perubahan

### Monitoring Stok

**Indikator Stok:**
- 🟢 **Hijau** (>20): Stok aman
- 🟡 **Kuning** (5-20): Stok menipis
- 🔴 **Merah** (0-5): Stok hampir habis
- ⚫ **Hitam** (0): Stok habis (menu auto-disabled)

### Reset Stok Harian

1. Klik **"Reset Stok Harian"**
2. Semua stok tersisa akan di-reset ke stok awal
3. Biasanya dilakukan setiap pagi sebelum operasional

---

## Best Practices

### 🔐 Keamanan

1. **Ganti Password Default**
   - Segera ganti password "admin123" setelah login pertama
   - Gunakan password yang kuat (kombinasi huruf, angka, simbol)

2. **Batasi Akses Admin**
   - Jangan buat terlalu banyak akun admin
   - Hanya berikan role admin ke orang yang dipercaya

3. **Audit Log**
   - Pantau aktivitas user secara berkala
   - Periksa siapa yang menambah/edit/hapus data

### 📊 Operasional

1. **Update Stok Harian**
   - Set stok setiap pagi sebelum operasional
   - Monitor stok sepanjang hari
   - Reset stok di akhir hari

2. **Manajemen Menu**
   - Nonaktifkan menu yang tidak tersedia
   - Update harga secara berkala
   - Hapus menu yang sudah tidak dijual

3. **Monitoring Meja**
   - Pastikan status meja selalu akurat
   - Ubah status manual jika ada ketidaksesuaian

4. **Backup Data**
   - Lakukan backup database secara berkala
   - Simpan backup di lokasi aman

### 📈 Reporting

1. **Review Dashboard Harian**
   - Cek statistik pendapatan setiap hari
   - Identifikasi tren penjualan
   - Analisis menu populer

2. **Evaluasi Mingguan**
   - Review grafik pendapatan mingguan
   - Bandingkan dengan minggu sebelumnya
   - Buat strategi penjualan

3. **Laporan Bulanan**
   - Export data untuk laporan bulanan
   - Analisis performa karyawan
   - Evaluasi menu yang kurang laku

---

## Troubleshooting

### ❌ Tidak bisa login sebagai Admin

**Solusi:**
1. Pastikan username: `admin` dan password: `admin123`
2. Cek apakah database sudah di-import dengan benar
3. Verifikasi role di database: `SELECT * FROM users WHERE role='admin'`

### ❌ Menu tidak muncul di Waiter

**Solusi:**
1. Pastikan menu status = **Aktif**
2. Cek apakah stok > 0 (jika menggunakan inventory)
3. Refresh halaman Waiter (F5)

### ❌ Tidak bisa hapus menu/meja/user

**Solusi:**
1. Cek apakah ada pesanan aktif yang menggunakan item tersebut
2. Pastikan Anda login sebagai Admin
3. Cek koneksi database

### ❌ Statistik tidak update

**Solusi:**
1. Tekan **F5** untuk refresh
2. Klik tombol **Refresh** di dashboard
3. Tunggu auto-refresh (setiap 30 detik)

---

## Keyboard Shortcuts

| Shortcut  | Fungsi                    |
|-----------|---------------------------|
| `F5`      | Refresh data              |
| `Ctrl+N`  | Tambah item baru          |
| `Ctrl+S`  | Simpan perubahan          |
| `Ctrl+Q`  | Logout                    |
| `Escape`  | Tutup dialog/Cancel       |
| `F11`     | Toggle fullscreen         |

---

## Kesimpulan

Role Admin adalah role paling penting dalam sistem RESTOS POS. Dengan akses penuh ke semua fitur, Admin bertanggung jawab untuk:

✅ Memastikan data menu selalu update  
✅ Mengelola meja dan kapasitas restoran  
✅ Menambah/menghapus user sesuai kebutuhan  
✅ Monitoring stok dan inventaris  
✅ Menganalisis performa penjualan  

**Gunakan role Admin dengan bijak dan bertanggung jawab!** 🎯

---

*Dokumen ini adalah bagian dari RESTOS - Restaurant Point of Sale System*  
*Version 1.0.0 - Updated: 2026-01-26*
