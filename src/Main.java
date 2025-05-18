import java.io.*;
import java.util.*;

import algorithm.AStar;
import algorithm.GreedyBestFirstSearch;
import algorithm.PathFinder;
import algorithm.UCS;
import algorithm.IDAStar;
import algorithm.heuristics.BlockingHeuristic;
import algorithm.heuristics.Heuristic;
import algorithm.heuristics.ManhattanHeuristic;
import algorithm.heuristics.CombinedHeuristic;
import entity.Board;
import entity.Node;
import parser.FileParser;
import visualizer.CLIVisualizer;
import visualizer.GUIVisualizer;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Board board = null;
        
        System.out.println("=== Rush Hour Puzzle Solver ===");
        System.out.println("Implementasi UCS, Greedy Best First Search, A*, dan IDA*");
        System.out.println();
        
        while (board == null) {
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
                board = FileParser.parseFromFile(filePath);
                System.out.println("File berhasil diparse!");
                
            } catch (IOException e) {
                System.err.println("Error membaca file: " + e.getMessage());
                System.out.println("Silakan coba file lain.\n");
            } catch (Exception e) {
                System.err.println("Error parsing: " + e.getMessage());
                System.out.println("Silakan coba file lain.\n");
            }
        }
        
        try {
            System.out.print("\nPilih mode (CLI/GUI): ");
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
                
                switch (heuristicChoice) {
                    case "MANHATTAN":
                        heuristic = new ManhattanHeuristic();
                        break;
                    case "COMBINED":
                        heuristic = new CombinedHeuristic();
                        break;
                    default:
                        heuristic = new BlockingHeuristic();
                        break;
                }
            }
            
            PathFinder pathFinder;
            switch (algorithm) {
                case "GREEDY":
                    pathFinder = new GreedyBestFirstSearch(heuristic);
                    break;
                case "ASTAR":
                    pathFinder = new AStar(heuristic);
                    break;
                case "IDASTAR":
                    pathFinder = new IDAStar(heuristic);
                    break;
                default:
                    pathFinder = new UCS();
                    break;
            }
            
            System.out.println("\nMencari solusi menggunakan " + pathFinder.getAlgorithmName() + "...");
            long startTime = System.currentTimeMillis();
            List<Node> solution = pathFinder.findPath(board);
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
                    CLIVisualizer.displaySolution(solution, board.getExitDirection(), 
                                              board.getExitPosition().getRow(), 
                                              board.getExitPosition().getCol());
                }
                
                System.out.print("\nSimpan solusi ke file? (y/n): ");
                String save = scanner.nextLine().toLowerCase().trim();
                if (save.equals("y") || save.equals("yes")) {
                    saveSolutionToFile(solution, pathFinder, board, totalTime);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error saat eksekusi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    private static void saveSolutionToFile(List<Node> solution, PathFinder pathFinder, Board board, long totalTime) {
        try {
            String filename = "solution_" + System.currentTimeMillis() + ".txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));
            
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
            
            writer.close();
            System.out.println("Solusi disimpan ke file: " + filename);
            
        } catch (IOException e) {
            System.err.println("Error menyimpan file: " + e.getMessage());
        }
    }
}