package visualizer;

import entity.Board;
import entity.Direction;
import entity.Move;
import entity.Node;

public class CLIVisualizer {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    // private static final String BLUE = "\u001B[34m";
    // private static final String PURPLE = "\u001B[35m";
    // private static final String CYAN = "\u001B[36m";
    
    public static void displaySolution(java.util.List<Node> path, Direction exitDirection, int exitRow, int exitCol) {
        if (path == null || path.isEmpty()) {
            System.out.println("No solution found!");
            return;
        }
        
        System.out.println("Papan Awal");
        displayBoard(path.get(0).getBoard(), null, exitDirection, exitRow, exitCol);
        
        for (int i = 1; i < path.size(); i++) {
            Node node = path.get(i);
            System.out.println("Gerakan " + i + ": " + node.getMove());
            displayBoard(node.getBoard(), node.getMove(), exitDirection, exitRow, exitCol);
        }
    }
    
    public static void displayBoard(Board board, Move lastMove, Direction exitDirection, int exitRow, int exitCol) {
        char[][] grid = board.getGrid();
        char primaryPieceId = board.getPrimaryPieceId();
        // Position exitPos = board.getExitPosition();
        int height = grid.length;
        int width = grid[0].length;
        
        char lastMovePieceId = lastMove != null ? lastMove.getPieceId() : ' ';
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                char cell = grid[i][j];
                
                if (cell == primaryPieceId) {
                    System.out.print(RED + cell + RESET);
                } else if (cell == '.') {
                    System.out.print(cell);
                } else if (cell == lastMovePieceId) {
                    System.out.print(YELLOW + cell + RESET);
                } else {
                    System.out.print(cell);
                }
            }
            
            if (exitDirection == Direction.RIGHT && i == exitRow) {
                System.out.print(GREEN + "K" + RESET);
            }
            
            System.out.println();
        }
        
        if (exitDirection == Direction.DOWN) {
            for (int j = 0; j < width; j++) {
                if (j == exitCol) {
                    System.out.print(GREEN + "K" + RESET);
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}