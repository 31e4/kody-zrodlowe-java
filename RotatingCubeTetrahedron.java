import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class RotatingCubeTetrahedron extends JPanel implements ActionListener {
    private Timer timer;

    // Rotation angles
    private double angleX = 0;
    private double angleY = 0;

    // Rotation speeds
    private final double deltaX = Math.toRadians(1);
    private final double deltaY = Math.toRadians(1);

    public RotatingCubeTetrahedron() {
        setPreferredSize(new Dimension(800, 800));
        setBackground(Color.BLACK);
        timer = new Timer(16, this); // roughly 60 FPS
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        angleX += deltaX;
        angleY += deltaY;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Enable better rendering
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Set origin to center
        AffineTransform originalTransform = g2.getTransform();
        g2.translate(width / 2, height / 2);

        // Draw the cube
        drawCube(g2);

        // Draw the tetrahedron inside the cube
        drawTetrahedron(g2);

        // Restore original transform
        g2.setTransform(originalTransform);
    }

    // Define cube vertices
    private Point3D[] cubeVertices = {
        new Point3D(-1, -1, -1),
        new Point3D(1, -1, -1),
        new Point3D(1, 1, -1),
        new Point3D(-1, 1, -1),
        new Point3D(-1, -1, 1),
        new Point3D(1, -1, 1),
        new Point3D(1, 1, 1),
        new Point3D(-1, 1, 1)
    };

    // Define cube edges by vertex indices
    private int[][] cubeEdges = {
        {0,1},{1,2},{2,3},{3,0}, // bottom face
        {4,5},{5,6},{6,7},{7,4}, // top face
        {0,4},{1,5},{2,6},{3,7}  // sides
    };

    // Define tetrahedron vertices
    private Point3D[] tetraVertices = {
        new Point3D(0, 1, 0),
        new Point3D(-1, -1, 1),
        new Point3D(1, -1, 1),
        new Point3D(0, -1, -1)
    };

    // Tetrahedron edges
    private int[][] tetraEdges = {
        {0,1},{0,2},{0,3},
        {1,2},{2,3},{3,1}
    };

    private void drawCube(Graphics2D g2) {
        // Rotate cube
        Point3D[] rotatedVertices = new Point3D[cubeVertices.length];
        for (int i=0; i<cubeVertices.length; i++) {
            rotatedVertices[i] = rotatePoint(cubeVertices[i], angleX, angleY);
        }

        // Perspective projection and scaling
        Point2D[] projected = new Point2D[cubeVertices.length];
        double scale = 200; // scaling factor for visualization
        for (int i=0; i<cubeVertices.length; i++) {
            projected[i] = projectPoint(rotatedVertices[i], scale);
        }

        // Draw edges
        g2.setColor(Color.WHITE);
        for (int[] edge : cubeEdges) {
            Point2D p1 = projected[edge[0]];
            Point2D p2 = projected[edge[1]];
            g2.draw(new Line2D.Double(p1, p2));
        }
    }

    private void drawTetrahedron(Graphics2D g2) {
        // Smaller tetrahedron inside the cube
        double scaleFactor = 0.5; // smaller size
        Point3D[] scaledVertices = new Point3D[tetraVertices.length];
        for (int i=0; i<tetraVertices.length; i++) {
            // Scale
            Point3D v = tetraVertices[i];
            Point3D scaled = new Point3D(v.x * scaleFactor, v.y * scaleFactor, v.z * scaleFactor);
            // Rotate
            scaledVertices[i] = rotatePoint(scaled, angleX * 1.5, angleY * 1.5);
        }

        // Project
        Point2D[] projected = new Point2D[scaledVertices.length];
        double scale = 200; // same as cube for consistency
        for (int i=0; i<scaledVertices.length; i++) {
            projected[i] = projectPoint(scaledVertices[i], scale);
        }

        // Draw edges
        g2.setColor(Color.CYAN);
        for (int[] edge : tetraEdges) {
            Point2D p1 = projected[edge[0]];
            Point2D p2 = projected[edge[1]];
            g2.draw(new Line2D.Double(p1, p2));
        }
    }

    // Helper classes and methods

    // 3D point class
    private static class Point3D {
        double x, y, z;
        Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    // Rotate point around X and Y axes
    private Point3D rotatePoint(Point3D p, double angleX, double angleY) {
        // Rotation around X axis
        double sinX = Math.sin(angleX);
        double cosX = Math.cos(angleX);
        double y1 = p.y * cosX - p.z * sinX;
        double z1 = p.y * sinX + p.z * cosX;

        // Rotation around Y axis
        double sinY = Math.sin(angleY);
        double cosY = Math.cos(angleY);
        double x2 = p.x * cosY + z1 * sinY;
        double z2 = -p.x * sinY + z1 * cosY;

        return new Point3D(x2, y1, z2);
    }

    // Perspective projection
    private Point2D projectPoint(Point3D p, double scale) {
        double perspective = 4; // distance from viewer
        double zOffset = 3; // to avoid division by zero
        double factor = perspective / (p.z + zOffset);
        double x2d = p.x * factor * scale;
        double y2d = p.y * factor * scale;
        return new Point2D.Double(x2d, y2d);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("3D Rotating Cube with Tetrahedron");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            RotatingCubeTetrahedron panel = new RotatingCubeTetrahedron();
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
