package visualizer;

import entity.Board;
import entity.Direction;
import entity.Move;
import entity.Node;
import entity.Piece;
import entity.Position;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;

public class GUIVisualizer extends JFrame {
    private final List<Node> solution;
    private int currentStep;
    private final BoardPanel boardPanel;
    private final JButton prevButton, nextButton, playButton, stopButton;
    private final JLabel stepLabel;
    private final JLabel moveLabel;
    private final Timer animationTimer;
    private boolean isPlaying;
    
    private static final int CELL_SIZE = 60;
    private static final int MARGIN = 20;
    private static final Color PRIMARY_COLOR = Color.RED;
    private static final Color LAST_MOVED_COLOR = Color.YELLOW;
    private static final Color BORDER_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color WALL_COLOR = Color.BLACK;
    
    public GUIVisualizer(List<Node> solution) {
        this.solution = solution;
        this.currentStep = 0;
        this.isPlaying = false;
        
        Board initialBoard = solution.get(0).getBoard();
        int width = initialBoard.getWidth();
        int height = initialBoard.getHeight();
        
        setTitle("Rush Hour Puzzle Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        boardPanel = new BoardPanel(width, height);
        add(boardPanel, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel();
        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        playButton = new JButton("Play");
        stopButton = new JButton("Stop");
        stepLabel = new JLabel("Step: 0/" + (solution.size() - 1));
        moveLabel = new JLabel("Move: None");
        
        prevButton.addActionListener((ActionEvent e) -> {
            if (currentStep > 0) {
                currentStep--;
                updateView();
            }
        });
        
        nextButton.addActionListener((ActionEvent e) -> {
            if (currentStep < solution.size() - 1) {
                currentStep++;
                updateView();
            }
        });
        
        playButton.addActionListener((ActionEvent e) -> {
            if (!isPlaying) {
                startAnimation();
            }
        });
        
        stopButton.addActionListener((ActionEvent e) -> {
            if (isPlaying) {
                stopAnimation();
            }
        });
        
        controlPanel.add(prevButton);
        controlPanel.add(playButton);
        controlPanel.add(stopButton);
        controlPanel.add(nextButton);
        controlPanel.add(stepLabel);
        controlPanel.add(moveLabel);
        
        add(controlPanel, BorderLayout.SOUTH);
        
        animationTimer = new Timer(500, (ActionEvent e) -> {
            if (currentStep < solution.size() - 1) {
                currentStep++;
                updateView();
            } else {
                stopAnimation();
            }
        });
        
        // tambahkan space untuk dinding
        int windowWidth = (width + 2) * CELL_SIZE + 2 * MARGIN;
        int windowHeight = (height + 2) * CELL_SIZE + 2 * MARGIN + 100;
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);
        
        updateView();
    }
    
    private void updateView() {
        Node currentNode = solution.get(currentStep);
        boardPanel.setBoard(currentNode.getBoard());
        boardPanel.setLastMove(currentStep > 0 ? currentNode.getMove() : null);
        boardPanel.repaint();
        
        stepLabel.setText("Step: " + currentStep + "/" + (solution.size() - 1));
        if (currentStep > 0) {
            moveLabel.setText("Move: " + currentNode.getMove().toString());
        } else {
            moveLabel.setText("Move: None");
        }
    }
    
    private void startAnimation() {
        isPlaying = true;
        playButton.setEnabled(false);
        stopButton.setEnabled(true);
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
        animationTimer.start();
    }
    
    private void stopAnimation() {
        isPlaying = false;
        playButton.setEnabled(true);
        stopButton.setEnabled(false);
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
        animationTimer.stop();
    }
    
    private class BoardPanel extends JPanel {
        private final int width;
        private final int height;
        private Board board;
        private Move lastMove;
        
        public BoardPanel(int width, int height) {
            this.width = width;
            this.height = height;
            // tambahkan ruang untuk dinding
            setPreferredSize(new Dimension((width + 2) * CELL_SIZE + 2 * MARGIN, (height + 2) * CELL_SIZE + 2 * MARGIN));
            setBackground(BACKGROUND_COLOR);
        }
        
        public void setBoard(Board board) {
            this.board = board;
        }
        
        public void setLastMove(Move lastMove) {
            this.lastMove = lastMove;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (board == null) return;
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            char primaryPieceId = board.getPrimaryPieceId();
            Position exitPos = board.getExitPosition();
            Direction exitDir = board.getExitDirection();
            char lastMovePieceId = lastMove != null ? lastMove.getPieceId() : ' ';
            
            // gambar dinding di sekeliling board
            drawWalls(g2d, exitPos, exitDir);
            
            // gambar grid lines untuk playing area
            g2d.setColor(BORDER_COLOR);
            for (int i = 0; i <= height; i++) {
                g2d.drawLine(MARGIN + CELL_SIZE, MARGIN + CELL_SIZE + i * CELL_SIZE, 
                           MARGIN + CELL_SIZE + width * CELL_SIZE, MARGIN + CELL_SIZE + i * CELL_SIZE);
            }
            for (int j = 0; j <= width; j++) {
                g2d.drawLine(MARGIN + CELL_SIZE + j * CELL_SIZE, MARGIN + CELL_SIZE, 
                           MARGIN + CELL_SIZE + j * CELL_SIZE, MARGIN + CELL_SIZE + height * CELL_SIZE);
            }
            
            // gambar pieces di playing area
            for (Piece piece : board.getPieces().values()) {
                Color pieceColor;
                if (piece.getId() == primaryPieceId) {
                    pieceColor = PRIMARY_COLOR;
                } else if (piece.getId() == lastMovePieceId) {
                    pieceColor = LAST_MOVED_COLOR;
                } else {
                    pieceColor = new Color(100 + (piece.getId() * 13) % 155, 100 + (piece.getId() * 17) % 155, 100 + (piece.getId() * 19) % 155);
                }
                
                for (Position pos : piece.getPositions()) {
                    g2d.setColor(pieceColor);
                    g2d.fillRect(MARGIN + CELL_SIZE + pos.getCol() * CELL_SIZE + 1, 
                               MARGIN + CELL_SIZE + pos.getRow() * CELL_SIZE + 1, 
                               CELL_SIZE - 1, CELL_SIZE - 1);
                    g2d.setColor(BORDER_COLOR);
                    g2d.drawString(String.valueOf(piece.getId()), 
                                 MARGIN + CELL_SIZE + pos.getCol() * CELL_SIZE + CELL_SIZE / 2 - 5, 
                                 MARGIN + CELL_SIZE + pos.getRow() * CELL_SIZE + CELL_SIZE / 2 + 5);
                }
            }
        }
        
        private void drawWalls(Graphics2D g2d, Position exitPos, Direction exitDir) {
            g2d.setColor(WALL_COLOR);
            
            // gambar dinding atas
            for (int j = 0; j < width + 2; j++) {
                if (!(exitDir == Direction.UP && j == exitPos.getCol() + 1)) {
                    g2d.fillRect(MARGIN + j * CELL_SIZE, MARGIN, CELL_SIZE, CELL_SIZE);
                }
            }
            
            // gambar dinding bawah
            for (int j = 0; j < width + 2; j++) {
                if (!(exitDir == Direction.DOWN && j == exitPos.getCol() + 1)) {
                    g2d.fillRect(MARGIN + j * CELL_SIZE, MARGIN + (height + 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
            
            // gambar dinding kiri
            for (int i = 1; i <= height; i++) {
                if (!(exitDir == Direction.LEFT && i - 1 == exitPos.getRow())) {
                    g2d.fillRect(MARGIN, MARGIN + i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
            
            // gambar dinding kanan
            for (int i = 1; i <= height; i++) {
                if (!(exitDir == Direction.RIGHT && i - 1 == exitPos.getRow())) {
                    g2d.fillRect(MARGIN + (width + 1) * CELL_SIZE, MARGIN + i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
            
            // gambar border untuk exit (kotak kosong)
            g2d.setColor(BORDER_COLOR);
            int exitDisplayRow, exitDisplayCol;
            
            switch (exitDir) {
                case UP -> {
                    exitDisplayRow = 0;
                    exitDisplayCol = exitPos.getCol() + 1;
                }
                case DOWN -> {
                    exitDisplayRow = height + 1;
                    exitDisplayCol = exitPos.getCol() + 1;
                }
                case LEFT -> {
                    exitDisplayRow = exitPos.getRow() + 1;
                    exitDisplayCol = 0;
                }
                case RIGHT -> {
                    exitDisplayRow = exitPos.getRow() + 1;
                    exitDisplayCol = width + 1;
                }
                default -> {
                    return;
                }
            }
            
            // gambar border exit tanpa fill
            g2d.drawRect(MARGIN + exitDisplayCol * CELL_SIZE, 
                        MARGIN + exitDisplayRow * CELL_SIZE, 
                        CELL_SIZE, CELL_SIZE);
        }
    }
}