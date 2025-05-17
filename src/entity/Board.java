package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private char[][] grid;
    private int width;
    private int height;
    private Map<Character, Piece> pieces;
    private Position exitPosition;
    private char primaryPieceId;
    private Direction exitDirection;
    
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new char[height][width];
        this.pieces = new HashMap<>();

        // inisialisasi grid dengan '.'
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = '.';
            }
        }
    }

    public void setExitDirection(Direction exitDirection) {
        this.exitDirection = exitDirection;
    }

    public Direction getExitDirection() {
        return exitDirection;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public char[][] getGrid() {
        return grid;
    }
    
    public Map<Character, Piece> getPieces() {
        return pieces;
    }
    
    public Position getExitPosition() {
        return exitPosition;
    }
    
    public char getPrimaryPieceId() {
        return primaryPieceId;
    }

    public void setExitPosition(Position exitPosition) {
        this.exitPosition = exitPosition;
        grid[exitPosition.getRow()][exitPosition.getCol()] = 'K';

        if (exitPosition instanceof WallPosition) {
            this.exitDirection = ((WallPosition) exitPosition).getDirection();
        }
    }
    
    public void addPiece(Piece piece) {
        char id = piece.getId();
        pieces.put(id, piece);
        
        if (piece.isPrimary()) {
            primaryPieceId = id;
        }
        
        // update grid
        for (Position pos : piece.getPositions()) {
            if (pos.getRow() >= 0 && pos.getRow() < height && 
                pos.getCol() >= 0 && pos.getCol() < width) {
                grid[pos.getRow()][pos.getCol()] = id;
            }
        }
    }
    
    public boolean movePiece(char id, Direction direction) {
        Piece piece = pieces.get(id);
        if (piece == null || !piece.canMove(direction, grid, width, height)) {
            return false;
        }
        
        for (Position pos : piece.getPositions()) {
            if (pos.getRow() >= 0 && pos.getRow() < height && 
                pos.getCol() >= 0 && pos.getCol() < width) {
                grid[pos.getRow()][pos.getCol()] = '.';
            }
        }
        
        piece.move(direction);
        
        // update grid dengan posisi baru
        for (Position pos : piece.getPositions()) {
            // skip jika posisi adalah exit
            // atau jika posisi adalah dinding
            if (exitPosition != null && pos.equals(exitPosition)) {
                continue;
            }
            
            if (pos.getRow() >= 0 && pos.getRow() < height && 
                pos.getCol() >= 0 && pos.getCol() < width) {
                grid[pos.getRow()][pos.getCol()] = id;
            }
        }
        
        return true;
    }
    
    public boolean isGoalState() {
        Piece primaryPiece = pieces.get(primaryPieceId);
        
        if (exitPosition == null) {
            return false;
        }
        
        Direction direction = exitDirection;
        if (direction == null) {
            if (exitPosition instanceof WallPosition) {
                direction = ((WallPosition) exitPosition).getDirection();
            } else {
                if (exitPosition.getCol() == width - 1) {
                    direction = Direction.RIGHT;
                } else if (exitPosition.getRow() == height - 1) {
                    direction = Direction.DOWN;
                } else if (exitPosition.getCol() == 0) {
                    direction = Direction.LEFT;
                } else if (exitPosition.getRow() == 0) {
                    direction = Direction.UP;
                }
            }
        }
        
        if (direction == Direction.RIGHT) {
            // primary piece harus horizontal
            if (primaryPiece.getOrientation() != Orientation.HORIZONTAL) {
                return false;
            }
            
            int rightCol = -1;
            int pieceRow = -1;
            for (Position pos : primaryPiece.getPositions()) {
                if (pos.getCol() > rightCol) {
                    rightCol = pos.getCol();
                    pieceRow = pos.getRow();
                }
            }
            
            if (pieceRow != exitPosition.getRow()) {
                return false;
            }
            
            for (int col = rightCol + 1; col < width; col++) {
                if (grid[pieceRow][col] != '.') {
                    return false;
                }
            }
            
            return true;
        } 
        else if (direction == Direction.DOWN) {
            // primary piece harus vertical
            if (primaryPiece.getOrientation() != Orientation.VERTICAL) {
                return false;
            }
            
            int bottomRow = -1;
            int pieceCol = -1;
            for (Position pos : primaryPiece.getPositions()) {
                if (pos.getRow() > bottomRow) {
                    bottomRow = pos.getRow();
                    pieceCol = pos.getCol();
                }
            }
            
            if (pieceCol != exitPosition.getCol()) {
                return false;
            }
            
            for (int row = bottomRow + 1; row < height; row++) {
                if (grid[row][pieceCol] != '.') {
                    return false;
                }
            }
            
            return true;
        }
        else if (direction == Direction.LEFT) {
            if (primaryPiece.getOrientation() != Orientation.HORIZONTAL) {
                return false;
            }
            
            int leftCol = width;
            int pieceRow = -1;
            for (Position pos : primaryPiece.getPositions()) {
                if (pos.getCol() < leftCol) {
                    leftCol = pos.getCol();
                    pieceRow = pos.getRow();
                }
            }
            
            if (pieceRow != exitPosition.getRow()) {
                return false;
            }
            
            for (int col = leftCol - 1; col >= 0; col--) {
                if (grid[pieceRow][col] != '.') {
                    return false;
                }
            }
            
            return true;
        }
        else if (direction == Direction.UP) {
            // primary piece harus vertical
            if (primaryPiece.getOrientation() != Orientation.VERTICAL) {
                return false;
            }
            
            int topRow = height;
            int pieceCol = -1;
            for (Position pos : primaryPiece.getPositions()) {
                if (pos.getRow() < topRow) {
                    topRow = pos.getRow();
                    pieceCol = pos.getCol();
                }
            }
            
            if (pieceCol != exitPosition.getCol()) {
                return false;
            }
            
            for (int row = topRow - 1; row >= 0; row--) {
                if (grid[row][pieceCol] != '.') {
                    return false;
                }
            }
        
            return true;
        }
        
        return false;
    }
    
    public List<Move> getPossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();
        
        for (char id : pieces.keySet()) {
            for (Direction direction : Direction.values()) {
                if (pieces.get(id).canMove(direction, grid, width, height)) {
                    possibleMoves.add(new Move(id, direction));
                }
            }
        }
        
        return possibleMoves;
    }
    
    public Board copy() {
        Board copy = new Board(width, height);
        
        for (int i = 0; i < height; i++) {
            System.arraycopy(grid[i], 0, copy.grid[i], 0, width);
        }
        
        for (Map.Entry<Character, Piece> entry : pieces.entrySet()) {
            copy.pieces.put(entry.getKey(), entry.getValue().copy());
        }
        
        copy.exitPosition = exitPosition;
        copy.primaryPieceId = primaryPieceId;
        copy.exitDirection = exitDirection;
        
        return copy;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Board other = (Board) obj;
        if (width != other.width || height != other.height) return false;
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (grid[i][j] != other.grid[i][j]) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result = 31 * result + grid[i][j];
            }
        }
        return result;
    }
    
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
}