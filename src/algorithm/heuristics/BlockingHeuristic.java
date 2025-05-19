package algorithm.heuristics;

import entity.Board;
import entity.Orientation;
import entity.Piece;
import entity.Position;

public class BlockingHeuristic implements Heuristic {
    @Override
    public int calculate(Board board) {
        char[][] grid = board.getGrid();
        Position exitPos = board.getExitPosition();
        Piece primaryPiece = board.getPieces().get(board.getPrimaryPieceId());
        int blockingCount = 0;
        
        if (primaryPiece.getOrientation() == Orientation.HORIZONTAL) {
            // Count pieces between the primary piece and the exit (horizontal)
            int row = primaryPiece.getPositions().get(0).getRow();
            int startCol = primaryPiece.getBackPosition().getCol() + 1;
            int endCol = exitPos.getCol();
            
            if (startCol > endCol) {
                // Exit is on the left
                startCol = exitPos.getCol();
                endCol = primaryPiece.getFrontPosition().getCol() - 1;
            }
            
            for (int col = startCol; col <= endCol; col++) {
                if (grid[row][col] != '.' && grid[row][col] != 'K') {
                    blockingCount++;
                }
            }
        } else {
            // Count pieces between the primary piece and the exit (vertical)
            int col = primaryPiece.getPositions().get(0).getCol();
            int startRow = primaryPiece.getBackPosition().getRow() + 1;
            int endRow = exitPos.getRow();
            
            if (startRow > endRow) {
                // Exit is above
                startRow = exitPos.getRow();
                endRow = primaryPiece.getFrontPosition().getRow() - 1;
            }
            
            for (int row = startRow; row <= endRow; row++) {
                if (grid[row][col] != '.' && grid[row][col] != 'K') {
                    blockingCount++;
                }
            }
        }
        
        return blockingCount;
    }
    
    @Override
    public String getName() {
        return "Blocking Vehicles";
    }
}