import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SimpleNotepad extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;

    public SimpleNotepad() {
        setTitle("Simple Notepad");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Initialize components
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open...");
        JMenuItem saveItem = new JMenuItem("Save As...");
        JMenuItem exitItem = new JMenuItem("Exit");

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem cutItem = new JMenuItem("Cut");
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");

        // Add menu items to File menu
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Add menu items to Edit menu
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);

        // Initialize file chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        // Action Listeners
        newItem.addActionListener(e -> textArea.setText(""));

        openItem.addActionListener(e -> openFile());

        saveItem.addActionListener(e -> saveFile());

        exitItem.addActionListener(e -> System.exit(0));

        cutItem.addActionListener(e -> textArea.cut());

        copyItem.addActionListener(e -> textArea.copy());

        pasteItem.addActionListener(e -> textArea.paste());

        // Add components to frame
        add(scrollPane, BorderLayout.CENTER);
    }

    private void openFile() {
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.read(reader, null);
            } catch (IOException ex) {
                showError("Error opening file: " + ex.getMessage());
            }
        }
    }

    private void saveFile() {
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                textArea.write(writer);
            } catch (IOException ex) {
                showError("Error saving file: " + ex.getMessage());
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimpleNotepad().setVisible(true);
        });
    }
}
