import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PongGame extends JPanel implements ActionListener, KeyListener {

    // Game constants
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int PADDLE_WIDTH = 10;
    private final int PADDLE_HEIGHT = 100;
    private final int BALL_SIZE = 20;
    private final int PADDLE_SPEED = 5;

    // Positions
    private int leftPaddleY = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int rightPaddleY = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int ballX = WIDTH / 2 - BALL_SIZE / 2;
    private int ballY = HEIGHT / 2 - BALL_SIZE / 2;

    // Velocities
    private int ballVelX = 3;
    private int ballVelY = 3;
    private int leftPaddleVelY = 0;
    private int rightPaddleVelY = 0; // Can be controlled by AI or player

    private Timer timer;

    public PongGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Initialize timer for game loop
        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the paddles
        g.setColor(Color.WHITE);
        g.fillRect(0, leftPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT); // Left paddle
        g.fillRect(WIDTH - PADDLE_WIDTH, rightPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT); // Right paddle

        // Draw the ball
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Update paddles positions
        leftPaddleY += leftPaddleVelY;
        rightPaddleY += rightPaddleVelY;

        // Keep paddles within bounds
        if (leftPaddleY < 0) leftPaddleY = 0;
        if (leftPaddleY > HEIGHT - PADDLE_HEIGHT) leftPaddleY = HEIGHT - PADDLE_HEIGHT;

        if (rightPaddleY < 0) rightPaddleY = 0;
        if (rightPaddleY > HEIGHT - PADDLE_HEIGHT) rightPaddleY = HEIGHT - PADDLE_HEIGHT;

        // Move the ball
        ballX += ballVelX;
        ballY += ballVelY;

        // Ball collision with top and bottom
        if (ballY <= 0 || ballY >= HEIGHT - BALL_SIZE) {
            ballVelY = -ballVelY;
        }

        // Collision with paddles
        // Left paddle
        if (ballX <= PADDLE_WIDTH) {
            if (ballY + BALL_SIZE >= leftPaddleY && ballY <= leftPaddleY + PADDLE_HEIGHT) {
                ballVelX = -ballVelX;
            } else if (ballX < 0) {
                // Score or reset
                resetBall();
            }
        }

        // Right paddle
        if (ballX + BALL_SIZE >= WIDTH - PADDLE_WIDTH) {
            if (ballY + BALL_SIZE >= rightPaddleY && ballY <= rightPaddleY + PADDLE_HEIGHT) {
                ballVelX = -ballVelX;
            } else if (ballX > WIDTH) {
                // Score or reset
                resetBall();
            }
        }

        repaint();
    }

    private void resetBall() {
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballVelX = -ballVelX; // Change direction
        ballVelY = 3 * (Math.random() > 0.5 ? 1 : -1); // Randomize vertical direction
    }

    // Basic keyboard controls: W/S for left paddle, Up/Down for right paddle
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                leftPaddleVelY = -PADDLE_SPEED;
                break;
            case KeyEvent.VK_S:
                leftPaddleVelY = PADDLE_SPEED;
                break;
            case KeyEvent.VK_UP:
                rightPaddleVelY = -PADDLE_SPEED;
                break;
            case KeyEvent.VK_DOWN:
                rightPaddleVelY = PADDLE_SPEED;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_S:
                leftPaddleVelY = 0;
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                rightPaddleVelY = 0;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong Game");
        PongGame gamePanel = new PongGame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
