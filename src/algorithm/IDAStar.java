package algorithm;

import algorithm.heuristics.Heuristic;
import entity.Board;
import entity.Move;
import entity.Node;
import java.util.*;

public class IDAStar implements PathFinder {
    private final Heuristic heuristic;
    private int nodesVisited;
    private long executionTime;
    private final int maxIterations;
    private final long maxTimeMillis;
    private long startTime;
    
    public IDAStar(Heuristic heuristic) {
        this.heuristic = heuristic;
        this.maxIterations = 1000; 
        this.maxTimeMillis = 30000; // 30 detik timeout
    }
    
    @Override
    public List<Node> findPath(Board initialBoard) {
        startTime = System.currentTimeMillis();
        nodesVisited = 0;
        
        int initialHeuristic = heuristic.calculate(initialBoard);
        Node initialNode = new Node(initialBoard, null, null, 0, initialHeuristic, 2);
        
        int threshold = initialHeuristic;
        
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            System.out.println("IDA* iteration with threshold: " + threshold);
            
            // check timeout setiap iterasi, bukan setiap node
            if (System.currentTimeMillis() - startTime > maxTimeMillis) {
                System.out.println("IDA* timeout reached (" + maxTimeMillis/1000 + " seconds)");
                break;
            }
            
            // hanya gunakan path tracking untuk cycle detection
            Map<Board, Integer> pathDepths = new HashMap<>();
            Result result = search(initialNode, 0, threshold, pathDepths);
            
            if (result.node != null) {
                executionTime = System.currentTimeMillis() - startTime;
                return reconstructPath(result.node);
            }
            
            if (result.nextThreshold == Integer.MAX_VALUE) {
                System.out.println("No more nodes to explore");
                break;
            }
            
            // early termination jika threshold jump terlalu besar
            if (result.nextThreshold > threshold + 15) {
                System.out.println("Threshold jump too large (" + 
                                 (result.nextThreshold - threshold) + "), stopping search");
                break;
            }
            
            threshold = result.nextThreshold;
        }
        
        executionTime = System.currentTimeMillis() - startTime;
        return null;
    }
    
    private Result search(Node node, int g, int threshold, Map<Board, Integer> pathDepths) {
        nodesVisited++;
        
        int f = g + node.getHeuristic();
        
        if (f > threshold) {
            return new Result(null, f);
        }
        
        if (node.getBoard().isGoalState()) {
            return new Result(node, -1);
        }
        
        // cycle detection menggunakan board equality, bukan string
        Board currentBoard = node.getBoard();
        Integer previousDepth = pathDepths.get(currentBoard);
        if (previousDepth != null && previousDepth <= g) {
            // sudah visit board ini dengan depth yang sama atau lebih rendah
            return new Result(null, Integer.MAX_VALUE);
        }
        
        pathDepths.put(currentBoard, g);
        
        int minThreshold = Integer.MAX_VALUE;
        
        // IDA* akan naturally explore optimal path karena f-cost cutoff
        List<Move> moves = node.getBoard().getPossibleMoves();
        
        for (Move move : moves) {
            Board newBoard = node.getBoard().copy();
            if (newBoard.movePiece(move.getPieceId(), move.getDirection())) {
                // quick check: skip jika masih di path
                if (pathDepths.containsKey(newBoard)) {
                    continue;
                }
                
                int h = heuristic.calculate(newBoard);
                Node child = new Node(newBoard, node, move, g + 1, h, 2);
                
                Result result = search(child, g + 1, threshold, pathDepths);
                
                if (result.node != null) {
                    // found solution, cleanup dan return
                    pathDepths.remove(currentBoard);
                    return result;
                }
                
                if (result.nextThreshold < minThreshold) {
                    minThreshold = result.nextThreshold;
                }
            }
        }
        
        // cleanup current board dari path
        pathDepths.remove(currentBoard);
        
        return new Result(null, minThreshold);
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
        return "IDA* Search (" + heuristic.getName() + ")";
    }
    
    private static class Result {
        Node node;
        int nextThreshold;
        
        Result(Node node, int nextThreshold) {
            this.node = node;
            this.nextThreshold = nextThreshold;
        }
    }
}