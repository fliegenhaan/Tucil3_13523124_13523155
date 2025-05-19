package parser;

import entity.Board;
import entity.Direction;
import entity.Orientation;
import entity.Piece;
import entity.Position;
import entity.WallPosition;
import java.io.*;
import java.util.*;

public class FileParser {
    public static Board parseFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File tidak ditemukan: " + filePath);
        }
        
        int boardWidth;
        int boardHeight;
        int pieceCount;
        try (BufferedReader dimReader = new BufferedReader(new FileReader(file))) {
            String dimensionLine = dimReader.readLine();
            String[] dimensions = dimensionLine.split(" ");
            boardWidth = Integer.parseInt(dimensions[0]);
            boardHeight = Integer.parseInt(dimensions[1]);
            String countLine = dimReader.readLine();
            pieceCount = Integer.parseInt(countLine);
        }
        
        System.out.println("Dimensi board: " + boardWidth + "x" + boardHeight);
        System.out.println("Jumlah piece non-primary: " + pieceCount);
        
        // baca file dengan dimensi diperluas (width+1) x (height+1)
        int extendedWidth = boardWidth + 1;
        int extendedHeight = boardHeight + 1;
        char[][] extendedGrid = new char[extendedHeight][extendedWidth];
        
        for (int i = 0; i < extendedHeight; i++) {
            for (int j = 0; j < extendedWidth; j++) {
                extendedGrid[i][j] = '.';
            }
        }
        
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            reader.readLine();
            lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        
        for (int i = 0; i < lines.size() && i < extendedHeight; i++) {
            String currentLine = lines.get(i);
            for (int j = 0; j < currentLine.length() && j < extendedWidth; j++) {
                extendedGrid[i][j] = currentLine.charAt(j);
            }
        }
        
        // mencari K di semua posisi dinding
        Position exitPosition = null;
        Direction exitDirection = null;
        int validRowStart = 0;
        int validColStart = 0;
        
        for (int j = 0; j < extendedWidth; j++) {
            if (extendedGrid[0][j] == 'K') {
                exitPosition = new WallPosition(0, j, Direction.UP);
                exitDirection = Direction.UP;
                validRowStart = 1;
                validColStart = 0;
                System.out.println("K ditemukan di dinding atas: [0," + j + "]");
                break;
            }
        }
        
        if (exitPosition == null) {
            for (int j = 0; j < extendedWidth; j++) {
                if (extendedGrid[boardHeight][j] == 'K') {
                    exitPosition = new WallPosition(boardHeight - 1, j, Direction.DOWN);
                    exitDirection = Direction.DOWN;
                    validRowStart = 0;
                    validColStart = 0;
                    System.out.println("K ditemukan di dinding bawah: [" + boardHeight + "," + j + "]");
                    break;
                }
            }
        }
        
        if (exitPosition == null) {
            for (int i = 0; i < extendedHeight; i++) {
                if (extendedGrid[i][0] == 'K') {
                    exitPosition = new WallPosition(i, 0, Direction.LEFT);
                    exitDirection = Direction.LEFT;
                    validRowStart = 0;
                    validColStart = 1;
                    System.out.println("K ditemukan di dinding kiri: [" + i + ",0]");
                    break;
                }
            }
        }
        
        if (exitPosition == null) {
            for (int i = 0; i < extendedHeight; i++) {
                if (extendedGrid[i][boardWidth] == 'K') {
                    exitPosition = new WallPosition(i, boardWidth - 1, Direction.RIGHT);
                    exitDirection = Direction.RIGHT;
                    validRowStart = 0;
                    validColStart = 0;
                    System.out.println("K ditemukan di dinding kanan: [" + i + "," + boardWidth + "]");
                    break;
                }
            }
        }
        
        if (exitPosition == null) {
            throw new IOException("Pintu keluar (K) tidak ditemukan di dinding!");
        }
        
        // ekstrak board yang valid berdasarkan posisi K
        char[][] validGrid = new char[boardHeight][boardWidth];
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                int extendedRow = validRowStart + i;
                int extendedCol = validColStart + j;
                validGrid[i][j] = extendedGrid[extendedRow][extendedCol];
            }
        }
        
        // cari dan analisis primary piece (P)
        List<Position> primaryPositions = new ArrayList<>();
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                if (validGrid[i][j] == 'P') {
                    primaryPositions.add(new Position(i, j));
                }
            }
        }
        
        if (primaryPositions.isEmpty()) {
            throw new IOException("Primary piece (P) tidak ditemukan!");
        }
        
        Orientation primaryOrientation;
        if (primaryPositions.size() == 1) {
            primaryOrientation = Orientation.HORIZONTAL; // asumsi default
        } else {
            Position first = primaryPositions.get(0);
            Position second = primaryPositions.get(1);
            if (first.getRow() == second.getRow()) {
                primaryOrientation = Orientation.HORIZONTAL;
            } else {
                primaryOrientation = Orientation.VERTICAL;
            }
        }
        
        boolean isAligned = validateAlignment(primaryPositions, exitPosition, exitDirection, primaryOrientation);
        if (!isAligned) {
            throw new IOException("Primary piece tidak sejajar dengan pintu keluar!");
        }
        
        Board board = new Board(boardWidth, boardHeight);
        board.setExitDirection(exitDirection);
        board.setExitPosition(exitPosition);
        
        Map<Character, List<Position>> pieces = new HashMap<>();
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                char cell = validGrid[i][j];
                if (cell != '.' && cell != 'K') {
                    pieces.putIfAbsent(cell, new ArrayList<>());
                    pieces.get(cell).add(new Position(i, j));
                }
            }
        }
        
        for (Map.Entry<Character, List<Position>> entry : pieces.entrySet()) {
            char id = entry.getKey();
            List<Position> positions = entry.getValue();
            boolean isPrimary = (id == 'P');
            
            if (!positions.isEmpty()) {
                Piece piece = new Piece(id, positions, isPrimary);
                board.addPiece(piece);
                System.out.println("ditambahkan piece: " + id + " (primary: " + isPrimary + 
                                  ", size: " + positions.size() + 
                                  ", orientasi: " + piece.getOrientation() + ")");
            }
        }
        
        System.out.println("Board berhasil diparse dengan exit di " + exitDirection);
        printBoardState(validGrid, boardHeight, boardWidth, exitDirection, exitPosition);
        
        return board;
    }
    
    private static boolean validateAlignment(List<Position> primaryPositions, Position exitPosition, 
                                           Direction exitDirection, Orientation primaryOrientation) {
        if (primaryOrientation == Orientation.HORIZONTAL) {
            if (exitDirection != Direction.LEFT && exitDirection != Direction.RIGHT) {
                System.out.println("Primary piece horizontal tetapi exit tidak di kiri/kanan");
                return false;
            }
            
            int exitRow = exitPosition.getRow();
            for (Position pos : primaryPositions) {
                if (pos.getRow() != exitRow) {
                    System.out.println("Primary piece tidak sejajar dengan exit secara horizontal");
                    return false;
                }
            }
        } else {
            if (exitDirection != Direction.UP && exitDirection != Direction.DOWN) {
                System.out.println("Primary piece vertikal tetapi exit tidak di atas/bawah");
                return false;
            }
            
            int exitCol = exitPosition.getCol();
            for (Position pos : primaryPositions) {
                if (pos.getCol() != exitCol) {
                    System.out.println("Primary piece tidak sejajar dengan exit secara vertikal");
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private static void printBoardState(char[][] grid, int height, int width, 
                                      Direction exitDirection, Position exitPosition) {
        System.out.println("\nBoard state setelah parsing:");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
        System.out.println("Exit: " + exitDirection + " di posisi " + exitPosition);
    }
}