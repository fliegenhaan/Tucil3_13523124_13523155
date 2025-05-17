package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Board;
import entity.Orientation;
import entity.Piece;
import entity.Position;

public class FileParser {
    public static Board parseFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        
        try {
            String[] dimensions = reader.readLine().split(" ");
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);
            
            System.out.println("Dimensi board: " + width + "x" + height);
            
            int nonPrimaryCount = Integer.parseInt(reader.readLine());
            System.out.println("Jumlah piece non-primary: " + nonPrimaryCount);
            
            Board board = new Board(width, height);
            
            List<String> gridLines = new ArrayList<>();
            for (int i = 0; i < height; i++) {
                String line = reader.readLine();
                gridLines.add(line);
                System.out.println("Baris " + i + ": " + line);
            }
            
            reader.close();
            
            Position exitPosition = null;
            boolean exitInWall = false;
            
            for (int i = 0; i < height; i++) {
                String line = gridLines.get(i);
                for (int j = 0; j < line.length() && j < width; j++) {
                    if (line.charAt(j) == 'K') {
                        exitPosition = new Position(i, j);
                        System.out.println("Exit (K) ditemukan di dalam grid di posisi: [" + i + "," + j + "]");
                        break;
                    }
                }
                if (exitPosition != null) break;
            }
            
            if (exitPosition == null) {
                for (int i = 0; i < height; i++) {
                    String line = gridLines.get(i);
                    if (line.length() > width && line.charAt(width) == 'K') {
                        exitPosition = new Position(i, width - 1);
                        exitInWall = true;
                        System.out.println("Exit (K) ditemukan di dinding kanan di posisi: [" + i + "," + width + "], diatur di [" + i + "," + (width-1) + "]");
                        break;
                    }
                }
                
                if (exitPosition == null && gridLines.size() > height) {
                    String line = gridLines.get(height);
                    for (int j = 0; j < line.length() && j < width; j++) {
                        if (line.charAt(j) == 'K') {
                            exitPosition = new Position(height - 1, j);
                            exitInWall = true;
                            System.out.println("Exit (K) ditemukan di dinding bawah di posisi: [" + height + "," + j + "], diatur di [" + (height-1) + "," + j + "]");
                            break;
                        }
                    }
                }
            }
            
            if (exitPosition == null) {
                System.out.println("PERINGATAN: Exit (K) tidak ditemukan di input file.");
                
                boolean hasPrimaryPiece = false;
                Orientation primaryOrientation = null;
                
                for (int i = 0; i < height; i++) {
                    String line = gridLines.get(i);
                    for (int j = 0; j < line.length() && j < width; j++) {
                        if (line.charAt(j) == 'P') {
                            hasPrimaryPiece = true;
                            if (j + 1 < line.length() && j + 1 < width && line.charAt(j + 1) == 'P') {
                                primaryOrientation = Orientation.HORIZONTAL;
                            } else if (i + 1 < gridLines.size() && i + 1 < height && 
                                      gridLines.get(i + 1).length() > j && gridLines.get(i + 1).charAt(j) == 'P') {
                                primaryOrientation = Orientation.VERTICAL;
                            }
                            break;
                        }
                    }
                    if (hasPrimaryPiece) break;
                }
                
                if (hasPrimaryPiece && primaryOrientation != null) {
                    if (primaryOrientation == Orientation.HORIZONTAL) {
                        exitPosition = new Position(height / 2, width - 1);
                    } else {
                        exitPosition = new Position(height - 1, width / 2);
                    }
                    System.out.println("Fallback: Exit ditempatkan di " + exitPosition + " berdasarkan orientasi primary piece");
                } else {
                    exitPosition = new Position(height - 1, width - 1);
                    System.out.println("Fallback: Exit ditempatkan di pojok kanan bawah " + exitPosition);
                }
            }
            
            board.setExitPosition(exitPosition);
            
            char[][] grid = new char[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    grid[i][j] = '.';
                }
            }
            
            for (int i = 0; i < height; i++) {
                String line = gridLines.get(i);
                for (int j = 0; j < line.length() && j < width; j++) {
                    char cell = line.charAt(j);
                    if (cell != '.' && (cell != 'K' || !exitInWall)) {
                        grid[i][j] = cell;
                    }
                }
            }
            
            Map<Character, List<Position>> piecePositions = new HashMap<>();
            
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    char cell = grid[i][j];
                    if (cell != '.' && cell != 'K') {
                        piecePositions.putIfAbsent(cell, new ArrayList<>());
                        piecePositions.get(cell).add(new Position(i, j));
                    }
                }
            }
            
            System.out.println("Pieces yang ditemukan: " + piecePositions.keySet());
            
            for (Map.Entry<Character, List<Position>> entry : piecePositions.entrySet()) {
                char id = entry.getKey();
                List<Position> positions = entry.getValue();
                boolean isPrimary = (id == 'P');
                
                Piece piece = new Piece(id, positions, isPrimary);
                System.out.println("Menambahkan piece " + id + " dengan ukuran " + positions.size() + 
                                  ", orientasi " + piece.getOrientation() + 
                                  (isPrimary ? " (primary)" : ""));
                board.addPiece(piece);
            }
            
            return board;
        } catch (Exception e) {
            System.err.println("Error membaca file: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}