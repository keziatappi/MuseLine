# MuseLine
MuseLine adalah aplikasi Android yang memungkinkan pengguna mencari lagu, menampilkan lirik, dan menyimpan lagu favorit mereka. Aplikasi ini menyediakan antarmuka yang ramah pengguna dengan kemampuan pengalihan tema dan dukungan mode offline.

## About Project
MuseLine adalah aplikasi Android yang dikembangkan untuk memudahkan penikmat musik dalam mengakses lirik lagu favorit mereka. Aplikasi ini lahir dari kebutuhan akan platform sederhana namun komprehensif yang menggabungkan pencarian lagu, akses lirik, dan manajemen koleksi musik dalam satu tempat. MuseLine dirancang dengan fokus pada pengalaman pengguna yang lancar, kemudahan akses informasi musik, dan fungsionalitas offline untuk mendukung penggunaan dalam berbagai kondisi jaringan.

### Problem Statement
Beberapa masalah yang menjadi latar belakang pengembangan MuseLine:
1. Fragmentasi Layanan Musik: Saat ini, pengguna sering harus beralih antara beberapa aplikasi berbeda untuk mendapatkan informasi lengkap tentang lagu (satu untuk mendengarkan, satu lagi untuk lirik, dst).
2. Ketergantungan Koneksi Internet: Banyak aplikasi lirik memerlukan koneksi internet yang stabil, sehingga tidak bisa diakses saat offline atau di area dengan koneksi terbatas.
3. Pengalaman Membaca Lirik: Aplikasi lirik yang ada sering menyajikan lirik dalam format yang kurang nyaman dibaca, terutama untuk lagu-lagu dengan lirik panjang.
4. Kurangnya Personalisasi: Pengguna kesulitan untuk melacak dan mengelola koleksi lagu favorit mereka beserta liriknya dalam satu tempat.
5. Kerumitan Antarmuka: Banyak aplikasi sejenis memiliki antarmuka yang rumit dan tidak intuitif, terutama bagi pengguna awam.

### Solution
MuseLine mengatasi masalah-masalah tersebut melalui pendekatan berikut:
1. Integrasi Layanan: Menggabungkan pencarian lagu (melalui Last.fm API) dan lirik (melalui web azlyric awalnya saya menggunakan lyrics.ovh API tetapi down) dalam satu aplikasi yang mulus.
2. Dukungan Mode Offline:
   - Menyimpan lagu favorit secara lokal
   - Indikator status offline yang jelas
   - Tombol refresh untuk mencoba ulang saat koneksi kembali tersedia
3. Pengalaman Membaca yang Ditingkatkan:
   - Fitur auto-scroll yang memudahkan pembacaan lirik panjang
   - Pemformatan lirik yang bersih dan rapi
   - Tampilan minimalis yang berfokus pada konten
4. Fitur Personalisasi:
   - Penandaan lagu favorit
   - Pelacakan riwayat pencarian
   - Pengelolaan koleksi musik pribadi
5. Antarmuka Pengguna yang Intuitif:
   - Navigasi berbasis tab yang sederhana
   - Tema gelap/terang untuk kenyamanan mata
   - Indikator visual yang jelas untuk setiap status (loading, offline, dll)

Dengan pendekatan ini, MuseLine menyediakan solusi terpadu untuk penggemar musik yang ingin mengakses dan mengelola lirik lagu favorit mereka dengan cara yang lebih efisien dan menyenangkan, bahkan dalam kondisi konektivitas terbatas.

## Features
1. Pencarian Lagu: Mencari lagu menggunakan API Last.fm
2. Tampilan Lirik: Menampilkan lirik lagu menggunakan web azlyric
3. Fitur Auto-scroll Lirik: Fitur scroll otomatis yang nyaman saat membaca lirik
4. Manajemen Favorit: Simpan dan kelola lagu favorit
5. Riwayat Pencarian: Melacak riwayat pencarian untuk akses cepat
6. Tema Gelap/Terang: Beralih antara tema gelap dan terang
7. Dukungan Mode Offline: Fungsionalitas dasar saat offline dengan indikator yang sesuai

## Screenshots
### Home Page
![WhatsApp Image 2025-06-10 at 22 06 51_99e68262](https://github.com/user-attachments/assets/367968b2-d418-414a-bc05-cabffa527a28)
### Search Page
![WhatsApp Image 2025-06-10 at 22 06 51_d950677d](https://github.com/user-attachments/assets/e54c2517-1ccc-44c2-8916-0db95669836b)
### Detail Lyrics
![WhatsApp Image 2025-06-10 at 22 56 47_ad5dfbe1](https://github.com/user-attachments/assets/c8bffc09-6617-4cec-9ac1-2ff7a8dcaadd)
### Favorite Page
![WhatsApp Image 2025-06-10 at 22 06 52_b31cf7a0](https://github.com/user-attachments/assets/0ed43e4c-4ab0-4989-b715-a885bcc0666f)
### Offline Mode
![WhatsApp Image 2025-06-10 at 22 56 46_8c49a5d2](https://github.com/user-attachments/assets/8a5db525-16e5-4b3e-b1ed-7ae2cf42d5bb)
### Light Mode
![WhatsApp Image 2025-06-10 at 22 06 51_8c55940f](https://github.com/user-attachments/assets/9460c328-f670-440a-be57-78348fb64013)


## Technical Implementation
### Arsitektur Aplikasi
Aplikasi menggunakan arsitektur Model-View-Controller (MVC) dengan komponen berikut:
1. Model: Kelas data dalam paket com.example.museline.data.model
2. View: Layout XML di res/layout dan kelas Fragment/Activity
3. Controller: Logic aplikasi dalam Activity dan Fragment
