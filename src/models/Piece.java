package models;

public class Piece {
    private final char id;          
    private final boolean isPrimary;
    private final boolean isHorizontal;
    private final int size;
    private int row;
    private int col;

    public Piece(char id, boolean isPrimary, boolean isHorizontal, int size, int row, int col) {
        this.id = id;
        this.isPrimary = isPrimary;
        this.isHorizontal = isHorizontal;
        this.size = size;
        this.row = row;
        this.col = col;
    }

    public Piece(Piece other) {
        this.id = other.id;
        this.isPrimary = other.isPrimary;
        this.isHorizontal = other.isHorizontal;
        this.size = other.size;
        this.row = other.row;
        this.col = other.col;
    }

    public char getId() {
        return id;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public int getSize() {
        return size;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getEndRow() {
        return isHorizontal ? row : row + size - 1;
    }

    public int getEndCol() {
        return isHorizontal ? col + size - 1 : col;
    }

    public boolean occupies(int r, int c) {
        if (isHorizontal) {
            return r == row && c >= col && c <= getEndCol();
        } else {
            return c == col && r >= row && r <= getEndRow();
        }
    }

    public boolean canMove(String direction, Board board) {
        if (isHorizontal && (direction.equals("atas") || direction.equals("bawah"))) {
            return false;
        }
        if (!isHorizontal && (direction.equals("kiri") || direction.equals("kanan"))) {
            return false;
        }

        int boardHeight = board.getHeight();
        int boardWidth = board.getWidth();

        switch (direction) {
            case "atas":
                return row > 0 && board.isCellEmpty(row - 1, col);
            case "bawah":
                return getEndRow() < boardHeight - 1 && board.isCellEmpty(getEndRow() + 1, col);
            case "kiri":
                return col > 0 && board.isCellEmpty(row, col - 1);
            case "kanan":
                return getEndCol() < boardWidth - 1 && board.isCellEmpty(row, getEndCol() + 1);
            default:
                return false;
        }
    }

    public void move(String direction) {
        switch (direction) {
            case "atas":
                row--;
                break;
            case "bawah":
                row++;
                break;
            case "kiri":
                col--;
                break;
            case "kanan":
                col++;
                break;
        }
    }

    @Override
    public String toString() {
        return String.format("Piece %c (size: %d, position: [%d,%d], %s, %s)",
                id, size, row, col,
                isHorizontal ? "horizontal" : "vertical",
                isPrimary ? "primary" : "normal");
    }
}