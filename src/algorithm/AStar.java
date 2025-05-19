package algorithm;

import algorithm.heuristics.Heuristic;
import entity.Board;
import entity.Move;
import entity.Node;
import java.util.*;

public class AStar implements PathFinder {
    private final Heuristic heuristic;
    private int nodesVisited;
    private long executionTime;
    
    public AStar(Heuristic heuristic) {
        this.heuristic = heuristic;
    }
    
    @Override
    public List<Node> findPath(Board initialBoard) {
        long startTime = System.currentTimeMillis();
        nodesVisited = 0;
        
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        Set<String> explored = new HashSet<>();
        
        int initialHeuristic = heuristic.calculate(initialBoard);
        
        Node initialNode = new Node(initialBoard, null, null, 0, initialHeuristic, 2);
        frontier.add(initialNode);
        
        while (!frontier.isEmpty()) {
            Node current = frontier.poll();
            nodesVisited++;
            
            if (current.getBoard().isGoalState()) {
                executionTime = System.currentTimeMillis() - startTime;
                return reconstructPath(current);
            }
            
            String boardString = current.getBoard().toString();
            if (explored.contains(boardString)) {
                continue;
            }
            explored.add(boardString);
            
            for (Move move : current.getBoard().getPossibleMoves()) {
                Board newBoard = current.getBoard().copy();
                if (newBoard.movePiece(move.getPieceId(), move.getDirection())) {
                    int h = heuristic.calculate(newBoard);
                    Node child = new Node(newBoard, current, move, current.getCost() + 1, h, 2);
                    frontier.add(child);
                }
            }
        }
        
        executionTime = System.currentTimeMillis() - startTime;
        return null; // No solution found
    }
    
    private List<Node> reconstructPath(Node goal) {
        List<Node> path = new ArrayList<>();
        Node current = goal;
        
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }
        
        Collections.reverse(path);
        return path;
    }
    
    @Override
    public int getNodesVisited() {
        return nodesVisited;
    }
    
    @Override
    public long getExecutionTime() {
        return executionTime;
    }
    
    @Override
    public String getAlgorithmName() {
        return "A* Search (" + heuristic.getName() + ")";
    }
}