import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ProcessThreadGUI extends JFrame {

    public ProcessThreadGUI() {
        setTitle("Process and Thread Creator");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create buttons
        JButton processButton = new JButton("Create Process");
        JButton threadButton = new JButton("Create Thread");

        // Set layout
        setLayout(new FlowLayout());

        // Add buttons to frame
        add(processButton);
        add(threadButton);

        // Action for creating a process
        processButton.addActionListener(e -> createProcess());

        // Action for creating a thread
        threadButton.addActionListener(e -> createThread());
    }

    private void createProcess() {
        try {
            // Example: open default web browser to a website
            String url = "https://www.google.com";

            // Detect OS and execute appropriate command
            String osName = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;

            if (osName.contains("windows")) {
                processBuilder = new ProcessBuilder("cmd", "/c", "start", url);
            } else if (osName.contains("mac")) {
                processBuilder = new ProcessBuilder("open", url);
            } else { // assume Linux or Unix
                processBuilder = new ProcessBuilder("xdg-open", url);
            }

            processBuilder.start();
            JOptionPane.showMessageDialog(this, "Process to open browser started.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to start process: " + ex.getMessage());
        }
    }

    private void createThread() {
        Thread thread = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                System.out.println("Thread working... step " + i);
                try {
                    Thread.sleep(1000); // sleep for 1 second
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted");
                }
            }
            System.out.println("Thread finished work");
        });
        thread.start();
        JOptionPane.showMessageDialog(this, "Thread has been started. Check console output.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProcessThreadGUI app = new ProcessThreadGUI();
            app.setVisible(true);
        });
    }
}
