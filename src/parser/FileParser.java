package parser;

import java.io.*;
import java.util.*;

import entity.Board;
import entity.Direction;
import entity.Orientation;
import entity.Piece;
import entity.Position;
import entity.WallPosition;

public class FileParser {
    public static Board parseFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File tidak ditemukan: " + filePath);
        }
        
        BufferedReader dimReader = new BufferedReader(new FileReader(file));
        String dimensionLine = dimReader.readLine();
        String[] dimensions = dimensionLine.split(" ");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);
        String countLine = dimReader.readLine();
        int pieceCount = Integer.parseInt(countLine);
        dimReader.close();
        
        System.out.println("Dimensi board: " + width + "x" + height);
        System.out.println("Jumlah piece non-primary: " + pieceCount);
        
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int lineCount = 0;
        boolean foundK = false;
        int kRow = -1, kCol = -1;
        Direction exitDirection = null;
        List<String> gridLines = new ArrayList<>();
        
        reader.readLine();
        reader.readLine();
        
        while ((line = reader.readLine()) != null && gridLines.size() < height) {
            gridLines.add(line);
            System.out.println("Baris " + lineCount + ": " + line);
            
            if (line.length() > width && line.charAt(width) == 'K') {
                foundK = true;
                kRow = lineCount;
                kCol = width;
                exitDirection = Direction.RIGHT;
                System.out.println("  K ditemukan di dinding kanan: baris " + lineCount + ", kolom " + width);
            }
            
            for (int i = 0; i < Math.min(line.length(), width); i++) {
                if (line.charAt(i) == 'K') {
                    System.out.println("  WARNING: K ditemukan di dalam papan: baris " + lineCount + ", kolom " + i);
                    System.out.println("  K seharusnya berada di dinding, bukan di dalam papan!");
                }
            }
            
            lineCount++;
        }
        
        if (!foundK && gridLines.size() == height && reader.ready()) {
            line = reader.readLine();
            System.out.println("Baris dinding bawah: " + line);
            
            for (int i = 0; i < Math.min(line.length(), width); i++) {
                if (line.charAt(i) == 'K') {
                    foundK = true;
                    kRow = height;
                    kCol = i;
                    exitDirection = Direction.DOWN;
                    System.out.println("  K ditemukan di dinding bawah: baris " + height + ", kolom " + i);
                }
            }
        }
        
        reader.close();
        
        if (!foundK) {
            System.out.println("PERINGATAN: K tidak ditemukan di dinding! Mencoba menentukan posisi exit...");
            
            boolean foundP = false;
            Orientation pOrientation = null;
            int pRow = -1, pCol = -1;
            
            for (int i = 0; i < Math.min(gridLines.size(), height); i++) {
                line = gridLines.get(i);
                for (int j = 0; j < Math.min(line.length(), width); j++) {
                    if (line.charAt(j) == 'P') {
                        if (!foundP) {
                            pRow = i;
                            pCol = j;
                            foundP = true;
                        } else {
                            if (i == pRow) {
                                pOrientation = Orientation.HORIZONTAL;
                            } else if (j == pCol) {
                                pOrientation = Orientation.VERTICAL;
                            }
                        }
                    }
                }
            }
            
            if (foundP && pOrientation != null) {
                if (pOrientation == Orientation.HORIZONTAL) {
                    kRow = pRow;
                    kCol = width;
                    exitDirection = Direction.RIGHT;
                } else {
                    kRow = height;
                    kCol = pCol;
                    exitDirection = Direction.DOWN;
                }
                System.out.println("Menempatkan K di dinding " + exitDirection + ": [" + kRow + "," + kCol + "]");
            } else {
                kRow = height - 1;
                kCol = width;
                exitDirection = Direction.RIGHT;
                System.out.println("Fallback: menempatkan K di dinding kanan: [" + kRow + "," + kCol + "]");
            }
        }
        
        char[][] grid = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = '.';
            }
        }
        
        for (int i = 0; i < Math.min(gridLines.size(), height); i++) {
            line = gridLines.get(i);
            for (int j = 0; j < Math.min(line.length(), width); j++) {
                char cell = line.charAt(j);
                if (cell != '.' && cell != 'K') {
                    grid[i][j] = cell;
                }
            }
        }
        
        Map<Character, List<Position>> pieces = new HashMap<>();
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                char cell = grid[i][j];
                if (cell != '.') {
                    pieces.putIfAbsent(cell, new ArrayList<>());
                    pieces.get(cell).add(new Position(i, j));
                }
            }
        }
        
        Board board = new Board(width, height);
        board.setExitDirection(exitDirection);
        
        Position exitPosition;
        if (exitDirection == Direction.RIGHT) {
            exitPosition = new WallPosition(kRow, width - 1, Direction.RIGHT);
        } else if (exitDirection == Direction.DOWN) {
            exitPosition = new WallPosition(height - 1, kCol, Direction.DOWN);
        } else {
            exitPosition = new WallPosition(height - 1, width - 1, Direction.RIGHT);
        }
        
        board.setExitPosition(exitPosition);
        System.out.println("Exit diposisikan di dinding " + exitDirection + " dekat sel [" + 
                          exitPosition.getRow() + "," + exitPosition.getCol() + "]");
        for (Map.Entry<Character, List<Position>> entry : pieces.entrySet()) {
            char id = entry.getKey();
            List<Position> positions = entry.getValue();
            boolean isPrimary = (id == 'P');
            
            if (positions.size() > 0) {
                Piece piece = new Piece(id, positions, isPrimary);
                board.addPiece(piece);
                System.out.println("Added piece: " + id + " (primary: " + isPrimary + 
                                  ", size: " + positions.size() + 
                                  ", orientation: " + piece.getOrientation() + ")");
            }
        }
        
        printBoardState(grid, height, width, exitDirection, kRow, kCol);
        
        return board;
    }
    
    private static void printBoardState(char[][] grid, int height, int width, Direction exitDirection, int kRow, int kCol) {
        System.out.println("\nBoard state setelah parsing:");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(grid[i][j]);
            }
            if (exitDirection == Direction.RIGHT && kRow == i) {
                System.out.print("K");
            }
            System.out.println();
        }
        
        if (exitDirection == Direction.DOWN) {
            for (int j = 0; j < width; j++) {
                if (j == kCol) {
                    System.out.print("K");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}