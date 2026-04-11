import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Maze3DGame extends JPanel implements KeyListener {

    // Maze layout: 1 = wall, 0 = empty space
    private final int[][] maze = {
        {1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 0, 1, 1, 0, 1},
        {1, 0, 1, 0, 0, 1, 0, 1},
        {1, 0, 1, 1, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1}
    };

    private double playerX = 2.5; // Player position in maze (column)
    private double playerY = 5.5; // Player position in maze (row)
    private double playerDir = 0; // Direction in radians
    private final double FOV = Math.toRadians(60); // Field of view
    private final double moveSpeed = 0.1;
    private final double rotSpeed = Math.toRadians(5);

    public Maze3DGame() {
        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Cast rays for each vertical stripe
        int width = getWidth();
        int height = getHeight();

        for (int x = 0; x < width; x++) {
            // Calculate ray angle
            double cameraX = 2 * x / (double) width - 1; // from -1 to 1
            double rayAngle = playerDir + cameraX * FOV / 2;

            // Ray direction
            double rayX = Math.cos(rayAngle);
            double rayY = Math.sin(rayAngle);

            // Distance to wall
            double distance = castRay(playerX, playerY, rayX, rayY);

            // Simple shading based on distance
            int lineHeight = (int) ((height / distance) * 1.5);
            lineHeight = Math.min(lineHeight, height);

            int drawStart = (height - lineHeight) / 2;
            g.setColor(Color.GRAY);
            g.drawLine(x, drawStart, x, drawStart + lineHeight);
        }
    }

    private double castRay(double startX, double startY, double dirX, double dirY) {
        double stepSize = 0.05; // How far to step each iteration
        double distance = 0;

        while (distance < 20) { // Max distance
            double testX = startX + dirX * distance;
            double testY = startY + dirY * distance;

            int mazeX = (int) testX;
            int mazeY = (int) testY;

            if (mazeY < 0 || mazeY >= maze.length || mazeX < 0 || mazeX >= maze[0].length) {
                return distance;
            }

            if (maze[mazeY][mazeX] == 1) {
                return distance;
            }

            distance += stepSize;
        }
        return 20;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        double newX = playerX;
        double newY = playerY;

        if (key == KeyEvent.VK_W) {
            // Move forward
            newX += Math.cos(playerDir) * moveSpeed;
            newY += Math.sin(playerDir) * moveSpeed;
            if (isWalkable(newX, newY)) {
                playerX = newX;
                playerY = newY;
            }
        } else if (key == KeyEvent.VK_S) {
            // Move backward
            newX -= Math.cos(playerDir) * moveSpeed;
            newY -= Math.sin(playerDir) * moveSpeed;
            if (isWalkable(newX, newY)) {
                playerX = newX;
                playerY = newY;
            }
        } else if (key == KeyEvent.VK_A) {
            // Turn left
            playerDir -= rotSpeed;
        } else if (key == KeyEvent.VK_D) {
            // Turn right
            playerDir += rotSpeed;
        }

        repaint();
    }

    private boolean isWalkable(double x, double y) {
        int mazeX = (int) x;
        int mazeY = (int) y;
        if (mazeY < 0 || mazeY >= maze.length || mazeX < 0 || mazeX >= maze[0].length) {
            return false;
        }
        return maze[mazeY][mazeX] == 0;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("3D Maze Walking");
        Maze3DGame game = new Maze3DGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
