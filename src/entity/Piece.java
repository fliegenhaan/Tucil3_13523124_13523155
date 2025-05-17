package entity;

import java.util.ArrayList;
import java.util.List;

public class Piece {
    private char id;
    private List<Position> positions;
    private Orientation orientation;
    private boolean isPrimary;
    
    public Piece(char id, List<Position> positions, boolean isPrimary) {
        this.id = id;
        this.positions = new ArrayList<>(positions);
        this.isPrimary = isPrimary;
        
        if (positions.size() > 1) {
            if (positions.get(0).getRow() == positions.get(1).getRow()) {
                this.orientation = Orientation.HORIZONTAL;
            } else {
                this.orientation = Orientation.VERTICAL;
            }
        } else {
            // jika hanya ada satu posisi, anggap horizontal (asumsi)
            this.orientation = Orientation.HORIZONTAL;
        }
    }
    
    public char getId() {
        return id;
    }
    
    public List<Position> getPositions() {
        return new ArrayList<>(positions);
    }
    
    public Orientation getOrientation() {
        return orientation;
    }
    
    public boolean isPrimary() {
        return isPrimary;
    }
    
    public int getSize() {
        return positions.size();
    }
    
    public Position getFrontPosition() {
        if (orientation == Orientation.HORIZONTAL) {
            // ambil posisi paling kiri
            return positions.stream()
                    .min((p1, p2) -> Integer.compare(p1.getCol(), p2.getCol()))
                    .orElse(positions.get(0));
        } else {
            // ambil posisi paling atas
            return positions.stream()
                    .min((p1, p2) -> Integer.compare(p1.getRow(), p2.getRow()))
                    .orElse(positions.get(0));
        }
    }
    
    public Position getBackPosition() {
        if (orientation == Orientation.HORIZONTAL) {
            // ambil posisi paling kanan
            return positions.stream()
                    .max((p1, p2) -> Integer.compare(p1.getCol(), p2.getCol()))
                    .orElse(positions.get(0));
        } else {
            // ambil posisi paling bawah
            return positions.stream()
                    .max((p1, p2) -> Integer.compare(p1.getRow(), p2.getRow()))
                    .orElse(positions.get(0));
        }
    }
    
    public boolean canMove(Direction direction, char[][] grid, int width, int height) {
        if ((orientation == Orientation.HORIZONTAL && (direction == Direction.UP || direction == Direction.DOWN)) ||
            (orientation == Orientation.VERTICAL && (direction == Direction.LEFT || direction == Direction.RIGHT))) {
            return false;
        }
        
        if (direction == Direction.UP) {
            Position front = getFrontPosition();
            Position target = front.offset(direction);
            return target.getRow() >= 0 && grid[target.getRow()][target.getCol()] == '.';
        } else if (direction == Direction.DOWN) {
            Position back = getBackPosition();
            Position target = back.offset(direction);
            return target.getRow() < height && grid[target.getRow()][target.getCol()] == '.';
        } else if (direction == Direction.LEFT) {
            Position front = getFrontPosition();
            Position target = front.offset(direction);
            return target.getCol() >= 0 && grid[target.getRow()][target.getCol()] == '.';
        } else if (direction == Direction.RIGHT) {
            Position back = getBackPosition();
            Position target = back.offset(direction);
            return target.getCol() < width && grid[target.getRow()][target.getCol()] == '.';
        }
        
        return false;
    }
    
    public void move(Direction direction) {
        List<Position> newPositions = new ArrayList<>();
        for (Position pos : positions) {
            newPositions.add(pos.offset(direction));
        }
        positions = newPositions;
    }
    
    public Piece copy() {
        Piece copy = new Piece(id, positions, isPrimary);
        return copy;
    }
}