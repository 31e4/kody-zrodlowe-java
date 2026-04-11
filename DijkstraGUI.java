import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DijkstraGUI extends JFrame {
    private JTextField verticesField;
    private JTextArea matrixArea;
    private JComboBox<Integer> startVertexBox;
    private JButton computeButton;
    private JTextArea resultArea;

    private int[][] adjacencyMatrix;
    private int numVertices;

    public DijkstraGUI() {
        setTitle("Dijkstra's Algorithm");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Panel for inputs
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));

        // Number of vertices
        inputPanel.add(new JLabel("Number of vertices:"));
        verticesField = new JTextField("5");
        inputPanel.add(verticesField);

        // Adjacency matrix input
        inputPanel.add(new JLabel("Adjacency matrix (rows separated by new lines, entries separated by spaces):"));
        matrixArea = new JTextArea(10, 40);
        JScrollPane matrixScrollPane = new JScrollPane(matrixArea);

        // Starting vertex selection
        inputPanel.add(new JLabel("Start vertex:"));
        startVertexBox = new JComboBox<>();
        inputPanel.add(startVertexBox);

        // Compute button
        computeButton = new JButton("Compute Shortest Paths");
        computeButton.addActionListener(e -> computePaths());

        // Result display
        resultArea = new JTextArea(10, 50);
        resultArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        // Add components to frame
        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.NORTH);
        add(matrixScrollPane, BorderLayout.CENTER);
        add(computeButton, BorderLayout.SOUTH);
        add(resultScrollPane, BorderLayout.EAST);

        // Initialize default matrix
        updateVerticesAndMatrix();
        // Add listener for vertices input
        verticesField.addActionListener(e -> updateVerticesAndMatrix());
    }

    private void updateVerticesAndMatrix() {
        try {
            numVertices = Integer.parseInt(verticesField.getText().trim());
            if (numVertices <= 0) throw new NumberFormatException();

            // Initialize adjacency matrix with zeros
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numVertices; i++) {
                for (int j = 0; j < numVertices; j++) {
                    sb.append("0 ");
                }
                sb.append("\n");
            }
            matrixArea.setText(sb.toString());

            // Update start vertex options
            startVertexBox.removeAllItems();
            for (int i = 0; i < numVertices; i++) {
                startVertexBox.addItem(i);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive integer for vertices.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void computePaths() {
        try {
            // Parse adjacency matrix input
            String[] lines = matrixArea.getText().split("\n");
            if (lines.length != numVertices) {
                throw new IllegalArgumentException("Number of rows in adjacency matrix does not match number of vertices.");
            }

            adjacencyMatrix = new int[numVertices][numVertices];

            for (int i = 0; i < numVertices; i++) {
                String[] entries = lines[i].trim().split("\\s+");
                if (entries.length != numVertices) {
                    throw new IllegalArgumentException("Row " + (i + 1) + " does not have " + numVertices + " entries.");
                }
                for (int j = 0; j < numVertices; j++) {
                    int weight = Integer.parseInt(entries[j]);
                    if (weight < 0) {
                        throw new IllegalArgumentException("Negative weights are not allowed.");
                    }
                    adjacencyMatrix[i][j] = weight;
                }
            }

            int startVertex = (int) startVertexBox.getSelectedItem();
            int[] distances = dijkstra(adjacencyMatrix, startVertex);

            // Display results
            StringBuilder sb = new StringBuilder();
            sb.append("Shortest distances from vertex ").append(startVertex).append(":\n");
            for (int i = 0; i < numVertices; i++) {
                sb.append("To vertex ").append(i).append(": ");
                if (distances[i] == Integer.MAX_VALUE) {
                    sb.append("Unreachable");
                } else {
                    sb.append(distances[i]);
                }
                sb.append("\n");
            }
            resultArea.setText(sb.toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Implementation of Dijkstra's Algorithm
    private int[] dijkstra(int[][] graph, int src) {
        int n = graph.length;
        int[] dist = new int[n];
        boolean[] visited = new boolean[n];

        // Initialize distances
        for (int i = 0; i < n; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        dist[src] = 0;

        for (int count = 0; count < n - 1; count++) {
            int u = minDistance(dist, visited);
            if (u == -1) break; // All reachable vertices processed
            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (!visited[v] && graph[u][v] != 0 && dist[u] != Integer.MAX_VALUE
                        && dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                }
            }
        }
        return dist;
    }

    private int minDistance(int[] dist, boolean[] visited) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int v = 0; v < dist.length; v++) {
            if (!visited[v] && dist[v] <= min) {
                min = dist[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DijkstraGUI().setVisible(true);
        });
    }
}
