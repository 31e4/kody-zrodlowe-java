import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleFPSGame extends JPanel implements KeyListener, MouseMotionListener, MouseListener, ActionListener {
    // Constants
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int MAP_SIZE = 10;
    private static final int CELL_SIZE = 64;
    private static final int NUM_TARGETS = 5;

    // Player state
    private double playerX = MAP_SIZE * CELL_SIZE / 2.0;
    private double playerY = MAP_SIZE * CELL_SIZE / 2.0;
    private double playerAngle = 0; // in radians
    private double moveSpeed = 2.0;
    private double rotSpeed = Math.toRadians(3);

    // Input flags
    private boolean up, down, left, right;

    // Targets
    private List<Target> targets = new ArrayList<>();
    private Random rand = new Random();

    // Timer for game loop
    private Timer timer;

    // FOV and rendering
    private static final double FOV = Math.toRadians(60);
    private static final int NUM_RAYS = 120;
    private double[] rayAngles = new double[NUM_RAYS];

    public SimpleFPSGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        setFocusable(true);
        requestFocusInWindow();

        // Initialize targets
        for (int i = 0; i < NUM_TARGETS; i++) {
            targets.add(new Target(rand.nextDouble() * MAP_SIZE * CELL_SIZE,
                                   rand.nextDouble() * MAP_SIZE * CELL_SIZE));
        }

        // Initialize ray angles
        double startAngle = playerAngle - FOV / 2;
        for (int i = 0; i < NUM_RAYS; i++) {
            rayAngles[i] = startAngle + i * (FOV / NUM_RAYS);
        }

        // Start game loop
        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw 3D view
        draw3DView(g);

        // Draw mini-map
        drawMiniMap(g);
    }

    private void drawMiniMap(Graphics g) {
        int size = 200;
        int offsetX = 10;
        int offsetY = 10;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(offsetX, offsetY, size, size);
        g.setColor(Color.WHITE);
        // Draw grid
        for (int i = 0; i <= MAP_SIZE; i++) {
            int linePos = offsetX + i * CELL_SIZE;
            g.drawLine(linePos, offsetY, linePos, offsetY + MAP_SIZE * CELL_SIZE);
            g.drawLine(offsetX, offsetY + i * CELL_SIZE, offsetX + MAP_SIZE * CELL_SIZE, offsetY + i * CELL_SIZE);
        }
        // Draw player
        g.setColor(Color.BLUE);
        int px = offsetX + (int) (playerX);
        int py = offsetY + (int) (playerY);
        g.fillOval(px - 5, py - 5, 10, 10);
        // Draw targets
        g.setColor(Color.RED);
        for (Target t : targets) {
            int tx = offsetX + (int) t.x;
            int ty = offsetY + (int) t.y;
            g.fillOval(tx - 5, ty - 5, 10, 10);
        }
    }

    private void draw3DView(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        // For each ray
        for (int i = 0; i < NUM_RAYS; i++) {
            double rayAngle = rayAngles[i];
            double distance = castRay(rayAngle);
            // Correct fish-eye effect
            double correctedDistance = distance * Math.cos(rayAngle - playerAngle);
            // Calculate line height
            int lineHeight = (int) ((CELL_SIZE * 300) / (correctedDistance + 0.0001));
            lineHeight = Math.min(lineHeight, height);
            int lineX = (int) (i * (width / (double) NUM_RAYS));
            int lineY = (height - lineHeight) / 2;

            // Shade based on distance
            float shade = Math.max(0.2f, 1 - (float) (correctedDistance / (MAP_SIZE * CELL_SIZE * 2)));
            g.setColor(new Color(shade, shade, shade));
            g.fillRect(lineX, lineY, (int) (width / (double) NUM_RAYS) + 1, lineHeight);
        }
    }

    private double castRay(double angle) {
        double stepSize = 5;
        double distance = 0;
        double ox = playerX;
        double oy = playerY;
        while (distance < MAP_SIZE * CELL_SIZE) {
            ox += Math.cos(angle) * stepSize;
            oy += Math.sin(angle) * stepSize;
            distance += stepSize;

            int mapX = (int) (ox / CELL_SIZE);
            int mapY = (int) (oy / CELL_SIZE);

            if (mapX < 0 || mapX >= MAP_SIZE || mapY < 0 || mapY >= MAP_SIZE) {
                return distance;
            }

            // Check collision with targets
            for (Target t : targets) {
                if (!t.hit && Math.hypot(t.x - ox, t.y - oy) < 10) {
                    t.hit = true; // Target hit
                }
            }
        }
        return distance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Update player position
        if (up) {
            playerX += Math.cos(playerAngle) * moveSpeed;
            playerY += Math.sin(playerAngle) * moveSpeed;
        }
        if (down) {
            playerX -= Math.cos(playerAngle) * moveSpeed;
            playerY -= Math.sin(playerAngle) * moveSpeed;
        }
        if (left) {
            playerX += Math.cos(playerAngle - Math.PI / 2) * moveSpeed;
            playerY += Math.sin(playerAngle - Math.PI / 2) * moveSpeed;
        }
        if (right) {
            playerX += Math.cos(playerAngle + Math.PI / 2) * moveSpeed;
            playerY += Math.sin(playerAngle + Math.PI / 2) * moveSpeed;
        }

        // Keep player within bounds
        playerX = Math.max(0, Math.min(MAP_SIZE * CELL_SIZE, playerX));
        playerY = Math.max(0, Math.min(MAP_SIZE * CELL_SIZE, playerY));

        // Update ray angles based on mouse position
        // (Could be added for aiming)

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode(); // retrieve key code
        switch (keyCode) {
            case KeyEvent.VK_W:
                up = true;
                break;
            case KeyEvent.VK_S:
                down = true;
                break;
            case KeyEvent.VK_A:
                left = true;
                break;
            case KeyEvent.VK_D:
                right = true;
                break;
        }
        // Rotate left/right
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            playerAngle -= rotSpeed;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            playerAngle += rotSpeed;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode(); // retrieve key code
        switch (keyCode) {
            case KeyEvent.VK_W:
                up = false;
                break;
            case KeyEvent.VK_S:
                down = false;
                break;
            case KeyEvent.VK_A:
                left = false;
                break;
            case KeyEvent.VK_D:
                right = false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No action
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Aim towards mouse
        int mx = e.getX();
        int my = e.getY();
        double dx = mx - getWidth() / 2.0;
        double dy = my - getHeight() / 2.0;
        playerAngle = Math.atan2(dy, dx);
        // Update ray angles
        double startAngle = playerAngle - FOV / 2;
        for (int i = 0; i < NUM_RAYS; i++) {
            rayAngles[i] = startAngle + i * (FOV / NUM_RAYS);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            // Shooting - check if hits a target
            for (Target t : targets) {
                if (!t.hit) {
                    double dx = t.x - playerX;
                    double dy = t.y - playerY;
                    double angleToTarget = Math.atan2(dy, dx);
                    double diff = Math.abs(normalizeAngle(angleToTarget - playerAngle));
                    if (diff < Math.toRadians(10)) {
                        t.hit = true;
                    }
                }
            }
        }
    }

    private double normalizeAngle(double angle) {
        while (angle < -Math.PI) angle += 2 * Math.PI;
        while (angle > Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // No action
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // No action
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // No action
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // No action
    }

    // Target class
    static class Target {
        double x, y;
        boolean hit = false;

        Target(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Simple FPS");
        SimpleFPSGame game = new SimpleFPSGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
