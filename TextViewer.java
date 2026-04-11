import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class TextViewer extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;

    public TextViewer() {
        super("Text Viewer");

        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create the File menu
        JMenu fileMenu = new JMenu("File");

        // Create menu items
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem exitItem = new JMenuItem("Exit");

        // Add action listener for Open
        openItem.addActionListener(e -> openFile());

        // Add action listener for Exit
        exitItem.addActionListener(e -> System.exit(0));

        // Assemble the menu
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Set the menu bar
        setJMenuBar(menuBar);

        // Initialize the text area
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Add the scroll pane to the frame
        add(scrollPane, BorderLayout.CENTER);

        // Initialize the file chooser
        fileChooser = new JFileChooser();

        // Configure the frame
        setSize(800, 600);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void openFile() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadFile(selectedFile);
        }
    }

    private void loadFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            textArea.setText(""); // Clear previous content
            String line;
            while ((line = reader.readLine()) != null) {
                textArea.append(line);
                textArea.append(System.lineSeparator());
            }
            setTitle("Text Viewer - " + file.getName());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error reading file: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Ensure GUI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new TextViewer().setVisible(true);
        });
    }
}
