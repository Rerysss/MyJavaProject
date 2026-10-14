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
// Kelas Pesanan: satu baris pesanan pelanggan (menu + jumlah).
// Jika menu bernilai null, berarti baris pesanan kosong/tidak valid.
// ============================================================
class Pesanan {
    Menu menu;
    int jumlah;
    double subtotal;

    public Pesanan(Menu menu, int jumlah) {
        this.menu = menu;
        this.jumlah = jumlah;
        if (menu != null) {
            this.subtotal = menu.harga * jumlah;
        } else {
            this.subtotal = 0;
        }
    }
}

// ============================================================
// Kelas Utama
// ============================================================
public class Main {

    // Aturan restoran (dijadikan konstanta agar mudah diubah)
    static final double TARIF_PAJAK = 0.10;        // pajak 10%
    static final double BIAYA_PELAYANAN = 20000;   // biaya pelayanan tetap
    static final double BATAS_DISKON = 100000;     // diskon 10% jika total item > 100.000
    static final double PERSEN_DISKON = 0.10;
    static final double BATAS_PROMO = 50000;       // promo B1G1 minuman jika total item > 50.000

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // 1. Input menu restoran: array Menu (4 makanan + 4 minuman)
        Menu[] menuResto = new Menu[8];
        menuResto[0] = new Menu("Nasi Goreng", 25000, "Makanan");
        menuResto[1] = new Menu("Nasi Padang", 30000, "Makanan");
        menuResto[2] = new Menu("Mie Goreng", 20000, "Makanan");
        menuResto[3] = new Menu("Ayam Bakar", 35000, "Makanan");
        menuResto[4] = new Menu("Es Teh", 5000, "Minuman");
        menuResto[5] = new Menu("Es Jeruk", 8000, "Minuman");
        menuResto[6] = new Menu("Es Kopi", 15000, "Minuman");
        menuResto[7] = new Menu("Jus Alpukat", 18000, "Minuman");

        // Menampilkan daftar menu per kategori
        tampilkanMenu(menuResto);

        // 2. Pemesanan (maksimal 4 menu)
        System.out.println("\nMasukkan maksimal 4 pesanan dengan format: Nama Menu = Jumlah");
        System.out.println("Contoh: Nasi Padang = 2");
        System.out.println("Kosongkan (tekan Enter) jika tidak ingin menambah pesanan di baris tersebut.\n");

        System.out.print("Pesanan 1: ");
        Pesanan p1 = prosesPesanan(input.nextLine(), menuResto);

        System.out.print("Pesanan 2: ");
        Pesanan p2 = prosesPesanan(input.nextLine(), menuResto);

        System.out.print("Pesanan 3: ");
        Pesanan p3 = prosesPesanan(input.nextLine(), menuResto);

        System.out.print("Pesanan 4: ");
        Pesanan p4 = prosesPesanan(input.nextLine(), menuResto);

        // 3 & 4. Hitung total biaya dan cetak struk
        cetakStruk(p1, p2, p3, p4);

        input.close();
    }

    // ------------------------------------------------------------
    // MENAMPILKAN MENU
    // ------------------------------------------------------------
    static void tampilkanMenu(Menu[] menu) {
        System.out.println("========== DAFTAR MENU RESTORAN ==========");
        System.out.println("--- Makanan ---");
        cetakBarisMenu(1, menu[0]);
        cetakBarisMenu(2, menu[1]);
        cetakBarisMenu(3, menu[2]);
        cetakBarisMenu(4, menu[3]);

        System.out.println("\n--- Minuman ---");
        cetakBarisMenu(5, menu[4]);
        cetakBarisMenu(6, menu[5]);
        cetakBarisMenu(7, menu[6]);
        cetakBarisMenu(8, menu[7]);
    }

    static void cetakBarisMenu(int nomor, Menu m) {
        System.out.printf("%d. %-13s %s%n", nomor, m.nama, rp(m.harga));
    }

    // ------------------------------------------------------------
    // MENERIMA & MENGOLAH PESANAN
    // Mengubah teks "Nama Menu = Jumlah" menjadi objek Pesanan.
    // Jika kosong/tidak valid, mengembalikan Pesanan kosong (menu = null).
    // ------------------------------------------------------------
    static Pesanan prosesPesanan(String teks, Menu[] menu) {
        Pesanan kosong = new Pesanan(null, 0);

        if (teks.trim().isEmpty()) {
            return kosong; // baris dilewati
        }

        if (!teks.contains("=")) {
            System.out.println("   [!] Format salah. Gunakan: Nama Menu = Jumlah");
            return kosong;
        }

        String[] bagian = teks.split("=");
        if (bagian.length != 2) {
            System.out.println("   [!] Format salah. Gunakan: Nama Menu = Jumlah");
            return kosong;
        }

        String nama = bagian[0].trim();
        int jumlah;
        try {
            jumlah = Integer.parseInt(bagian[1].trim());
        } catch (NumberFormatException e) {
            System.out.println("   [!] Jumlah harus berupa angka bulat.");
            return kosong;
        }

        if (jumlah <= 0) {
            System.out.println("   [!] Jumlah harus lebih dari 0.");
            return kosong;
        }

        Menu ditemukan = cariMenu(nama, menu);
        if (ditemukan == null) {
            System.out.println("   [!] Menu \"" + nama + "\" tidak ada dalam daftar.");
            return kosong;
        }

        return new Pesanan(ditemukan, jumlah);
    }

    // Mencari menu berdasarkan nama (tanpa perulangan, memakai if - else if)
    static Menu cariMenu(String nama, Menu[] menu) {
        if (nama.equalsIgnoreCase(menu[0].nama)) return menu[0];
        else if (nama.equalsIgnoreCase(menu[1].nama)) return menu[1];
        else if (nama.equalsIgnoreCase(menu[2].nama)) return menu[2];
        else if (nama.equalsIgnoreCase(menu[3].nama)) return menu[3];
        else if (nama.equalsIgnoreCase(menu[4].nama)) return menu[4];
        else if (nama.equalsIgnoreCase(menu[5].nama)) return menu[5];
        else if (nama.equalsIgnoreCase(menu[6].nama)) return menu[6];
        else if (nama.equalsIgnoreCase(menu[7].nama)) return menu[7];
        else return null;
    }

    // ------------------------------------------------------------
    // MENGHITUNG TOTAL BIAYA
    // ------------------------------------------------------------

    // Total harga seluruh item yang dipesan (sebelum pajak & pelayanan)
    static double hitungTotalItem(Pesanan p1, Pesanan p2, Pesanan p3, Pesanan p4) {
        return p1.subtotal + p2.subtotal + p3.subtotal + p4.subtotal;
    }

    // Pajak 10% dari total biaya item
    static double hitungPajak(double totalItem) {
        return totalItem * TARIF_PAJAK;
    }

    // Total keseluruhan = total item + pajak + biaya pelayanan
    static double hitungTotalKeseluruhan(double totalItem) {
        return totalItem + hitungPajak(totalItem) + BIAYA_PELAYANAN;
    }

    // Diskon 10% jika total biaya item melebihi Rp100.000.
    // Nilai diskon dihitung dari total keseluruhan (setelah pajak & pelayanan).
    static double hitungDiskon(double totalItem, double totalKeseluruhan) {
        if (totalItem > BATAS_DISKON) {
            return totalKeseluruhan * PERSEN_DISKON;
        } else {
            return 0;
        }
    }

    // Pesanan ini minuman?
    static boolean adalahMinuman(Pesanan p) {
        if (p.menu == null) {
            return false;
        } else {
            return p.menu.kategori.equals("Minuman");
        }
    }

    // Promo Beli 1 Gratis 1 untuk salah satu minuman, jika total biaya item
    // melebihi Rp50.000. Mengembalikan nama minuman bonus (minuman pertama
    // yang dipesan), atau "" jika promo tidak berlaku.
    static String cariMinumanBonus(double totalItem, Pesanan p1, Pesanan p2, Pesanan p3, Pesanan p4) {
        if (totalItem > BATAS_PROMO) {
            if (adalahMinuman(p1)) return p1.menu.nama;
            else if (adalahMinuman(p2)) return p2.menu.nama;
            else if (adalahMinuman(p3)) return p3.menu.nama;
            else if (adalahMinuman(p4)) return p4.menu.nama;
        }
        return "";
    }

    // ------------------------------------------------------------
    // MENCETAK STRUK
    // ------------------------------------------------------------
    static void cetakStruk(Pesanan p1, Pesanan p2, Pesanan p3, Pesanan p4) {
        double totalItem = hitungTotalItem(p1, p2, p3, p4);

        // Tidak ada pesanan valid -> struk tidak dicetak
        if (totalItem == 0) {
            System.out.println("\nTidak ada pesanan yang valid. Struk tidak dicetak.");
            return;
        }

        double pajak = hitungPajak(totalItem);
        double totalKeseluruhan = hitungTotalKeseluruhan(totalItem);
        double diskon = hitungDiskon(totalItem, totalKeseluruhan);
        double hargaSetelahDiskon = totalKeseluruhan - diskon;
        String minumanBonus = cariMinumanBonus(totalItem, p1, p2, p3, p4);

        System.out.println("\n=============================================");
        System.out.println("               STRUK PESANAN");
        System.out.println("=============================================");

        // Hanya pesanan yang valid yang dicetak
        cetakBarisPesanan(p1);
        cetakBarisPesanan(p2);
        cetakBarisPesanan(p3);
        cetakBarisPesanan(p4);

        System.out.println("---------------------------------------------");
        cetakBaris("Total biaya pesanan", rp(totalItem));
        cetakBaris("Pajak (10%)", rp(pajak));
        cetakBaris("Biaya pelayanan", rp(BIAYA_PELAYANAN));
        System.out.println("---------------------------------------------");
        cetakBaris("Total keseluruhan", rp(totalKeseluruhan));

        // Skenario diskon 10%
        if (diskon > 0) {
            cetakBaris("Diskon 10%", "-" + rp(diskon));
            cetakBaris("Harga setelah diskon", rp(hargaSetelahDiskon));
        }

        // Skenario penawaran Beli 1 Gratis 1 minuman
        if (!minumanBonus.isEmpty()) {
            cetakBaris("Promo B1G1 (bonus gratis)", "1 x " + minumanBonus);
        }

        System.out.println("=============================================");
        cetakBaris("TOTAL BAYAR", rp(hargaSetelahDiskon));
        System.out.println("=============================================");
    }

    // Mencetak satu baris item pesanan (hanya jika pesanan valid)
    static void cetakBarisPesanan(Pesanan p) {
        if (p.menu != null) {
            System.out.printf("%-14s %2d x %9s = %10s%n",
                    p.menu.nama, p.jumlah, rp(p.menu.harga), rp(p.subtotal));
        }
    }

    // Mencetak satu baris "label ........ nilai" yang rata kanan
    static void cetakBaris(String label, String nilai) {
        System.out.printf("%-28s %16s%n", label, nilai);
    }

    // Format angka menjadi Rupiah, contoh: 25000 -> Rp25.000
    static String rp(double nilai) {
        return String.format(Locale.forLanguageTag("id-ID"), "Rp%,.0f", nilai);
    }
}