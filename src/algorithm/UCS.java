package algorithm;

import java.util.*;

import entity.Board;
import entity.Move;
import entity.Node;

public class UCS implements PathFinder {
    private int nodesVisited;
    private long executionTime;
    
    @Override
    public List<Node> findPath(Board initialBoard) {
        long startTime = System.currentTimeMillis();
        nodesVisited = 0;
        
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        Set<String> explored = new HashSet<>();
        
        Node initialNode = new Node(initialBoard, null, null, 0, 0, 0);
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
                    Node child = new Node(newBoard, current, move, current.getCost() + 1, 0, 0);
                    frontier.add(child);
                }
            }
        }
        
        executionTime = System.currentTimeMillis() - startTime;
        return null;
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
        return "Uniform Cost Search";
    }
}