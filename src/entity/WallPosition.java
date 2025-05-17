package entity;

public class WallPosition extends Position {
    private Direction direction;
    
    public WallPosition(int row, int col, Direction direction) {
        super(row, col);
        this.direction = direction;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    @Override
    public String toString() {
        return "Wall(" + getRow() + "," + getCol() + "," + direction + ")";
    }
}