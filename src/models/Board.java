package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Board {
    private final int width;
    private final int height;
    private final char[][] grid;
    private final List<Piece> pieces;
    private Piece primaryPiece;
    private int exitRow;
    private int exitCol;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new char[height][width];
        this.pieces = new ArrayList<>();
        
        // inisialisasi grid dengan cell kosong
        for (int i = 0; i < height; i++) {
            Arrays.fill(grid[i], '.');
        }
    }

    public Board(Board other) {
        this.width = other.width;
        this.height = other.height;
        this.grid = new char[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(other.grid[i], 0, this.grid[i], 0, width);
        }

        // deep copy pieces
        this.pieces = new ArrayList<>();
        for (Piece p : other.pieces) {
            Piece newPiece = new Piece(p);
            this.pieces.add(newPiece);
            if (p.isPrimary()) {
                this.primaryPiece = newPiece;
            }
        }
        
        this.exitRow = other.exitRow;
        this.exitCol = other.exitCol;
    }

    // getters
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public char[][] getGrid() {
        return grid;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public Piece getPrimaryPiece() {
        return primaryPiece;
    }

    public int getExitRow() {
        return exitRow;
    }

    public int getExitCol() {
        return exitCol;
    }

    public void setExit(int row, int col) {
        this.exitRow = row;
        this.exitCol = col;
        grid[row][col] = 'K';
    }

    public void addPiece(Piece piece) {
        pieces.add(piece);
        if (piece.isPrimary()) {
            this.primaryPiece = piece;
        }

        updateGrid();
    }

    public void updateGrid() {
        // reset grid
        for (int i = 0; i < height; i++) {
            Arrays.fill(grid[i], '.');
        }
        
        // set exit
        grid[exitRow][exitCol] = 'K';
        
        // set pieces
        for (Piece piece : pieces) {
            char id = piece.getId();
            if (piece.isHorizontal()) {
                for (int c = piece.getCol(); c <= piece.getEndCol(); c++) {
                    grid[piece.getRow()][c] = id;
                }
            } else {
                for (int r = piece.getRow(); r <= piece.getEndRow(); r++) {
                    grid[r][piece.getCol()] = id;
                }
            }
        }
    }

    public Piece getPieceById(char id) {
        for (Piece piece : pieces) {
            if (piece.getId() == id) {
                return piece;
            }
        }
        return null;
    }

    public Piece getPieceAt(int row, int col) {
        for (Piece piece : pieces) {
            if (piece.getRow() <= row && row <= piece.getEndRow() &&
                piece.getCol() <= col && col <= piece.getEndCol()) {
                return piece;
            }
        }
        return null;
    }

    public boolean isCellEmpty(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return false;
        }
        return grid[row][col] == '.';
    }
    
    // mengubah posisi piece
    public void movePiece(Piece piece, String direction) {
        piece.move(direction);
        updateGrid();
    }
    
    // menghasilkan semua state yang mungkin dari board saat ini
    public Map<Board, Move> generatePossibleMoves() {
        Map<Board, Move> possibleStates = new HashMap<>();
        
        for (Piece piece : pieces) {
            String[] directions;
            if (piece.isHorizontal()) {
                directions = new String[]{"kiri", "kanan"};
            } else {
                directions = new String[]{"atas", "bawah"};
            }
            
            for (String direction : directions) {
                if (piece.canMove(direction, this)) {
                    Board newBoard = new Board(this);
                    Piece newPiece = newBoard.getPieceById(piece.getId());
                    
                    newBoard.movePiece(newPiece, direction);
                    Move move = new Move(piece.getId(), direction);
                    
                    possibleStates.put(newBoard, move);
                }
            }
        }
        
        return possibleStates;
    }
    
    // memeriksa apakah puzzle sudah selesai
    public boolean isGoalState() {
        if (primaryPiece == null) {
            return false;
        }
        
        boolean horizontalExit = (exitRow == primaryPiece.getRow() && primaryPiece.isHorizontal());
        boolean verticalExit = (exitCol == primaryPiece.getCol() && !primaryPiece.isHorizontal());
        
        if (!horizontalExit && !verticalExit) {
            return false;
        }
        
        // cek apakah primary piece sudah di posisi exit
        if (horizontalExit) {
            // exit di kanan
            if (exitCol > primaryPiece.getCol()) {
                return primaryPiece.getEndCol() == exitCol - 1;
            } 
            // exit di kiri
            else {
                return primaryPiece.getCol() == exitCol + 1;
            }
        } else {
            // exit di bawah
            if (exitRow > primaryPiece.getRow()) {
                return primaryPiece.getEndRow() == exitRow - 1;
            } 
            // exit di atas
            else {
                return primaryPiece.getRow() == exitRow + 1;
            }
        }
    }

    // menghasilkan string representasi board
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sb.append(grid[i][j]);
            }
            if (i < height - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Board other = (Board) obj;
        return toString().equals(other.toString());
    }
}