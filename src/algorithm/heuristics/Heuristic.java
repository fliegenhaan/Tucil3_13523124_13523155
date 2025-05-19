package algorithm.heuristics;

import entity.Board;

public interface Heuristic {
    int calculate(Board board);
    String getName();
}