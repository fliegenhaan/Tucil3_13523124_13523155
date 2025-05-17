package entity;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;
    
    public String toString() {
        switch (this) {
            case UP: return "atas";
            case DOWN: return "bawah";
            case LEFT: return "kiri";
            case RIGHT: return "kanan";
            default: return "";
        }
    }
}