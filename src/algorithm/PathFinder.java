package algorithm;

import java.util.List;

import entity.Board;
import entity.Node;

public interface PathFinder {
    List<Node> findPath(Board initialBoard);
    int getNodesVisited();
    long getExecutionTime();
    String getAlgorithmName();
}