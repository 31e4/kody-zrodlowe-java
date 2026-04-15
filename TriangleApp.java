import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class TriangleApp extends JFrame {

    private DrawPanel drawPanel;
    private JButton centroidBtn, incircleBtn, circumcircleBtn;
    private JButton heightsBtn; /// new feature button

    public TriangleApp() {
        setTitle("Triangle Geometry App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();

        centroidBtn = new JButton("Draw Centroid");
        incircleBtn = new JButton("Draw Incircle");
        circumcircleBtn = new JButton("Draw Circumcircle");
        heightsBtn = new JButton("Draw Heights"); ///

        controlPanel.add(centroidBtn);
        controlPanel.add(incircleBtn);
        controlPanel.add(circumcircleBtn);
        controlPanel.add(heightsBtn); ///

        add(controlPanel, BorderLayout.SOUTH);

        // Button actions
        centroidBtn.addActionListener(e -> {drawPanel.showCentroid = true; drawPanel.repaint();});
        incircleBtn.addActionListener(e -> {drawPanel.showIncircle = true; drawPanel.repaint();});
        circumcircleBtn.addActionListener(e -> {drawPanel.showCircumcircle = true; drawPanel.repaint();});
        heightsBtn.addActionListener(e -> {drawPanel.showHeights = true; drawPanel.repaint();}); ///

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TriangleApp::new);
    }
}

class DrawPanel extends JPanel {

    ArrayList<Point> points = new ArrayList<>();

    boolean showCentroid = false;
    boolean showIncircle = false;
    boolean showCircumcircle = false;
    boolean showHeights = false; ///

    public DrawPanel() {
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (points.size() < 3) {
                    points.add(e.getPoint());
                    repaint();
                }
            }
        });
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Draw points
        for (Point p : points) {
            g2.fillOval(p.x - 4, p.y - 4, 8, 8);
        }

        if (points.size() == 3) {
            Point A = points.get(0);
            Point B = points.get(1);
            Point C = points.get(2);

            // Draw triangle
            g2.drawLine(A.x, A.y, B.x, B.y);
            g2.drawLine(B.x, B.y, C.x, C.y);
            g2.drawLine(C.x, C.y, A.x, A.y);

            if (showCentroid) drawCentroid(g2, A, B, C);         ///
            if (showIncircle) drawIncircle(g2, A, B, C);         ///
            if (showCircumcircle) drawCircumcircle(g2, A, B, C); ///
            if (showHeights) drawHeights(g2, A, B, C);           ///
        }
    }

    private void drawCentroid(Graphics2D g2, Point A, Point B, Point C) {
        int x = (A.x + B.x + C.x) / 3;
        int y = (A.y + B.y + C.y) / 3;

        g2.setColor(Color.RED);
        g2.fillOval(x - 5, y - 5, 10, 10);
        g2.setColor(Color.BLACK);
    }

    private double dist(Point p1, Point p2) {
        return Math.hypot(p1.x - p2.x, p1.y - p2.y);
    }

    private void drawIncircle(Graphics2D g2, Point A, Point B, Point C) {
        double a = dist(B, C);
        double b = dist(A, C);
        double c = dist(A, B);

        double px = (a * A.x + b * B.x + c * C.x) / (a + b + c);
        double py = (a * A.y + b * B.y + c * C.y) / (a + b + c);

        double s = (a + b + c) / 2;
        double area = Math.abs((A.x*(B.y-C.y) + B.x*(C.y-A.y) + C.x*(A.y-B.y)) / 2.0);

        double r = area / s;

        g2.setColor(Color.BLUE);
        g2.drawOval((int)(px - r), (int)(py - r), (int)(2*r), (int)(2*r));
        g2.setColor(Color.BLACK);
    }

    private void drawCircumcircle(Graphics2D g2, Point A, Point B, Point C) {
        double d = 2 * (A.x*(B.y-C.y) + B.x*(C.y-A.y) + C.x*(A.y-B.y));

        if (d == 0) return; // collinear

        double ux = (
                (A.x*A.x + A.y*A.y)*(B.y - C.y) +
                (B.x*B.x + B.y*B.y)*(C.y - A.y) +
                (C.x*C.x + C.y*C.y)*(A.y - B.y)
        ) / d;

        double uy = (
                (A.x*A.x + A.y*A.y)*(C.x - B.x) +
                (B.x*B.x + B.y*B.y)*(A.x - C.x) +
                (C.x*C.x + C.y*C.y)*(B.x - A.x)
        ) / d;

        double r = Math.hypot(A.x - ux, A.y - uy);

        g2.setColor(Color.GREEN);
        g2.drawOval((int)(ux - r), (int)(uy - r), (int)(2*r), (int)(2*r));
        g2.setColor(Color.BLACK);
    }

    private void drawHeights(Graphics2D g2, Point A, Point B, Point C) { ///
        g2.setColor(Color.MAGENTA);                                      ///
                                                                         ///
        drawAltitude(g2, A, B, C); // from A to BC                       ///
        drawAltitude(g2, B, A, C); // from B to AC                       ///
        drawAltitude(g2, C, A, B); // from C to AB                       ///
                                                                         ///
    g2.setColor(Color.BLACK);                                            ///
    }                                                                    ///

    private void drawAltitude(Graphics2D g2, Point P, Point A, Point B) {  ///
        // Line AB represented as ax + by + c = 0                          ///
        double a = B.y - A.y;                                              ///
        double b = A.x - B.x;                                              ///
        double c = B.x * A.y - A.x * B.y;                                  ///
                                                                           ///
        // Foot of perpendicular from P to AB                              ///
        double d = (a * P.x + b * P.y + c) / (a * a + b * b);              ///
                                                                           ///
        double x = P.x - a * d;                                            ///
        double y = P.y - b * d;                                            ///
                                                                           ///
        // Draw altitude                                                   ///
        g2.drawLine(P.x, P.y, (int)x, (int)y);                             ///
    }                                                                      ///
}
