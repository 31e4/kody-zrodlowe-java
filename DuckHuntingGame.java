import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class DuckHuntingGame extends JFrame {
    public DuckHuntingGame() {
        setTitle("2D Duck Hunting Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        GamePanel panel = new GamePanel();
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DuckHuntingGame());
    }
}

class GamePanel extends JPanel implements ActionListener, MouseListener {
    private Timer timer;
    private java.util.List<Duck> ducks;
    private Random rand;
    private int score;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(135, 206, 250)); // Sky blue
        ducks = new ArrayList<>();
        rand = new Random();
        score = 0;

        // Spawn ducks periodically
        timer = new Timer(20, this); // 50 FPS
        timer.start();

        // Spawn ducks every 1 second
        Timer spawnTimer = new Timer(1000, e -> spawnDuck());
        spawnTimer.start();

        addMouseListener(this);
    }

    private void spawnDuck() {
        // Ducks appear randomly on the left or right
        boolean fromLeft = rand.nextBoolean();
        int y = rand.nextInt(HEIGHT / 2) + 50; // Upper half of screen
        int speed = rand.nextInt(3) + 2; // Speed between 2 and 4

        Duck duck = new Duck(
                fromLeft ? -50 : WIDTH + 50,
                y,
                fromLeft,
                speed
        );
        ducks.add(duck);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background (sky)
        g.setColor(new Color(135, 206, 250));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw ducks
        for (Duck duck : ducks) {
            duck.draw(g);
        }

        // Draw score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 25);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Update ducks positions
        Iterator<Duck> iterator = ducks.iterator();
        while (iterator.hasNext()) {
            Duck duck = iterator.next();
            duck.move();

            // Remove ducks that fly out of screen
            if (duck.isOutOfScreen(getWidth(), getHeight())) {
                iterator.remove();
            }
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Check if a duck is clicked
        Point clickPoint = e.getPoint();
        Iterator<Duck> iterator = ducks.iterator();
        while (iterator.hasNext()) {
            Duck duck = iterator.next();
            if (duck.contains(clickPoint)) {
                // Duck is shot
                score++;
                iterator.remove();
                break; // Only one duck per click
            }
        }
        repaint();
    }

    // Unused mouse events
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}

class Duck {
    private int x, y;
    private int width = 40;
    private int height = 20;
    private boolean movingRight;
    private int speed;
    private Color color;

    public Duck(int x, int y, boolean movingRight, int speed) {
        this.x = x;
        this.y = y;
        this.movingRight = movingRight;
        this.speed = speed;
        this.color = new Color(139, 69, 19); // Brown duck
    }

    public void move() {
        if (movingRight) {
            x += speed;
        } else {
            x -= speed;
        }
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
        // Simple wing
        g.setColor(Color.BLACK);
        g.drawOval(x, y, width, height);
        // Beak
        g.setColor(Color.ORANGE);
        int[] bx = {x + width, x + width + 10, x + width};
        int[] by = {y + height / 2, y + height / 2 - 5, y + height / 2 + 5};
        g.fillPolygon(bx, by, 3);
    }

    public boolean contains(Point p) {
        Rectangle rect = new Rectangle(x, y, width, height);
        return rect.contains(p);
    }

    public boolean isOutOfScreen(int screenWidth, int screenHeight) {
        return (x > screenWidth + 50) || (x < -50);
    }
}
