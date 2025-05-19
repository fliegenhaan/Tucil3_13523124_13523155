package algorithm.heuristics;

import entity.Board;
import entity.Orientation;
import entity.Piece;
import entity.Position;

public class ManhattanHeuristic implements Heuristic {
    @Override
    public int calculate(Board board) {
        Position exitPos = board.getExitPosition();
        Piece primaryPiece = board.getPieces().get(board.getPrimaryPieceId());
        
        if (primaryPiece.getOrientation() == Orientation.HORIZONTAL) {
            // Calculate Manhattan distance horizontally
            int pieceCol = primaryPiece.getBackPosition().getCol();
            if (exitPos.getCol() < pieceCol) {
                // Exit is on the left
                pieceCol = primaryPiece.getFrontPosition().getCol();
            }
            
            return Math.abs(pieceCol - exitPos.getCol());
        } else {
            // Calculate Manhattan distance vertically
            int pieceRow = primaryPiece.getBackPosition().getRow();
            if (exitPos.getRow() < pieceRow) {
                // Exit is above
                pieceRow = primaryPiece.getFrontPosition().getRow();
            }
            
            return Math.abs(pieceRow - exitPos.getRow());
        }
    }
    
    @Override
    public String getName() {
        return "Manhattan Distance";
    }
}