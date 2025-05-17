package entity;

public class Move {
    private char pieceId;
    private Direction direction;
    
    public Move(char pieceId, Direction direction) {
        this.pieceId = pieceId;
        this.direction = direction;
    }
    
    public char getPieceId() {
        return pieceId;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    @Override
    public String toString() {
        return pieceId + "-" + direction.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return pieceId == move.pieceId && direction == move.direction;
    }
    
    @Override
    public int hashCode() {
        return 31 * pieceId + direction.hashCode();
    }
}