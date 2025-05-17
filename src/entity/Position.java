package entity;

import java.util.Objects;

public class Position {
    private int row;
    private int col;
    
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public Position offset(Direction direction) {
        switch (direction) {
            case UP:
                return new Position(row - 1, col);
            case DOWN:
                return new Position(row + 1, col);
            case LEFT:
                return new Position(row, col - 1);
            case RIGHT:
                return new Position(row, col + 1);
            default:
                return new Position(row, col);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
    
    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}