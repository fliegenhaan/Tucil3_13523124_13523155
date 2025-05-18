package visualizer;

import javax.swing.*;

import entity.Board;
import entity.Move;
import entity.Node;
import entity.Piece;
import entity.Position;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GUIVisualizer extends JFrame {
    private List<Node> solution;
    private int currentStep;
    private BoardPanel boardPanel;
    private JButton prevButton, nextButton, playButton, stopButton;
    private JLabel stepLabel;
    private JLabel moveLabel;
    private Timer animationTimer;
    private boolean isPlaying;
    
    private static final int CELL_SIZE = 60;
    private static final int MARGIN = 20;
    private static final Color PRIMARY_COLOR = Color.RED;
    private static final Color EXIT_COLOR = Color.GREEN;
    private static final Color LAST_MOVED_COLOR = Color.YELLOW;
    private static final Color BORDER_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    
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
        
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentStep > 0) {
                    currentStep--;
                    updateView();
                }
            }
        });
        
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentStep < solution.size() - 1) {
                    currentStep++;
                    updateView();
                }
            }
        });
        
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPlaying) {
                    startAnimation();
                }
            }
        });
        
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPlaying) {
                    stopAnimation();
                }
            }
        });
        
        controlPanel.add(prevButton);
        controlPanel.add(playButton);
        controlPanel.add(stopButton);
        controlPanel.add(nextButton);
        controlPanel.add(stepLabel);
        controlPanel.add(moveLabel);
        
        add(controlPanel, BorderLayout.SOUTH);
        
        animationTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentStep < solution.size() - 1) {
                    currentStep++;
                    updateView();
                } else {
                    stopAnimation();
                }
            }
        });
        
        int windowWidth = width * CELL_SIZE + 2 * MARGIN;
        int windowHeight = height * CELL_SIZE + 2 * MARGIN + 100;
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
        private int width;
        private int height;
        private Board board;
        private Move lastMove;
        
        public BoardPanel(int width, int height) {
            this.width = width;
            this.height = height;
            setPreferredSize(new Dimension(width * CELL_SIZE + 2 * MARGIN, height * CELL_SIZE + 2 * MARGIN));
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
            
            char[][] grid = board.getGrid();
            char primaryPieceId = board.getPrimaryPieceId();
            Position exitPos = board.getExitPosition();
            char lastMovePieceId = lastMove != null ? lastMove.getPieceId() : ' ';
            
            g2d.setColor(BORDER_COLOR);
            for (int i = 0; i <= height; i++) {
                g2d.drawLine(MARGIN, MARGIN + i * CELL_SIZE, MARGIN + width * CELL_SIZE, MARGIN + i * CELL_SIZE);
            }
            for (int j = 0; j <= width; j++) {
                g2d.drawLine(MARGIN + j * CELL_SIZE, MARGIN, MARGIN + j * CELL_SIZE, MARGIN + height * CELL_SIZE);
            }
            
            g2d.setColor(EXIT_COLOR);
            g2d.fillRect(MARGIN + exitPos.getCol() * CELL_SIZE, MARGIN + exitPos.getRow() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            g2d.setColor(BORDER_COLOR);
            g2d.drawString("K", MARGIN + exitPos.getCol() * CELL_SIZE + CELL_SIZE / 2 - 5, MARGIN + exitPos.getRow() * CELL_SIZE + CELL_SIZE / 2 + 5);
            
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
                    g2d.fillRect(MARGIN + pos.getCol() * CELL_SIZE + 1, MARGIN + pos.getRow() * CELL_SIZE + 1, CELL_SIZE - 1, CELL_SIZE - 1);
                    g2d.setColor(BORDER_COLOR);
                    g2d.drawString(String.valueOf(piece.getId()), MARGIN + pos.getCol() * CELL_SIZE + CELL_SIZE / 2 - 5, MARGIN + pos.getRow() * CELL_SIZE + CELL_SIZE / 2 + 5);
                }
            }
        }
    }
}