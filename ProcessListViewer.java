import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;

public class ProcessListViewer extends JFrame {
    private JTextArea processTextArea;
    private ScheduledExecutorService scheduler;

    public ProcessListViewer() {
        setTitle("Running Processes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize JTextArea inside a JScrollPane
        processTextArea = new JTextArea();
        processTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(processTextArea);

        add(scrollPane, BorderLayout.CENTER);

        // Start the scheduler to update process list every second
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::updateProcessList, 0, 1, TimeUnit.SECONDS);
    }

    private void updateProcessList() {
        StringBuilder sb = new StringBuilder();
        ProcessHandle.allProcesses()
            .forEach(ph -> {
                long pid = ph.pid();
                String cmd = ph.info().command().orElse("Unknown");
                String args = String.join(" ", ph.info().arguments().orElse(new String[]{}));
                sb.append(String.format("PID: %d | Command: %s %s%n", pid, cmd, args));
            });

        // Update GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            processTextArea.setText(sb.toString());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProcessListViewer viewer = new ProcessListViewer();
            viewer.setVisible(true);
        });
    }
}
