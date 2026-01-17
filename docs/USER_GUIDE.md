# 📋 Panduan Pengguna - RESTOS POS

## Daftar Isi
1. [Pendahuluan](#1-pendahuluan)
2. [Login & Registrasi](#2-login--registrasi)
3. [Admin Dashboard](#3-admin-dashboard)
4. [Waiter Module](#4-waiter-module)
5. [Kitchen Display System](#5-kitchen-display-system)
6. [Cashier POS](#6-cashier-pos)
7. [FAQ](#7-faq)

---

## 1. Pendahuluan

### Apa itu RESTOS?
RESTOS adalah aplikasi Point of Sale (POS) untuk restoran yang membantu mengelola:
- Pemesanan makanan/minuman
- Pengelolaan meja
- Proses pembayaran
- Laporan penjualan

### Siapa yang menggunakan?
- **Admin** - Mengelola menu, meja, pengguna, dan melihat laporan
- **Waiter** - Membuat pesanan untuk pelanggan
- **Kitchen** - Melihat dan memproses pesanan
- **Cashier** - Memproses pembayaran

---

## 2. Login & Registrasi

### 2.1 Login

1. Buka aplikasi RESTOS
2. Masukkan **Username** 
3. Masukkan **Password**
4. Klik tombol **"Masuk"** atau tekan **Enter**

![Login](screenshots/login.png)

**Tips:**
- Tekan Enter di field username untuk pindah ke password
- Tekan Enter di field password untuk langsung login

### 2.2 Registrasi Akun Baru

1. Klik link **"Daftar di sini"** di halaman login
2. Isi formulir:
   - **Nama Lengkap** *(wajib)*
   - **Username** *(wajib, minimal 4 karakter)*
   - **Email** *(opsional)*
   - **No. Telepon** *(opsional)*
   - **Role/Posisi** *(pilih: Waiter, Kitchen, atau Cashier)*
   - **Password** *(wajib, minimal 6 karakter)*
   - **Konfirmasi Password**
3. Klik tombol **"Daftar"**
4. Setelah berhasil, Anda akan diarahkan ke halaman login

**Catatan:** 
- Role Admin tidak tersedia untuk registrasi mandiri
- Username harus unik (belum digunakan)

---

## 3. Admin Dashboard

### 3.1 Overview Dashboard

Setelah login sebagai Admin, Anda akan melihat:
- **Statistik Hari Ini**: Pendapatan, jumlah pesanan, status meja
- **Grafik Pendapatan Mingguan**
- **Pesanan Terbaru**
- **Menu Populer**

### 3.2 Manajemen Menu

**Melihat Daftar Menu:**
1. Klik **"Menu"** di sidebar
2. Daftar menu akan ditampilkan dalam tabel

**Menambah Menu Baru:**
1. Klik tombol **"+ Tambah Menu"**
2. Isi form:
   - Nama Menu
   - Kategori
   - Harga
   - Deskripsi
3. Klik **"Simpan"**

**Mengedit Menu:**
1. Klik tombol **Edit** pada baris menu
2. Ubah data yang diperlukan
3. Klik **"Simpan"**

**Menghapus Menu:**
1. Klik tombol **Hapus** pada baris menu
2. Konfirmasi penghapusan

### 3.3 Manajemen Meja

**Melihat Daftar Meja:**
1. Klik **"Meja"** di sidebar

**Menambah Meja Baru:**
1. Klik **"+ Tambah Meja"**
2. Isi nomor meja dan kapasitas
3. Klik **"Simpan"**

**Mengubah Status Meja:**
1. Klik tombol **Edit**
2. Ubah status (Tersedia/Terisi/Dipesan)
3. Klik **"Simpan"**

### 3.4 Manajemen Pengguna

1. Klik **"Pengguna"** di sidebar
2. Lihat daftar semua pengguna
3. Tambah, edit, atau hapus pengguna

### 3.5 Manajemen Inventaris

1. Klik **"Inventaris"** di sidebar
2. Atur stok harian untuk setiap menu
3. Menu dengan stok 0 akan ditandai "Habis"

---

## 4. Waiter Module

### 4.1 Memilih Meja

1. Setelah login sebagai Waiter, Anda akan melihat grid meja
2. Warna meja menunjukkan status:
   - 🟢 **Hijau** = Tersedia
   - 🟠 **Orange** = Terisi (ada pesanan aktif)
   - 🔵 **Biru** = Dipesan (reserved)
3. Klik meja yang **tersedia** untuk membuat pesanan baru

### 4.2 Membuat Pesanan

1. Setelah memilih meja, halaman pesanan baru akan terbuka
2. **Cari Menu**: Gunakan kotak pencarian atau filter kategori
3. **Tambah ke Keranjang**: Klik menu dan atur jumlah
4. **Review Keranjang**: Periksa item di panel kanan
5. **Isi Info Pelanggan**: Nama pelanggan (opsional)
6. Klik **"Kirim Pesanan"**

### 4.3 Melihat Pesanan Saya

1. Klik **"Pesanan Saya"** di sidebar
2. Lihat semua pesanan yang Anda buat hari ini
3. Klik pesanan untuk melihat detail

### 4.4 Menambah Item ke Pesanan Aktif

1. Klik meja yang **terisi** (orange)
2. Pilih **"Tambah Item"**
3. Tambahkan item baru
4. Klik **"Update Pesanan"**

---

## 5. Kitchen Display System

### 5.1 Antrian Pesanan

Setelah login sebagai Kitchen, Anda akan melihat:
- Semua pesanan yang perlu diproses
- Setiap kartu pesanan menampilkan:
  - Nomor order
  - Nomor meja
  - Daftar item
  - Waktu tunggu

### 5.2 Filter Pesanan

Gunakan tombol filter di atas:
- **Semua** - Tampilkan semua pesanan
- **Pending** - Pesanan baru, belum diproses
- **Cooking** - Sedang dimasak
- **Served** - Sudah selesai dimasak

### 5.3 Memproses Pesanan

**Mulai Memasak:**
1. Temukan item dengan status **"Pending"**
2. Klik tombol **"Masak"** atau klik item
3. Status berubah menjadi **"Cooking"**

**Selesai Memasak:**
1. Setelah item selesai, klik **"Selesai"**
2. Status berubah menjadi **"Served"**
3. Item siap diantar ke pelanggan

### 5.4 Notifikasi

- Pesanan baru akan muncul dengan highlight
- Pesanan dengan waktu tunggu lama akan ditandai merah
- Data refresh otomatis setiap 5 detik

---

## 6. Cashier POS

### 6.1 Daftar Pesanan Siap Bayar

Setelah login sebagai Cashier, Anda akan melihat:
- Panel kiri: Daftar pesanan dengan status **"Delivered"**
- Panel kanan: Detail pembayaran

### 6.2 Memproses Pembayaran

1. **Pilih Pesanan**: Klik pesanan di panel kiri
2. **Lihat Detail**: Panel kanan menampilkan item dan total
3. **Pilih Metode Pembayaran**:
   - 💵 **Cash** - Pembayaran tunai
   - 💳 **Card** - Kartu debit/kredit
   - 📱 **E-Wallet** - GoPay, OVO, DANA, dll.
4. **Masukkan Jumlah Dibayar** (untuk cash)
5. **Sistem menghitung kembalian** otomatis
6. Klik **"Proses Pembayaran"**

### 6.3 Struk Pembayaran

Setelah pembayaran berhasil:
1. Preview struk akan muncul
2. Pilih aksi:
   - **Cetak** - Kirim ke printer
   - **Simpan PDF** - Export sebagai file PDF
   - **Tutup** - Kembali ke daftar pesanan

### 6.4 Statistik Harian

Panel sidebar menampilkan:
- Total transaksi hari ini
- Total pendapatan
- Rata-rata nilai transaksi

---

## 7. FAQ

### Q: Lupa password?
A: Hubungi Admin untuk reset password.

### Q: Bagaimana cara logout?
A: Klik tombol **"Logout"** di sidebar atau tekan **Ctrl+Q**.

### Q: Pesanan tidak muncul di Kitchen?
A: Pastikan pesanan sudah dikirim dengan mengklik "Kirim Pesanan".

### Q: Struk tidak tercetak?
A: Periksa koneksi printer dan pastikan driver terinstall.

### Q: Data tidak update otomatis?
A: Tekan **F5** untuk refresh manual atau tunggu auto-refresh.

### Q: Bagaimana membatalkan pesanan?
A: Hubungi Admin untuk membatalkan pesanan.

---

## Butuh Bantuan?

Jika mengalami masalah, hubungi:
- **Admin Sistem**
- **Technical Support**

---

*Dokumen ini adalah bagian dari RESTOS - Restaurant Point of Sale System*
