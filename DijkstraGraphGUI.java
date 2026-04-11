import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


public class DijkstraGraphGUI extends JFrame {
    private GraphPanel graphPanel;
    private JButton runDijkstraButton;
    private JComboBox<Node> startNodeComboBox;

    public DijkstraGraphGUI() {
        super("Dijkstra's Algorithm Graph");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        graphPanel = new GraphPanel();

        runDijkstraButton = new JButton("Run Dijkstra");
        runDijkstraButton.addActionListener(e -> {
            Node startNode = (Node) startNodeComboBox.getSelectedItem();
            if (startNode != null) {
                graphPanel.runDijkstra(startNode);
            }
        });

        startNodeComboBox = new JComboBox<>();

        JButton refreshButton = new JButton("Refresh Nodes");
        refreshButton.addActionListener(e -> {
            // Update combo box with current nodes
            startNodeComboBox.removeAllItems();
            for (Node node : graphPanel.nodes) {
                startNodeComboBox.addItem(node);
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Start Node:"));
        controlPanel.add(startNodeComboBox);
        controlPanel.add(runDijkstraButton);
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(graphPanel), BorderLayout.CENTER);

        // Initialize nodes list in combo box
        // Will be populated after nodes are added

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DijkstraGraphGUI());
    }

    // Inner class for the drawing panel
    class GraphPanel extends JPanel {
        java.util.List<Node> nodes = new ArrayList<>();
        java.util.List<Edge> edges = new ArrayList<>();
        Node selectedNode = null; // For connecting nodes
        Node startDijkstraNode = null;

        public GraphPanel() {
            setBackground(Color.WHITE);
            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    Point p = e.getPoint();
                    Node clickedNode = getNodeAtPoint(p);
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (clickedNode == null) {
                            // Add new node
                            Node newNode = new Node(p.x, p.y);
                            nodes.add(newNode);
                            repaint();
                        } else {
                            // Select node for connecting
                            selectedNode = clickedNode;
                        }
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        if (clickedNode != null) {
                            // Remove node and connected edges
                            removeNode(clickedNode);
                            repaint();
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        Node releasedNode = getNodeAtPoint(e.getPoint());
                        if (selectedNode != null && releasedNode != null && selectedNode != releasedNode) {
                            // Connect nodes
                            // For simplicity, weight is Euclidean distance
                            double weight = selectedNode.distanceTo(releasedNode);
                            Edge edge = new Edge(selectedNode, releasedNode, weight);
                            edges.add(edge);
                            repaint();
                        }
                        selectedNode = null;
                    }
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }

        private Node getNodeAtPoint(Point p) {
            for (Node node : nodes) {
                if (node.contains(p)) {
                    return node;
                }
            }
            return null;
        }

        private void removeNode(Node node) {
            nodes.remove(node);
            // Remove edges connected to this node
            edges.removeIf(edge -> edge.from == node || edge.to == node);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw edges
            g.setColor(Color.BLACK);
            for (Edge edge : edges) {
                g.drawLine(edge.from.x, edge.from.y, edge.to.x, edge.to.y);
                // Draw weight
                int midX = (edge.from.x + edge.to.x) / 2;
                int midY = (edge.from.y + edge.to.y) / 2;
                g.setColor(Color.RED);
                g.drawString(String.format("%.1f", edge.weight), midX, midY);
                g.setColor(Color.BLACK);
            }

            // Draw nodes
            for (Node node : nodes) {
                node.draw(g);
            }
        }

        // Run Dijkstra's algorithm from startNode
        public void runDijkstra(Node startNode) {
            // Initialize distances
            for (Node node : nodes) {
                node.distance = Double.POSITIVE_INFINITY;
                node.previous = null;
            }
            startNode.distance = 0;

            PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));
            queue.addAll(nodes);

            while (!queue.isEmpty()) {
                Node current = queue.poll();
                for (Edge edge : getEdgesFrom(current)) {
                    Node neighbor = (edge.from == current) ? edge.to : edge.from;
                    double newDist = current.distance + edge.weight;
                    if (newDist < neighbor.distance) {
                        neighbor.distance = newDist;
                        neighbor.previous = current;
                        // Re-insert neighbor into queue
                        queue.remove(neighbor);
                        queue.add(neighbor);
                    }
                }
            }

            // Highlight shortest paths
            repaint();

            // Display shortest distances
            StringBuilder result = new StringBuilder("Shortest distances from node " + startNode.id + ":\n");
            for (Node node : nodes) {
                result.append("Node ").append(node.id).append(": ");
                if (node.distance == Double.POSITIVE_INFINITY) {
                    result.append("Unreachable");
                } else {
                    result.append(String.format("%.2f", node.distance));
                }
                result.append("\n");
            }
            JOptionPane.showMessageDialog(this, result.toString());
        }

        private List<Edge> getEdgesFrom(Node node) {
            List<Edge> list = new ArrayList<>();
            for (Edge e : edges) {
                if (e.from == node || e.to == node) {
                    list.add(e);
                }
            }
            return list;
        }
    }

    // Node class
    static class Node {
        private static int idCounter = 1;
        int id;
        int x, y;
        double distance; // For Dijkstra
        Node previous;   // For path reconstruction
        private static final int RADIUS = 15;

        public Node(int x, int y) {
            this.id = idCounter++;
            this.x = x;
            this.y = y;
            this.distance = Double.POSITIVE_INFINITY;
            this.previous = null;
        }

        public boolean contains(Point p) {
            return (x - p.x) * (x - p.x) + (y - p.y) * (y - p.y) <= RADIUS * RADIUS;
        }

        public void draw(Graphics g) {
            g.setColor(Color.BLUE);
            g.fillOval(x - RADIUS, y - RADIUS, 2 * RADIUS, 2 * RADIUS);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(id), x - 4, y + 4);
            // Draw shortest path if applicable
            if (distance != Double.POSITIVE_INFINITY && previous != null && previous != this) {
                g.setColor(Color.GREEN);
                g.drawLine(x, y, previous.x, previous.y);
            }
        }

        public double distanceTo(Node other) {
            return Math.hypot(this.x - other.x, this.y - other.y);
        }

        @Override
        public String toString() {
            return "Node " + id;
        }
    }

    // Edge class
    static class Edge {
        Node from, to;
        double weight;

        public Edge(Node from, Node to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
}
