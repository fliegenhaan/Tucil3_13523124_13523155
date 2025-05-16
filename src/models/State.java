package models;

import java.util.ArrayList;
import java.util.List;

public class State implements Comparable<State> {
    private final Board board;
    private final State parent;
    private final Move moveFromParent;
    private final int cost;
    private double heuristic;
    private double totalCost;

    // konstruktor untuk state awal
    public State(Board board) {
        this.board = board;
        this.parent = null;
        this.moveFromParent = null;
        this.cost = 0;
        this.heuristic = 0;
        this.totalCost = 0;
    }

    // konstruktor untuk state hasil dari state parent
    public State(Board board, State parent, Move moveFromParent) {
        this.board = board;
        this.parent = parent;
        this.moveFromParent = moveFromParent;
        this.cost = parent.getCost() + 1; // setiap gerakan memiliki cost 1
        this.heuristic = 0;
        this.totalCost = this.cost;
    }

    // getters
    public Board getBoard() {
        return board;
    }

    public State getParent() {
        return parent;
    }

    public Move getMoveFromParent() {
        return moveFromParent;
    }

    public int getCost() {
        return cost;
    }

    public double getHeuristic() {
        return heuristic;
    }

    public double getTotalCost() {
        return totalCost;
    }

    // setters
    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
        // update totalCost setelah mengubah heuristic
        this.totalCost = this.cost + this.heuristic;
    }

    // mendapatkan jalur dari root ke state ini
    public List<Move> getPath() {
        List<Move> path = new ArrayList<>();
        State current = this;
        
        while (current.parent != null) {
            path.add(0, current.moveFromParent);
            current = current.parent;
        }
        
        return path;
    }

    // mendapatkan semua board dalam jalur
    public List<Board> getBoardsInPath() {
        List<Board> boards = new ArrayList<>();
        State current = this;
        
        while (current != null) {
            boards.add(0, current.board);
            current = current.parent;
        }
        
        return boards;
    }

    // implementasi comparable untuk prioritas queue
    @Override
    public int compareTo(State other) {
        if (this.totalCost < other.totalCost) {
            return -1;
        } else if (this.totalCost > other.totalCost) {
            return 1;
        } else {
            return 0;
        }
    }

    // untuk digunakan sebagai key di hashmap/set
    @Override
    public int hashCode() {
        return board.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        State other = (State) obj;
        return board.equals(other.board);
    }
}