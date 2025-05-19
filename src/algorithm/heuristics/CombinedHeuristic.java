package algorithm.heuristics;

import entity.Board;

public class CombinedHeuristic implements Heuristic {
    private final BlockingHeuristic blockingHeuristic;
    private final ManhattanHeuristic manhattanHeuristic;
    
    public CombinedHeuristic() {
        this.blockingHeuristic = new BlockingHeuristic();
        this.manhattanHeuristic = new ManhattanHeuristic();
    }
    
    @Override
    public int calculate(Board board) {
        // Combine both heuristics with weights
        int blocking = blockingHeuristic.calculate(board);
        int manhattan = manhattanHeuristic.calculate(board);
        
        // Blocking pieces are more important as they directly block the path
        return blocking * 2 + manhattan;
    }
    
    @Override
    public String getName() {
        return "Combined (Blocking + Manhattan)";
    }
}