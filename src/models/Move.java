package models;


public class Move {
    private final char pieceId;
    private final String direction;

    public Move(char pieceId, String direction) {
        this.pieceId = pieceId;
        this.direction = direction;
    }

    public char getPieceId() {
        return pieceId;
    }

    public String getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return pieceId + "-" + direction;
    }
}