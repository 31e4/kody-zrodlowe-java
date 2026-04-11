import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SpiderGame extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public SpiderGame() {
        setTitle("Spider Card Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SpiderGame game = new SpiderGame();
            game.setVisible(true);
        });
    }
}

class GamePanel extends JPanel implements MouseListener, MouseMotionListener {

    private Point mousePosition = new Point();
    private boolean dragging = false;
    private Rectangle cardRect;

    public GamePanel() {
        setBackground(new Color(0, 128, 0)); // Green background
        addMouseListener(this);
        addMouseMotionListener(this);
        // Example card rectangle
        cardRect = new Rectangle(50, 50, 100, 150);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw a simple card placeholder
        g.setColor(Color.WHITE);
        g.fillRect(cardRect.x, cardRect.y, cardRect.width, cardRect.height);
        g.setColor(Color.BLACK);
        g.drawRect(cardRect.x, cardRect.y, cardRect.width, cardRect.height);
        g.drawString("Spider Card", cardRect.x + 10, cardRect.y + 75);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (cardRect.contains(e.getPoint())) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            // Move the card with the mouse
            cardRect.setLocation(e.getX() - cardRect.width / 2, e.getY() - cardRect.height / 2);
            repaint();
        }
    }

    // Unused mouse events
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}
}
