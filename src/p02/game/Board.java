package p02.game;

import p02.pres.CounterPanel;
import p02.pres.GameBoard;
import p02.pres.TrackPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Iterator;
import java.util.Arrays;

public class Board extends JPanel implements KeyListener, ActionListener {
    private final int[] track;
    private int carPosition;
    private int score;
    private int tickCounter;
    private final Random random;
    private final Map<Integer, Image[]> images;
    private final Map<Integer, Integer> imageHeights;
    private Image placeholder;
    private Image carLeft;
    private Image carMiddle;
    private Image carRight;
    private boolean gameStarted;
    private final GameBoard gameBoard;
    private final CounterPanel counterPanel;
    private final JLayeredPane layeredPane;
    private final TrackPanel trackPanel;
    private GameThread gameThread;
    private GameState gameState;

    public Board() {
        this.track = new int[7];
        this.carPosition = 1;
        this.score = 0;
        this.tickCounter = 0;
        this.random = new Random();
        this.images = new HashMap<>();
        this.imageHeights = new HashMap<>();
        this.gameStarted = false;

        loadImages();

        this.gameBoard = new GameBoard(carLeft, carMiddle, carRight, images, imageHeights);
        this.trackPanel = new TrackPanel();
        this.layeredPane = new JLayeredPane();
        this.counterPanel = new CounterPanel();

        setLayout(new BorderLayout());

        layeredPane.setLayout(null);

        trackPanel.setBounds(-80, 0, 1080, 790);
        gameBoard.setBounds(0, 0, 986, 670);
        counterPanel.setBounds(10, 10, 150, 50);

        layeredPane.add(gameBoard, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(trackPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(counterPanel, JLayeredPane.MODAL_LAYER);

        add(layeredPane, BorderLayout.CENTER);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_S) {
                    if (gameState == GameState.START || gameState == GameState.GAME_OVER) {
                        gameState = GameState.RUNNING;
                        startGameThread();
                    } else if (gameState == GameState.RUNNING) {
                        gameState = GameState.PAUSED;
                        gameThread.stopThread();
                    } else if (gameState == GameState.PAUSED) {
                        gameState = GameState.RUNNING;
                        startGameThread();
                    }
                } else if (keyCode == KeyEvent.VK_A && gameState == GameState.RUNNING) {
                    carPosition = Math.max(0, carPosition - 1);
                    gameBoard.updateBoard(track, carPosition);
                } else if (keyCode == KeyEvent.VK_D && gameState == GameState.RUNNING) {
                    carPosition = Math.min(2, carPosition + 1);
                    gameBoard.updateBoard(track, carPosition);
                }
            }
        });

        gameThread = GameThread.getInstance(this);
        gameState = GameState.START;
    }

    private void loadImages() {
        try {
            placeholder = ImageIO.read(new File("src/Rows/placeholder.png"));
        } catch (IOException e) {
            e.printStackTrace();
            placeholder = new BufferedImage(50, 100, BufferedImage.TYPE_INT_ARGB);
            Graphics g = placeholder.getGraphics();
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, 50, 100);
            g.dispose();
        }

        try {
            carLeft = ImageIO.read(new File("src/Rows/carl.png"));
            carMiddle = ImageIO.read(new File("src/Rows/car_m.png"));
            carRight = ImageIO.read(new File("src/Rows/carr.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for (int row = 0; row < 7; row++) {
                Image[] rowImages = new Image[15];
                int maxHeight = 0;
                for (int col = 0; col < 15; col++) {
                    if ((row == 0 && col != 0) || col == 7) {
                        rowImages[col] = placeholder;
                        continue;
                    }
                    String fileName = String.format("src/Rows/%d.%d.png", row, col);
                    File imageFile = new File(fileName);
                    if (imageFile.exists() && imageFile.isFile()) {
                        try {
                            rowImages[col] = ImageIO.read(imageFile);
                            int imageHeight = rowImages[col].getHeight(null);
                            if (imageHeight > maxHeight) {
                                maxHeight = imageHeight;
                            }
                        } catch (IOException e) {
                            System.err.println("Error reading image file: " + fileName);
                        }
                    } else {
                        rowImages[col] = placeholder;
                    }
                }
                images.put(row, rowImages);
                imageHeights.put(row, maxHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGameThread() {
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = GameThread.getInstance(this);
            gameThread.start();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());

        if (!gameStarted) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Press 'S' to Start", getWidth() / 2 - 150, getHeight() / 2);
        } else {
            gameBoard.repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted) {
            tickCounter++;
            updateObstacles();
            checkCollision();
            gameBoard.updateBoard(track, carPosition);
        }
    }

    public void tick() {
        tickCounter++;
        updateObstacles();
        checkCollision();
        gameBoard.updateBoard(track, carPosition);
    }

    private void updateObstacles() {
        System.arraycopy(track, 0, track, 1, track.length - 1);

        int numberOfZeros = countZeros();
        int obstacleFrequency = numberOfZeros + 1;

        if (tickCounter % obstacleFrequency == 0) {
            int newObstaclePosition;
            do {
                newObstaclePosition = random.nextInt(15);
            } while ((track[1] == newObstaclePosition && track[2] == newObstaclePosition)
                    || (track[0] == newObstaclePosition)
                    || (newObstaclePosition == 7)
                    || (track[0] != 0 && track[1] != 0 && newObstaclePosition == track[1]));
            track[0] = newObstaclePosition;

            if ((track[1] & track[0]) != 0) {
                track[0] = 0;
            }
        } else {
            track[0] = 0;
        }

        Iterator<Integer> iterator = Arrays.stream(track).iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        System.out.println();
    }

    private int countZeros() {
        int count = 0;
        if (counterPanel.getHundreds().getDigit() == 0) count++;
        if (counterPanel.getTens().getDigit() == 0) count++;
        if (counterPanel.getOnes().getDigit() == 0) count++;
        return count;
    }

    private void checkCollision() {
        int bottomObstacle = track[6];
        if ((bottomObstacle == 1 && carPosition == 2) ||
                (bottomObstacle == 2 && carPosition == 1) ||
                (bottomObstacle == 3 && (carPosition == 1 || carPosition == 2)) ||
                (bottomObstacle == 4 && carPosition == 0) ||
                (bottomObstacle == 5 && (carPosition == 0 || carPosition == 2)) ||
                (bottomObstacle == 6 && (carPosition == 0 || carPosition == 1)) ||
                (bottomObstacle == 9 && carPosition == 2) ||
                (bottomObstacle == 10 && carPosition == 1) ||
                (bottomObstacle == 11 && (carPosition == 1 || carPosition == 2)) ||
                (bottomObstacle == 12 && carPosition == 0) ||
                (bottomObstacle == 13 && (carPosition == 0 || carPosition == 2)) ||
                (bottomObstacle == 14 && (carPosition == 0 || carPosition == 1))) {
            gameThread.stopThread();
            JOptionPane.showMessageDialog(this, "Game Over! Your score: " + score);
            fireResetEvent();
            resetGame();
        } else {
            if (bottomObstacle % 10 != 0 && bottomObstacle % 10 != 8) {
                score++;
                counterPanel.incrementCounter();
            }
        }
    }



    private void fireResetEvent() {
        ResetEvent event = new ResetEvent(this);
        counterPanel.fireResetEvent(event);
    }

    private void resetGame() {
        for (int i = 0; i < track.length; i++) {
            track[i] = 0;
        }
        carPosition = 1;
        score = 0;
        tickCounter = 0;
        gameThread.resetInterval();
        gameStarted = true;
        gameState = GameState.START;
        gameThread = GameThread.getInstance(this);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Autoslalom Game");
        Board board = new Board();
        frame.add(board);
        frame.setSize(986, 670);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
