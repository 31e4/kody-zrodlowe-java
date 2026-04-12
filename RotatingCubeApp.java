import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;

public class RotatingCubeApp extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // Rotation angles for main cube
    private double angleX = 0;
    private double angleY = 0;

    // Orbit angle for smaller cube
    private double orbitAngle = 0;

    // Timer for animation
    private Timer timer;

    public RotatingCubeApp() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        // Initialize timer for animation (60 FPS)
        timer = new Timer(16, e -> {
            updateAngles();
            repaint();
        });
        timer.start();
    }

    private void updateAngles() {
        angleX += Math.toRadians(1);
        angleY += Math.toRadians(1);
        orbitAngle += Math.toRadians(2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Convert to Graphics2D
        Graphics2D g2d = (Graphics2D) g;
        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set up coordinate system: translate to center
        g2d.translate(WIDTH / 2, HEIGHT / 2);

        // Draw main cube
        drawCube(g2d, 0, 0, 0, 150, angleX, angleY, Color.CYAN);

        // Calculate orbiting cube position
        double orbitRadius = 250;
        double xOffset = orbitRadius * Math.cos(orbitAngle);
        double yOffset = orbitRadius * Math.sin(orbitAngle);

        // Draw smaller orbiting cube
        drawCube(g2d, xOffset, yOffset, 0, 50, -angleX * 1.5, -angleY * 1.5, Color.ORANGE);
    }

    /**
     * Draws a 3D cube centered at (cx, cy, cz) with given size and rotation angles.
     */
    private void drawCube(Graphics2D g2d, double cx, double cy, double cz, double size, double rotX, double rotY, Color color) {
        // Define 8 vertices of the cube
        double hs = size / 2; // half size
        Point3D[] vertices = new Point3D[] {
            new Point3D(-hs, -hs, -hs),
            new Point3D(hs, -hs, -hs),
            new Point3D(hs, hs, -hs),
            new Point3D(-hs, hs, -hs),
            new Point3D(-hs, -hs, hs),
            new Point3D(hs, -hs, hs),
            new Point3D(hs, hs, hs),
            new Point3D(-hs, hs, hs)
        };

        // Rotate vertices
        for (Point3D v : vertices) {
            v.rotateX(rotX);
            v.rotateY(rotY);
            v.translate(cx, cy, cz);
        }

        // Project vertices
        Point2D[] projected = new Point2D[8];
        double viewerDistance = 500;
        for (int i = 0; i < vertices.length; i++) {
            projected[i] = project(vertices[i], viewerDistance);
        }

        // Define edges between vertices
        int[][] edges = {
            {0,1},{1,2},{2,3},{3,0}, // Back face
            {4,5},{5,6},{6,7},{7,4}, // Front face
            {0,4},{1,5},{2,6},{3,7}  // Connecting edges
        };

        // Set color
        g2d.setColor(color);

        // Draw edges
        for (int[] edge : edges) {
            Point2D p1 = projected[edge[0]];
            Point2D p2 = projected[edge[1]];
            g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
        }
    }

    /**
     * Projects a 3D point onto 2D plane using simple perspective projection.
     */
    private Point2D project(Point3D point, double viewerDistance) {
        double factor = viewerDistance / (viewerDistance + point.z);
        double x = point.x * factor;
        double y = point.y * factor;
        return new Point2D(x, y);
    }

    /**
     * 3D Point class with rotation and translation methods.
     */
    private static class Point3D {
        double x, y, z;

        Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        void rotateX(double angle) {
            double cosA = Math.cos(angle);
            double sinA = Math.sin(angle);
            double y1 = y * cosA - z * sinA;
            double z1 = y * sinA + z * cosA;
            y = y1;
            z = z1;
        }

        void rotateY(double angle) {
            double cosA = Math.cos(angle);
            double sinA = Math.sin(angle);
            double x1 = x * cosA + z * sinA;
            double z1 = -x * sinA + z * cosA;
            x = x1;
            z = z1;
        }

        void translate(double dx, double dy, double dz) {
            x += dx;
            y += dy;
            z += dz;
        }
    }

    private static class Point2D {
        double x, y;

        Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("3D Rotating Cube with Orbiting Smaller Cube");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new RotatingCubeApp());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
