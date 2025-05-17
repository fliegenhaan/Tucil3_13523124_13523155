package entity;

public class Node implements Comparable<Node> {
    private Board board;
    private Node parent;
    private Move move;
    private int cost;
    private int heuristic;
    private int algorithm; // 0 = UCS, 1 = Greedy, 2 = A*
    
    public Node(Board board, Node parent, Move move, int cost, int heuristic, int algorithm) {
        this.board = board;
        this.parent = parent;
        this.move = move;
        this.cost = cost;
        this.heuristic = heuristic;
        this.algorithm = algorithm;
    }
    
    public Board getBoard() {
        return board;
    }
    
    public Node getParent() {
        return parent;
    }
    
    public Move getMove() {
        return move;
    }
    
    public int getCost() {
        return cost;
    }
    
    public int getHeuristic() {
        return heuristic;
    }
    
    public int getTotalCost() {
        return cost + heuristic;
    }
    
    @Override
    public int compareTo(Node other) {
        switch (algorithm) {
            case 0: // UCS - membandingkan cost
                return Integer.compare(this.cost, other.cost);
            case 1: // Greedy - membandingkan heuristic
                return Integer.compare(this.heuristic, other.heuristic);
            case 2: // A* - membandingkan f(n) = g(n) + h(n)
                return Integer.compare(this.getTotalCost(), other.getTotalCost());
            default:
                return Integer.compare(this.cost, other.cost);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Node other = (Node) obj;
        return board.equals(other.board);
    }
    
    @Override
    public int hashCode() {
        return board.hashCode();
    }
}