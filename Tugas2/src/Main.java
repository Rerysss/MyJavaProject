import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

// ============================================================
// Kelas Menu: merepresentasikan satu item menu restoran
// ============================================================
class Menu {
    String nama;
    double harga;
    String kategori; // "Makanan" atau "Minuman"

    public Menu(String nama, double harga, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.kategori = kategori;
    }
}

// ============================================================
// Kelas Pesanan: satu baris pesanan pelanggan (menu + jumlah)
// ============================================================
class Pesanan {
    Menu menu;
    int jumlah;
    double subtotal;

    public Pesanan(Menu menu, int jumlah) {
        this.menu = menu;
        this.jumlah = jumlah;
        this.subtotal = menu.harga * jumlah;
    }

    // Dipakai jika pelanggan memesan menu yang sama lebih dari sekali
    void tambahJumlah(int tambahan) {
        this.jumlah += tambahan;
        this.subtotal = menu.harga * this.jumlah;
    }
}

// ============================================================
// Kelas Utama
// ============================================================
public class Main {

    // Aturan restoran
    static final double TARIF_PAJAK = 0.10;       // pajak 10%
    static final double BIAYA_PELAYANAN = 20000;  // biaya pelayanan tetap
    static final double BATAS_DISKON = 100000;    // diskon 10% jika total biaya item > 100.000
    static final double PERSEN_DISKON = 0.10;
    static final double BATAS_PROMO = 50000;      // B1G1 minuman jika total biaya item > 50.000

    // Array untuk menu restoran (otomatis diperbesar jika penuh)
    static Menu[] daftarMenu = new Menu[10];
    static int jumlahMenu = 0;

    // Array untuk pesanan pelanggan (otomatis diperbesar jika penuh)
    static Pesanan[] keranjang = new Pesanan[10];
    static int jumlahPesanan = 0;

    static Scanner input = new Scanner(System.in);

    // ------------------------------------------------------------
    // MENU UTAMA (parent): pelanggan, pemilik, keluar
    // ------------------------------------------------------------
    public static void main(String[] args) {
        inisialisasiMenuAwal();

        boolean isRunning = true;
        while (isRunning) { // perulangan while untuk navigasi utama
            System.out.println("\n=== APLIKASI RESTORAN SEDERHANA ===");
            System.out.println("1. Pesan Menu (Menu Pelanggan)");
            System.out.println("2. Kelola Menu (Menu Pemilik)");
            System.out.println("3. Keluar");
            System.out.print("Pilih opsi (1/2/3): ");
            String opsi = input.nextLine().trim();

            switch (opsi) { // struktur keputusan switch-case
                case "1":
                    menuPelanggan();
                    break;
                case "2":
                    menuPemilik();
                    break;
                case "3":
                    System.out.println("Terima kasih telah menggunakan aplikasi restoran.");
                    isRunning = false;
                    break;
                default:
                    System.out.println("[!] Pilihan tidak valid, silakan input kembali.");
            }
        }
        input.close();
    }

    // Data awal: 4 makanan + 4 minuman
    static void inisialisasiMenuAwal() {
        sisipkanMenu(new Menu("Nasi Goreng", 25000, "Makanan"));
        sisipkanMenu(new Menu("Nasi Padang", 30000, "Makanan"));
        sisipkanMenu(new Menu("Mie Goreng", 20000, "Makanan"));
        sisipkanMenu(new Menu("Ayam Bakar", 35000, "Makanan"));
        sisipkanMenu(new Menu("Es Teh", 5000, "Minuman"));
        sisipkanMenu(new Menu("Es Jeruk", 8000, "Minuman"));
        sisipkanMenu(new Menu("Es Kopi", 15000, "Minuman"));
        sisipkanMenu(new Menu("Jus Alpukat", 18000, "Minuman"));
    }

    // ------------------------------------------------------------
    // MENAMPILKAN MENU (dikelompokkan per kategori, memakai for)
    // Array selalu terurut: semua Makanan di depan, lalu Minuman.
    // ------------------------------------------------------------
    static void tampilkanMenu() {
        System.out.println("\n========== DAFTAR MENU RESTORAN ==========");
        if (jumlahMenu == 0) {
            System.out.println("(belum ada menu)");
        }
        String kategoriSebelumnya = "";
        for (int i = 0; i < jumlahMenu; i++) {
            Menu m = daftarMenu[i];
            // Cetak judul kategori setiap kali kategori berganti
            if (!m.kategori.equals(kategoriSebelumnya)) {
                System.out.println("\n--- " + m.kategori + " ---");
                kategoriSebelumnya = m.kategori;
            }
            System.out.printf("%2d. %-15s %s%n", i + 1, m.nama, rp(m.harga));
        }
        System.out.println("==========================================");
    }

    // Menyisipkan menu baru agar tetap berada di kelompok kategorinya
    static void sisipkanMenu(Menu baru) {
        if (jumlahMenu == daftarMenu.length) { // array penuh -> perbesar
            daftarMenu = Arrays.copyOf(daftarMenu, daftarMenu.length * 2);
        }
        int posisi = jumlahMenu; // Minuman: di akhir
        if (baru.kategori.equals("Makanan")) {
            posisi = hitungJumlahKategori("Makanan"); // Makanan: setelah makanan terakhir
        }
        for (int i = jumlahMenu; i > posisi; i--) { // geser ke kanan
            daftarMenu[i] = daftarMenu[i - 1];
        }
        daftarMenu[posisi] = baru;
        jumlahMenu++;
    }

    static int hitungJumlahKategori(String kategori) {
        int hitung = 0;
        for (int i = 0; i < jumlahMenu; i++) {
            if (daftarMenu[i].kategori.equals(kategori)) {
                hitung++;
            }
        }
        return hitung;
    }

    // ============================================================
    // FITUR 1: MENU PELANGGAN (MENERIMA & MENGOLAH PESANAN)
    // ============================================================
    static void menuPelanggan() {
        keranjang = new Pesanan[10];
        jumlahPesanan = 0;

        tampilkanMenu();
        System.out.println("\nKetik nama atau nomor menu untuk memesan.");
        System.out.println("Perintah lain: 'hapus' untuk menghapus pesanan, 'selesai' jika sudah selesai.");

        while (true) { // pesanan tidak dibatasi, berhenti saat input 'selesai'
            System.out.print("\nMenu yang dipesan (nama/nomor/'hapus'/'selesai'): ");
            String teks = input.nextLine().trim();

            if (teks.equalsIgnoreCase("selesai")) {
                break;
            } else if (teks.equalsIgnoreCase("hapus")) {
                hapusPesanan();
            } else {
                Menu dipilih = cariMenu(teks);
                if (dipilih == null) {
                    // Input di luar pilihan menu -> terus meminta input kembali
                    System.out.println("[!] Menu tidak ditemukan. Silakan input kembali.");
                } else {
                    int qty = inputJumlah(dipilih.nama);
                    tambahKeKeranjang(dipilih, qty);
                    System.out.println("-> " + qty + " " + dipilih.nama + " ditambahkan ke pesanan.");
                }
            }
        }

        if (jumlahPesanan > 0) {
            // Salin pesanan yang terisi saja ke array baru, lalu cetak struk
            Pesanan[] pesananAktif = Arrays.copyOf(keranjang, jumlahPesanan);
            cetakStruk(pesananAktif);
        } else {
            System.out.println("[!] Tidak ada pesanan yang dibuat.");
        }
    }

    // Mencari menu berdasarkan nomor (sesuai daftar) atau nama
    static Menu cariMenu(String teks) {
        try {
            int nomor = Integer.parseInt(teks);
            if (nomor >= 1 && nomor <= jumlahMenu) {
                return daftarMenu[nomor - 1];
            }
        } catch (NumberFormatException e) {
            // bukan angka, lanjut mencari berdasarkan nama
        }
        return cariMenuBerdasarkanNama(teks);
    }

    static Menu cariMenuBerdasarkanNama(String nama) {
        for (int i = 0; i < jumlahMenu; i++) {
            if (daftarMenu[i].nama.equalsIgnoreCase(nama)) {
                return daftarMenu[i];
            }
        }
        return null;
    }

    // Meminta jumlah pesanan; terus meminta ulang sampai valid (do-while)
    static int inputJumlah(String namaMenu) {
        int qty = 0;
        boolean valid = false;
        do {
            System.out.print("Masukkan jumlah pesanan untuk " + namaMenu + ": ");
            try {
                qty = Integer.parseInt(input.nextLine().trim());
                if (qty > 0) {
                    valid = true;
                } else {
                    System.out.println("[!] Jumlah harus lebih dari 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[!] Format angka salah. Silakan input kembali.");
            }
        } while (!valid);
        return qty;
    }

    // Menambah pesanan ke array keranjang
    static void tambahKeKeranjang(Menu menu, int qty) {
        // Jika menu sudah ada di keranjang, cukup tambah jumlahnya
        for (int i = 0; i < jumlahPesanan; i++) {
            if (keranjang[i].menu == menu) {
                keranjang[i].tambahJumlah(qty);
                return;
            }
        }
        if (jumlahPesanan == keranjang.length) { // array penuh -> perbesar
            keranjang = Arrays.copyOf(keranjang, keranjang.length * 2);
        }
        keranjang[jumlahPesanan++] = new Pesanan(menu, qty);
    }

    // Menghapus satu pesanan dari keranjang sebelum pemesanan selesai
    static void hapusPesanan() {
        if (jumlahPesanan == 0) {
            System.out.println("[!] Belum ada pesanan yang bisa dihapus.");
            return;
        }
        System.out.println("\nPesanan saat ini:");
        for (int i = 0; i < jumlahPesanan; i++) {
            System.out.printf("%d. %s x %d%n", i + 1, keranjang[i].menu.nama, keranjang[i].jumlah);
        }
        while (true) {
            System.out.print("Nomor pesanan yang dihapus (0 untuk batal): ");
            try {
                int nomor = Integer.parseInt(input.nextLine().trim());
                if (nomor == 0) {
                    break;
                } else if (nomor >= 1 && nomor <= jumlahPesanan) {
                    String nama = keranjang[nomor - 1].menu.nama;
                    for (int i = nomor - 1; i < jumlahPesanan - 1; i++) { // geser ke kiri
                        keranjang[i] = keranjang[i + 1];
                    }
                    keranjang[jumlahPesanan - 1] = null;
                    jumlahPesanan--;
                    System.out.println("-> Pesanan " + nama + " dihapus.");
                    break;
                } else {
                    System.out.println("[!] Nomor di luar jangkauan, silakan input kembali.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[!] Format salah, masukkan nomor urut angka.");
            }
        }
    }

    // ------------------------------------------------------------
    // MENGHITUNG TOTAL BIAYA
    // ------------------------------------------------------------

    // Total harga seluruh item (perulangan for-each)
    static double hitungTotalItem(Pesanan[] pesanan) {
        double total = 0;
        for (Pesanan p : pesanan) {
            total += p.subtotal;
        }
        return total;
    }

    // Pajak 10% dari total biaya item
    static double hitungPajak(double totalItem) {
        return totalItem * TARIF_PAJAK;
    }

    // Diskon 10% jika total biaya item melebihi Rp100.000.
    // Nilai diskon dihitung dari total keseluruhan (item + pajak + pelayanan).
    static double hitungDiskon(double totalItem, double totalKeseluruhan) {
        if (totalItem > BATAS_DISKON) {
            return totalKeseluruhan * PERSEN_DISKON;
        } else {
            return 0;
        }
    }

    // Beli 1 gratis 1 untuk salah satu minuman jika total biaya item
    // melebihi Rp50.000. Mengembalikan nama minuman bonus (minuman pertama
    // yang dipesan) atau "" jika promo tidak berlaku.
    static String cariMinumanBonus(double totalItem, Pesanan[] pesanan) {
        if (totalItem > BATAS_PROMO) {
            for (Pesanan p : pesanan) {
                if (p.menu.kategori.equals("Minuman")) {
                    return p.menu.nama;
                }
            }
        }
        return "";
    }

    // ------------------------------------------------------------
    // MENCETAK STRUK
    // ------------------------------------------------------------
    static void cetakStruk(Pesanan[] pesanan) {
        double totalItem = hitungTotalItem(pesanan);
        double pajak = hitungPajak(totalItem);
        double totalKeseluruhan = totalItem + pajak + BIAYA_PELAYANAN;
        double diskon = hitungDiskon(totalItem, totalKeseluruhan);
        String minumanBonus = cariMinumanBonus(totalItem, pesanan);
        double totalBayar = totalKeseluruhan - diskon;

        System.out.println("\n=============================================");
        System.out.println("               STRUK PESANAN");
        System.out.println("=============================================");

        for (Pesanan p : pesanan) {
            System.out.printf("%-15s %2d x %9s = %10s%n",
                    p.menu.nama, p.jumlah, rp(p.menu.harga), rp(p.subtotal));
        }

        System.out.println("---------------------------------------------");
        cetakBaris("Total Biaya Item", rp(totalItem));
        cetakBaris("Pajak (10%)", rp(pajak));
        cetakBaris("Biaya Pelayanan", rp(BIAYA_PELAYANAN));
        System.out.println("---------------------------------------------");
        cetakBaris("Total Keseluruhan", rp(totalKeseluruhan));

        if (diskon > 0) {
            cetakBaris("Diskon 10%", "-" + rp(diskon));
            cetakBaris("Harga Setelah Diskon", rp(totalBayar));
        }

        if (!minumanBonus.isEmpty()) {
            cetakBaris("Promo B1G1 (bonus gratis)", "1 x " + minumanBonus);
        }

        System.out.println("=============================================");
        cetakBaris("TOTAL BAYAR", rp(totalBayar));
        System.out.println("=============================================");
    }

    // ============================================================
    // FITUR 2: MENU PEMILIK (MANAJEMEN MENU)
    // ============================================================
    static void menuPemilik() {
        boolean diMenuPemilik = true;
        while (diMenuPemilik) {
            System.out.println("\n=== KELOLA MENU RESTORAN ===");
            System.out.println("1. Tambah Menu Baru (bisa beberapa sekaligus)");
            System.out.println("2. Ubah Harga Menu");
            System.out.println("3. Hapus Menu");
            System.out.println("4. Kembali ke Menu Utama");
            System.out.print("Pilih opsi (1/2/3/4): ");
            String opsi = input.nextLine().trim();

            switch (opsi) {
                case "1":
                    tambahMenu();
                    break;
                case "2":
                    ubahMenu();
                    break;
                case "3":
                    hapusMenu();
                    break;
                case "4":
                    diMenuPemilik = false; // kembali ke menu parent
                    break;
                default:
                    System.out.println("[!] Pilihan tidak valid, silakan input kembali.");
            }
        }
    }

    // Menambah satu atau beberapa menu baru sekaligus (perulangan for)
    static void tambahMenu() {
        int banyak = inputBilanganPositif("\nBerapa menu baru yang ingin ditambahkan sekaligus? ");
        for (int ke = 1; ke <= banyak; ke++) {
            System.out.println("\n--- Menu baru ke-" + ke + " dari " + banyak + " ---");
            String nama = inputNamaMenuBaru();
            double harga = inputHarga("Masukkan harga menu: ");
            String kategori = inputKategori();
            sisipkanMenu(new Menu(nama, harga, kategori));
            System.out.println("-> Menu " + nama + " berhasil ditambahkan.");
        }
    }

    static void ubahMenu() {
        if (jumlahMenu == 0) {
            System.out.println("[!] Belum ada menu.");
            return;
        }
        tampilkanMenu();
        int nomor = inputNomorMenu("Masukkan nomor menu yang harganya ingin diubah (0 untuk batal): ");
        if (nomor == 0) {
            System.out.println("-> Perubahan dibatalkan.");
            return;
        }
        Menu m = daftarMenu[nomor - 1];
        double hargaBaru = inputHarga("Masukkan harga baru untuk " + m.nama + ": ");

        // Konfirmasi sebelum dieksekusi
        boolean yakin = konfirmasi("Ubah harga " + m.nama + " dari " + rp(m.harga)
                + " menjadi " + rp(hargaBaru) + "? (Ya/Tidak): ");
        if (yakin) {
            m.harga = hargaBaru;
            System.out.println("-> Harga menu berhasil diubah.");
        } else {
            System.out.println("-> Perubahan dibatalkan.");
        }
    }

    static void hapusMenu() {
        if (jumlahMenu == 0) {
            System.out.println("[!] Belum ada menu.");
            return;
        }
        tampilkanMenu();
        int nomor = inputNomorMenu("Masukkan nomor menu yang ingin dihapus (0 untuk batal): ");
        if (nomor == 0) {
            System.out.println("-> Penghapusan dibatalkan.");
            return;
        }
        int indeks = nomor - 1;
        String nama = daftarMenu[indeks].nama;

        // Konfirmasi sebelum dieksekusi
        boolean yakin = konfirmasi("Yakin ingin menghapus " + nama + "? (Ya/Tidak): ");
        if (yakin) {
            for (int i = indeks; i < jumlahMenu - 1; i++) { // geser ke kiri
                daftarMenu[i] = daftarMenu[i + 1];
            }
            daftarMenu[jumlahMenu - 1] = null;
            jumlahMenu--;
            System.out.println("-> Menu " + nama + " berhasil dihapus.");
        } else {
            System.out.println("-> Penghapusan dibatalkan.");
        }
    }

    // ============================================================
    // METHOD BANTU INPUT (terus meminta input sampai valid)
    // ============================================================
    static int inputBilanganPositif(String pertanyaan) {
        while (true) {
            System.out.print(pertanyaan);
            try {
                int n = Integer.parseInt(input.nextLine().trim());
                if (n > 0) {
                    return n;
                }
                System.out.println("[!] Angka harus lebih dari 0.");
            } catch (NumberFormatException e) {
                System.out.println("[!] Format salah, masukkan angka bulat.");
            }
        }
    }

    static String inputNamaMenuBaru() {
        while (true) {
            System.out.print("Masukkan nama menu baru: ");
            String nama = input.nextLine().trim();
            if (nama.isEmpty()) {
                System.out.println("[!] Nama menu tidak boleh kosong.");
            } else if (nama.equalsIgnoreCase("selesai") || nama.equalsIgnoreCase("hapus")) {
                System.out.println("[!] Nama itu dipakai sebagai perintah pemesanan, gunakan nama lain.");
            } else if (cariMenuBerdasarkanNama(nama) != null) {
                System.out.println("[!] Menu dengan nama itu sudah ada.");
            } else {
                return nama;
            }
        }
    }

    static double inputHarga(String pertanyaan) {
        while (true) {
            System.out.print(pertanyaan);
            try {
                double harga = Double.parseDouble(input.nextLine().trim());
                if (harga > 0 && !Double.isInfinite(harga)) {
                    return harga;
                }
                System.out.println("[!] Harga harus lebih dari 0.");
            } catch (NumberFormatException e) {
                System.out.println("[!] Format harga salah, masukkan angka.");
            }
        }
    }

    static String inputKategori() {
        while (true) {
            System.out.print("Masukkan kategori (Makanan/Minuman): ");
            String kategori = normalisasiKategori(input.nextLine().trim());
            if (!kategori.isEmpty()) {
                return kategori;
            }
            System.out.println("[!] Kategori hanya boleh 'Makanan' atau 'Minuman'.");
        }
    }

    // Menyeragamkan penulisan kategori; "" jika tidak dikenali
    static String normalisasiKategori(String teks) {
        if (teks.equalsIgnoreCase("Makanan")) {
            return "Makanan";
        } else if (teks.equalsIgnoreCase("Minuman")) {
            return "Minuman";
        } else {
            return "";
        }
    }

    // Mengembalikan 0 (batal) atau nomor menu yang valid
    static int inputNomorMenu(String pertanyaan) {
        while (true) {
            System.out.print(pertanyaan);
            try {
                int nomor = Integer.parseInt(input.nextLine().trim());
                if (nomor >= 0 && nomor <= jumlahMenu) {
                    return nomor;
                }
                System.out.println("[!] Nomor menu di luar jangkauan, silakan input kembali.");
            } catch (NumberFormatException e) {
                System.out.println("[!] Format salah, masukkan nomor urut angka.");
            }
        }
    }

    // Konfirmasi Ya/Tidak; input lain diminta ulang
    static boolean konfirmasi(String pertanyaan) {
        while (true) {
            System.out.print(pertanyaan);
            String jawab = input.nextLine().trim();
            if (jawab.equalsIgnoreCase("Ya")) {
                return true;
            } else if (jawab.equalsIgnoreCase("Tidak")) {
                return false;
            }
            System.out.println("[!] Jawab dengan 'Ya' atau 'Tidak'.");
        }
    }

    // ============================================================
    // UTILITAS FORMAT
    // ============================================================
    static void cetakBaris(String label, String nilai) {
        System.out.printf("%-29s %15s%n", label, nilai);
    }

    // Format angka menjadi Rupiah, contoh: 25000 -> Rp25.000
    static String rp(double nilai) {
        return String.format(Locale.forLanguageTag("id-ID"), "Rp%,.0f", nilai);
    }
}