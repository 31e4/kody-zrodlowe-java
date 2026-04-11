import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.io.*;
import com.google.gson.*;
import com.google.gson.reflect.*;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class AdvancedTaskManager extends JFrame {

    private JPanel taskListPanel;
    private JScrollPane scrollPane;
    private List<Task> tasks;
    private DefaultListModel<Task> taskModel;
    private boolean darkMode = false;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final String DATA_FILE = "tasks.json";

    public AdvancedTaskManager() {
        setTitle("Advanced Task Manager");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true); // For custom window dragging
        initUI();
        loadTasks();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveTasks();
                executor.shutdownNow();
            }
        });
    }

    private void initUI() {
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // Top bar with title and buttons
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JLabel titleLabel = new JLabel("Task Manager");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topBar.add(titleLabel, BorderLayout.WEST);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton addButton = new JButton("Add Task");
        JButton themeButton = new JButton("Toggle Theme");
        JButton closeButton = new JButton("X");
        closeButton.setForeground(Color.RED);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setPreferredSize(new Dimension(40, 30));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addButton.addActionListener(e -> showAddTaskDialog());
        themeButton.addActionListener(e -> toggleTheme());
        closeButton.addActionListener(e -> dispose());

        buttonsPanel.add(addButton);
        buttonsPanel.add(themeButton);
        buttonsPanel.add(closeButton);

        topBar.add(buttonsPanel, BorderLayout.EAST);

        // Make window draggable
        MouseAdapter dragListener = new MouseAdapter() {
            Point initialClick;

            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                // Determine how much the mouse moved since the initial click
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // Move window
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        };
        topBar.addMouseListener(dragListener);
        topBar.addMouseMotionListener(dragListener);

        mainPanel.add(topBar, BorderLayout.NORTH);

        // Task list panel within scroll pane
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setBackground(getBackground());

        scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smoother scrolling
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Load tasks into UI
        tasks = new ArrayList<>();
        refreshTaskList();
        applyTheme();
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        applyTheme();
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void applyTheme() {
        Color bgColor = darkMode ? new Color(30,30,30) : Color.WHITE;
        Color fgColor = darkMode ? Color.WHITE : Color.BLACK;
        getContentPane().setBackground(bgColor);
        taskListPanel.setBackground(bgColor);
        for (Component comp : taskListPanel.getComponents()) {
            if (comp instanceof TaskPanel) {
                ((TaskPanel) comp).updateTheme(darkMode);
            }
        }
    }

    private void showAddTaskDialog() {
        JTextField taskField = new JTextField(20);
        Object[] message = {
            "Task Name:", taskField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Add New Task", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String taskName = taskField.getText().trim();
            if (!taskName.isEmpty()) {
                Task task = new Task(taskName);
                tasks.add(task);
                addTaskToUI(task);
            }
        }
    }

    private void refreshTaskList() {
        taskListPanel.removeAll();
        for (Task task : tasks) {
            addTaskToUI(task);
        }
        taskListPanel.revalidate();
        taskListPanel.repaint();
    }

    private void addTaskToUI(Task task) {
        TaskPanel taskPanel = new TaskPanel(task);
        taskPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        taskPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        taskListPanel.add(taskPanel);
        taskListPanel.add(Box.createVerticalStrut(5));
        taskPanel.startProgressAnimation();
    }

    private void showNotification(String message) {
        JDialog dialog = new JDialog(this, false);
        dialog.setUndecorated(true);
        JLabel label = new JLabel(message);
        label.setOpaque(true);
        label.setBackground(new Color(0,0,0,150));
        label.setForeground(Color.WHITE);
        label.setBorder(new EmptyBorder(10, 20, 10, 20));
        dialog.add(label);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        javax.swing.Timer timer = new javax.swing.Timer(2000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
        dialog.setVisible(true);
    }

    private void loadTasks() {
        try (Reader reader = new FileReader(DATA_FILE)) {
            Gson gson = new Gson();
            Type taskListType = new TypeToken<List<Task>>(){}.getType();
            List<Task> loadedTasks = gson.fromJson(reader, taskListType);
            if (loadedTasks != null) {
                tasks = loadedTasks;
                for (Task t : tasks) {
                    addTaskToUI(t);
                }
            }
        } catch (IOException e) {
            // No saved data
        }
    }

    private void saveTasks() {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner class for Task representation
    private static class Task {
        String name;
        int progress; // 0 to 100
        boolean completed;

        Task(String name) {
            this.name = name;
            this.progress = 0;
            this.completed = false;
        }
    }

    // Inner class for Task Panel UI
    private class TaskPanel extends JPanel {
        private Task task;
        private JLabel nameLabel;
        private JProgressBar progressBar;
        private JButton completeButton;
        private JPopupMenu contextMenu;
        private boolean animatedProgress = false;

        public TaskPanel(Task task) {
            this.task = task;
            setLayout(new BorderLayout(10, 10));
            setBorder(new LineBorder(Color.GRAY, 1, true));
            setBackground(getBackground());

            nameLabel = new JLabel(task.name);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            add(nameLabel, BorderLayout.WEST);

            progressBar = new JProgressBar(0, 100);
            progressBar.setValue(task.progress);
            progressBar.setStringPainted(true);
            progressBar.setPreferredSize(new Dimension(150, 20));
            add(progressBar, BorderLayout.CENTER);

            completeButton = new JButton(task.completed ? "✓" : "Done");
            completeButton.setFocusPainted(false);
            completeButton.setBackground(task.completed ? new Color(0,128,0) : new Color(0,0,128));
            completeButton.setForeground(Color.WHITE);
            completeButton.setPreferredSize(new Dimension(80, 30));
            completeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            completeButton.addActionListener(e -> toggleComplete());

            add(completeButton, BorderLayout.EAST);

            // Context menu
            contextMenu = new JPopupMenu();
            JMenuItem deleteItem = new JMenuItem("Delete");
            deleteItem.addActionListener(e -> deleteTask());
            JMenuItem editItem = new JMenuItem("Edit");
            editItem.addActionListener(e -> editTaskName());
            contextMenu.add(editItem);
            contextMenu.add(deleteItem);

            // Mouse listener for right-click
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) showContextMenu(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) showContextMenu(e);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                        showEditDialog();
                    }
                }

                private void showContextMenu(MouseEvent e) {
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            });
        }

        public void updateTheme(boolean dark) {
            setBackground(dark ? new Color(50,50,50) : Color.WHITE);
            nameLabel.setForeground(dark ? Color.WHITE : Color.BLACK);
            progressBar.setBackground(dark ? new Color(70,70,70) : Color.LIGHT_GRAY);
            progressBar.setForeground(dark ? Color.CYAN : Color.BLUE);
        }

        private void toggleComplete() {
            task.completed = !task.completed;
            completeButton.setText(task.completed ? "✓" : "Done");
            completeButton.setBackground(task.completed ? new Color(0,128,0) : new Color(0,0,128));
            showNotification("Task '" + task.name + "' marked as " + (task.completed ? "completed." : "incomplete."));
        }

        private void deleteTask() {
            int confirm = JOptionPane.showConfirmDialog(AdvancedTaskManager.this, "Delete task: " + task.name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tasks.remove(task);
                taskListPanel.remove(this);
                taskListPanel.revalidate();
                taskListPanel.repaint();
            }
        }

        private void editTaskName() {
            String newName = JOptionPane.showInputDialog(AdvancedTaskManager.this, "Edit Task Name:", task.name);
            if (newName != null && !newName.trim().isEmpty()) {
                task.name = newName.trim();
                nameLabel.setText(task.name);
            }
        }

        private void showEditDialog() {
            editTaskName();
        }

        public void startProgressAnimation() {
            animatedProgress = true;
            new Thread(() -> {
                while (animatedProgress && task.progress < 100) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    SwingUtilities.invokeLater(() -> {
                        task.progress += 1;
                        progressBar.setValue(task.progress);
                        if (task.progress >= 100) {
                            task.progress = 100;
                            animatedProgress = false;
                        }
                    });
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        // Optional: Set look and feel for modern UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fallback
        }
        SwingUtilities.invokeLater(() -> {
            AdvancedTaskManager manager = new AdvancedTaskManager();
            manager.setVisible(true);
        });
    }
}
