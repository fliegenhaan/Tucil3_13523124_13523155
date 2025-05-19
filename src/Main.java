import algorithm.AStar;
import algorithm.GreedyBestFirstSearch;
import algorithm.IDAStar;
import algorithm.PathFinder;
import algorithm.UCS;
import algorithm.heuristics.BlockingHeuristic;
import algorithm.heuristics.CombinedHeuristic;
import algorithm.heuristics.Heuristic;
import algorithm.heuristics.ManhattanHeuristic;
import entity.Board;
import entity.Node;
import java.io.*;
import java.util.*;
import javax.swing.SwingUtilities;
import parser.FileParser;
import visualizer.CLIVisualizer;
import visualizer.GUIVisualizer;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== Rush Hour Puzzle Solver ===");
            System.out.println("Implementasi UCS, Greedy Best First Search, A*, dan IDA*");
            System.out.println();
            boolean continueProgram = true;
            Board currentBoard = null;
            String currentFilePath = null;
            while (continueProgram) {
                // jika belum ada board atau user ingin ganti testcase
                if (currentBoard == null) {
                    while (currentBoard == null) {
                        try {
                            System.out.print("Masukkan path file konfigurasi (atau 'exit' untuk keluar): ");
                            String filePath = scanner.nextLine().trim();
                            
                            if (filePath.equalsIgnoreCase("exit")) {
                                System.out.println("Program dihentikan oleh user.");
                                scanner.close();
                                return;
                            }
                            
                            if (filePath.isEmpty()) {
                                System.out.println("Path file tidak boleh kosong!\n");
                                continue;
                            }
                            
                            System.out.println("Testing file konfigurasi...");
                            currentBoard = FileParser.parseFromFile(filePath);
                            currentFilePath = filePath;
                            System.out.println("File berhasil diparse!");
                            
                        } catch (IOException e) {
                            System.err.println("Error membaca file: " + e.getMessage());
                            System.out.println("Silakan coba file lain.\n");
                        } catch (Exception e) {
                            System.err.println("Error parsing: " + e.getMessage());
                            System.out.println("Silakan coba file lain.\n");
                        }
                    }
                }
                
                try {
                    System.out.println("\nMenggunakan file: " + currentFilePath);
                    
                    System.out.print("Pilih mode (CLI/GUI): ");
                    String mode = scanner.nextLine().toUpperCase().trim();
                    
                    while (!mode.equals("CLI") && !mode.equals("GUI")) {
                        System.out.print("Mode tidak valid! Pilih CLI atau GUI: ");
                        mode = scanner.nextLine().toUpperCase().trim();
                    }
                    
                    System.out.print("Pilih algoritma (UCS/GREEDY/ASTAR/IDASTAR): ");
                    String algorithm = scanner.nextLine().toUpperCase().trim();
                    
                    while (!algorithm.equals("UCS") && !algorithm.equals("GREEDY") &&
                            !algorithm.equals("ASTAR") && !algorithm.equals("IDASTAR")) {
                        System.out.print("Algoritma tidak valid! Pilih UCS/GREEDY/ASTAR/IDASTAR: ");
                        algorithm = scanner.nextLine().toUpperCase().trim();
                    }
                    
                    Heuristic heuristic = null;
                    if (algorithm.equals("GREEDY") || algorithm.equals("ASTAR") || algorithm.equals("IDASTAR")) {
                        System.out.print("Pilih heuristic (BLOCKING/MANHATTAN/COMBINED): ");
                        String heuristicChoice = scanner.nextLine().toUpperCase().trim();
                        
                        while (!heuristicChoice.equals("BLOCKING") && !heuristicChoice.equals("MANHATTAN") &&
                                !heuristicChoice.equals("COMBINED")) {
                            System.out.print("Heuristic tidak valid! Pilih BLOCKING/MANHATTAN/COMBINED: ");
                            heuristicChoice = scanner.nextLine().toUpperCase().trim();
                        }
                        
                        heuristic = switch (heuristicChoice) {
                            case "MANHATTAN" -> new ManhattanHeuristic();
                            case "COMBINED" -> new CombinedHeuristic();
                            default -> new BlockingHeuristic();
                        };
                    }
                    
                    PathFinder pathFinder;
                    pathFinder = switch (algorithm) {
                        case "GREEDY" -> new GreedyBestFirstSearch(heuristic);
                        case "ASTAR" -> new AStar(heuristic);
                        case "IDASTAR" -> new IDAStar(heuristic);
                        default -> new UCS();
                    };
                    
                    System.out.println("\nMencari solusi menggunakan " + pathFinder.getAlgorithmName() + "...");
                    long startTime = System.currentTimeMillis();
                    List<Node> solution = pathFinder.findPath(currentBoard);
                    long totalTime = System.currentTimeMillis() - startTime;
                    
                    if (solution == null) {
                        System.out.println("Tidak ada solusi yang ditemukan.");
                        System.out.println("Jumlah node yang dikunjungi: " + pathFinder.getNodesVisited());
                        System.out.println("Total waktu eksekusi: " + totalTime + " ms");
                    } else {
                        System.out.println("\n=== SOLUSI DITEMUKAN! ===");
                        System.out.println("Algoritma: " + pathFinder.getAlgorithmName());
                        System.out.println("Jumlah node yang dikunjungi: " + pathFinder.getNodesVisited());
                        System.out.println("Waktu eksekusi algoritma: " + pathFinder.getExecutionTime() + " ms");
                        System.out.println("Total waktu program: " + totalTime + " ms");
                        System.out.println("Jumlah langkah solusi: " + (solution.size() - 1));
                        System.out.println();
                        
                        if (mode.equals("GUI")) {
                            System.out.println("Membuka GUI visualizer...");
                            SwingUtilities.invokeLater(() -> {
                                GUIVisualizer visualizer = new GUIVisualizer(solution);
                                visualizer.setVisible(true);
                            });
                        } else {
                            CLIVisualizer.displaySolution(solution, currentBoard.getExitDirection(),
                                    currentBoard.getExitPosition().getRow(),
                                    currentBoard.getExitPosition().getCol());
                        }
                    }
                    
                    // tanya apakah ingin mengulang program
                    System.out.print("\nIngin mengulang kembali program? (ya/tidak): ");
                    String repeat = scanner.nextLine().toLowerCase().trim();
                    
                    if (repeat.equals("tidak") || repeat.equals("no") || repeat.equals("n")) {
                        continueProgram = false;
                        
                        // tanya simpan solusi hanya ketika user tidak ingin mengulang
                        if (solution != null) {
                            System.out.print("Simpan solusi ke file? (y/n): ");
                            String save = scanner.nextLine().toLowerCase().trim();
                            if (save.equals("y") || save.equals("yes")) {
                                saveSolutionToFile(solution, pathFinder, totalTime);
                            }
                        }
                        
                        System.out.println("Terima kasih telah menggunakan Rush Hour Puzzle Solver!");
                    } else {
                        // jika ya, tanya apakah ingin menggunakan testcase yang sama
                        System.out.print("Ingin mengulang dengan testcase yang sama? (ya/tidak): ");
                        String sameTestcase = scanner.nextLine().toLowerCase().trim();
                        
                        if (sameTestcase.equals("tidak") || sameTestcase.equals("no") || sameTestcase.equals("n")) {
                            // reset board untuk input file baru
                            currentBoard = null;
                            currentFilePath = null;
                            System.out.println("\n" + "=".repeat(50));
                            System.out.println("Memulai sesi baru dengan testcase berbeda...\n");
                        } else {
                            // gunakan testcase yang sama
                            System.out.println("\n" + "=".repeat(50));
                            System.out.println("Memulai sesi baru dengan testcase yang sama...\n");
                        }
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error saat eksekusi: " + e.getMessage());
                    
                    // jika error, tanya apakah ingin mengulang
                    System.out.print("\nTerjadi error. Ingin mengulang kembali program? (ya/tidak): ");
                    String repeat = scanner.nextLine().toLowerCase().trim();
                    
                    if (repeat.equals("tidak") || repeat.equals("no") || repeat.equals("n")) {
                        continueProgram = false;
                    } else {
                        // jika ada error, reset board untuk keamanan
                        currentBoard = null;
                        currentFilePath = null;
                    }
                }
            }
            // close scanner hanya di akhir program
        }
        System.out.println("Program selesai.");
    }
    
    private static void saveSolutionToFile(List<Node> solution, PathFinder pathFinder, long totalTime) {
        try {
            String filename = "solution_" + System.currentTimeMillis() + ".txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                writer.println("=== RUSH HOUR PUZZLE SOLUTION ===");
                writer.println("Algoritma: " + pathFinder.getAlgorithmName());
                writer.println("Jumlah node yang dikunjungi: " + pathFinder.getNodesVisited());
                writer.println("Waktu eksekusi algoritma: " + pathFinder.getExecutionTime() + " ms");
                writer.println("Total waktu program: " + totalTime + " ms");
                writer.println("Jumlah langkah: " + (solution.size() - 1));
                writer.println();
                
                writer.println("Papan Awal");
                writer.println(solution.get(0).getBoard().toString());
                writer.println();
                
                for (int i = 1; i < solution.size(); i++) {
                    Node node = solution.get(i);
                    writer.println("Gerakan " + i + ": " + node.getMove());
                    writer.println(node.getBoard().toString());
                    writer.println();
                }
            }
            System.out.println("Solusi disimpan ke file: " + filename);
            
        } catch (IOException e) {
            System.err.println("Error menyimpan file: " + e.getMessage());
        }
    }
}