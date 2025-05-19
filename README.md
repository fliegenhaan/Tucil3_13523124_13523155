# Tucil3_13523124_13523155
# Rush Hour Puzzle Solver

Program ini mengimplementasikan penyelesaian puzzle Rush Hour menggunakan algoritma pathfinding UCS (Uniform Cost Search), Greedy Best First Search, A*, dan IDA*.

## Deskripsi

Rush Hour adalah permainan puzzle logika berbasis grid di mana pemain harus menggeser kendaraan di dalam kotak agar mobil utama (primary piece) dapat keluar dari kemacetan melalui pintu keluar. Program ini menyediakan solusi otomatis menggunakan berbagai algoritma pathfinding dengan visualisasi CLI dan GUI.

## Features

- Implementasi 4 algoritma pathfinding: UCS, Greedy Best First Search, A*, dan IDA*
- 3 jenis heuristik: Blocking Vehicles, Manhattan Distance, dan Combined Heuristic
- Mode CLI dengan output berwarna untuk menunjukkan pergerakan
- Mode GUI dengan animasi step-by-step
- Parsing file konfigurasi dengan format .txt
- Output statistik (jumlah node yang dikunjungi, waktu eksekusi)
- Opsi menyimpan solusi ke file

## Requirements

- Java 8 atau versi lebih tinggi
- IDE atau terminal dengan dukungan Java compilation

## Instalasi

1. Clone repository ini atau download semua file
2. Pastikan struktur direktori sesuai dengan yang ditentukan
3. Compile semua file Java

## Cara Kompilasi

```bash
cd src
javac Main.java -d ../bin
```

## Cara Menjalankan
(Pastikan berada di folder src)

```bash
java -cp ../bin Main
```

## Cara Penggunaan

1. Jalankan program dengan command di atas
2. Masukkan path file konfigurasi (contoh: `../test/test.txt`)
3. Pilih mode visualisasi: CLI atau GUI
4. Pilih algoritma: UCS, GREEDY, ASTAR, atau IDASTAR
5. Jika memilih algoritma yang menggunakan heuristik, pilih jenis heuristik: BLOCKING, MANHATTAN, atau COMBINED
6. Program akan menampilkan solusi beserta statistik
7. Pilih apakah ingin menyimpan solusi ke file

## Format File Konfigurasi

File input berformat .txt dengan struktur:
```
A B
N
konfigurasi_papan
```

Keterangan:
- `A B`: dimensi papan (lebar tinggi)
- `N`: jumlah piece non-primary
- `konfigurasi_papan`: matriks karakter representing board state
- `P`: primary piece (mobil merah)
- `K`: pintu keluar
- `.`: cell kosong
- Huruf/karakter lain: piece kendaraan

Contoh:
```
6 6
11
AAB..F
..BCDF
GPPCDFK
GH.III
GHJ...
LLJMM.
```

## Algoritma Pathfinding

### 1. UCS (Uniform Cost Search)
- Mencari solusi optimal berdasarkan biaya uniform
- f(n) = g(n)
- Menjamin solusi optimal

### 2. Greedy Best First Search
- Menggunakan heuristik untuk memandu pencarian
- f(n) = h(n)
- Tidak menjamin solusi optimal

### 3. A* Search
- Kombinasi UCS dan Greedy dengan heuristik admissible
- f(n) = g(n) + h(n)
- Menjamin solusi optimal jika heuristik admissible

### 4. IDA* Search
- Iterative Deepening A* untuk menghemat memori
- Menggunakan depth-first search dengan batas f(n)
- Menjamin solusi optimal seperti A*

## Heuristik

### 1. Blocking Vehicles Heuristic
Menghitung jumlah kendaraan yang menghalangi jalur primary piece ke pintu keluar.

### 2. Manhattan Distance Heuristic
Menghitung jarak Manhattan dari primary piece ke pintu keluar.

### 3. Combined Heuristic
Kombinasi blocking vehicles dan Manhattan distance dengan bobot yang disesuaikan.

## Output

Program menampilkan:
- Konfigurasi board awal
- Langkah-langkah solusi dengan format: `Gerakan X: piece-arah`
- Jumlah node yang dikunjungi
- Waktu eksekusi algoritma
- Total langkah solusi

### Mode CLI
Output dengan warna:
- **Merah**: Primary piece
- **Kuning**: Piece yang baru digerakkan
- **Hijau**: Pintu keluar

### Mode GUI
- Interface dengan tombol navigasi (Previous, Next, Play, Stop)
- Animasi step-by-step dengan kontrol kecepatan
- Display informasi gerakan dan status current step

## Project Structure

```
Tucil3_13523124_13523155/
├── src/
│   ├── algorithm/
│   │   ├── AStar.java
│   │   ├── GreedyBestFirstSearch.java
│   │   ├── IDAStar.java
│   │   ├── PathFinder.java
│   │   ├── UCS.java
│   │   └── heuristics/
│   │       ├── BlockingHeuristic.java
│   │       ├── CombinedHeuristic.java
│   │       ├── Heuristic.java
│   │       └── ManhattanHeuristic.java
│   ├── entity/
│   │   ├── Board.java
│   │   ├── Direction.java
│   │   ├── Move.java
│   │   ├── Node.java
│   │   ├── Orientation.java
│   │   ├── Piece.java
│   │   ├── Position.java
│   │   └── WallPosition.java
│   ├── parser/
│   │   └── FileParser.java
│   ├── visualizer/
│   │   ├── CLIVisualizer.java
│   │   └── GUIVisualizer.java
│   └── Main.java
├── bin/                    # compiled .class files
├── test/                   # test cases dan solusi
├── doc/                    # laporan tugas kecil dalam bentuk PDF
└── README.md              # file ini
```

## Author

- Muhammad Raihaan Perdana (13523124)
- M. Abizzar Gamadrian (13523155)
- Program Studi Teknik Informatika
- Sekolah Teknik Elektro dan Informatika
- Institut Teknologi Bandung

## License
Project ini dibuat sebagai bagian dari "Tugas Kecil 3 IF2211 Strategi Algoritma" semester II tahun 2024/2025.
