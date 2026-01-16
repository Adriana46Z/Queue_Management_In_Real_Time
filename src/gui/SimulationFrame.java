package gui;

import businesslogic.SimulationManager;
import model.Client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

public class SimulationFrame extends JFrame {

    private final Color DARK_GREEN = new Color(0, 100, 0);
    private final Color FOREST_GREEN = new Color(34, 139, 34);
    private final Color MINT_CREAM = new Color(245, 255, 250);

    private JPanel inputPanel;
    private JPanel outputPanel;
    private CardLayout cardLayout;
    private JTextField[] inputFields;
    private JTextArea generatedClientsArea;
    private JTextArea logArea;
    private SimulationManager manager;
    private Thread simulationThread;
    private JButton exportLogButton;

    public SimulationFrame() {
        setTitle("Queue Simulation Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
                System.exit(0);
            }
        });

        manager = new SimulationManager(this);

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        createInputPanel();
        createOutputPanel();

        add(inputPanel, "first");
        add(outputPanel, "second");

        testInitialDisplay();

        cardLayout.show(getContentPane(), "first");
        setVisible(true);
    }

    private void cleanup() {
        if (simulationThread != null && simulationThread.isAlive()) {
            manager.stopSimulation();
            simulationThread.interrupt();
            try {
                simulationThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        manager.cleanup();
    }

    private void testInitialDisplay() {
        generatedClientsArea.setText("Waiting for client generation...");
    }

    private void createInputPanel() {
        inputPanel = new JPanel(new BorderLayout(20, 20));
        inputPanel.setBackground(MINT_CREAM);
        inputPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel header = new JLabel("SIMULATION PARAMETERS", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(DARK_GREEN);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        String[] labels = {
                "Number of Clients:", "Min Arrival Time:", "Max Arrival Time:",
                "Min Service Time:", "Max Service Time:", "Number of Queues:",
                "Simulation Time Limit:"
        };

        JPanel fieldsPanel = new JPanel(new GridLayout(7, 2, 15, 15));
        fieldsPanel.setBackground(MINT_CREAM);
        inputFields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Verdana", Font.PLAIN, 16));
            label.setForeground(DARK_GREEN);
            fieldsPanel.add(label);

            inputFields[i] = new JTextField();
            inputFields[i].setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputFields[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FOREST_GREEN, 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            fieldsPanel.add(inputFields[i]);
        }

        JButton generateBtn = createStyledButton("GENERATE & VIEW RESULTS", FOREST_GREEN, Color.BLACK);
        generateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateClients();
            }
        });

        inputPanel.add(header, BorderLayout.NORTH);
        inputPanel.add(fieldsPanel, BorderLayout.CENTER);
        inputPanel.add(generateBtn, BorderLayout.SOUTH);
    }

    private void createOutputPanel() {
        outputPanel = new JPanel(new BorderLayout(20, 20));
        outputPanel.setBackground(MINT_CREAM);
        outputPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel header = new JLabel("GENERATED DATA", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(DARK_GREEN);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        contentPanel.setBackground(MINT_CREAM);

        JPanel clientsPanel = createStyledPanel("GENERATED CLIENTS");
        generatedClientsArea = createStyledTextArea();
        clientsPanel.add(new JScrollPane(generatedClientsArea));

        JPanel logPanel = createStyledPanel("SIMULATION LOG");
        logArea = createStyledTextArea();
        logPanel.add(new JScrollPane(logArea));

        contentPanel.add(clientsPanel);
        contentPanel.add(logPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.setBackground(MINT_CREAM);

        JButton backBtn = createStyledButton("BACK TO PARAMETERS", DARK_GREEN, Color.BLACK);
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(getContentPane(), "first");
            }
        });

        JButton startBtn = createStyledButton("START SIMULATION", FOREST_GREEN, Color.BLACK);
        startBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });

        exportLogButton = createStyledButton("EXPORT RESULTS", new Color(139, 69, 19), Color.BLACK);
        exportLogButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportLog();
            }
        });
        exportLogButton.setEnabled(false);

        buttonPanel.add(backBtn);
        buttonPanel.add(startBtn);
        buttonPanel.add(exportLogButton);

        outputPanel.add(header, BorderLayout.NORTH);
        outputPanel.add(contentPanel, BorderLayout.CENTER);
        outputPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 2),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MINT_CREAM);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(FOREST_GREEN, 2), title,
                0, 0, new Font("Segoe UI", Font.BOLD, 16), DARK_GREEN
        ));
        return panel;
    }

    private JTextArea createStyledTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Verdana", Font.PLAIN, 14));
        textArea.setForeground(DARK_GREEN);
        textArea.setBackground(MINT_CREAM);
        textArea.setBorder(new EmptyBorder(10, 15, 10, 15));
        textArea.setEditable(false);
        return textArea;
    }

    private void generateClients() {
        try {
            int[] values = new int[inputFields.length];
            for (int i = 0; i < inputFields.length; i++) {
                values[i] = Integer.parseInt(inputFields[i].getText());
                if (values[i] < 0) throw new NumberFormatException();
            }

            manager.updateParameters(
                    values[5], values[0],
                    values[1], values[2],
                    values[3], values[4]
            );

            manager.generateRandomClients();
            cardLayout.show(getContentPane(), "second");
            exportLogButton.setEnabled(false);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid positive numbers in all fields",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startSimulation() {
        try {
            int timeLimit = Integer.parseInt(inputFields[6].getText());
            manager.setTimeLimit(timeLimit);

            logArea.setText("");
            log("Starting simulation...");

            currentSimulationLogged = false;

            if (simulationThread != null && simulationThread.isAlive()) {
                manager.stopSimulation();
                simulationThread.interrupt();
                try {
                    simulationThread.join(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }

            simulationThread = new Thread(new Runnable() {
                public void run() {
                    manager.runSimulation();

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            exportLogButton.setEnabled(true);
                        }
                    });
                }
            });
            simulationThread.start();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid time limit",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportLog() {
        try {
            if (!currentSimulationLogged) {
                PrintWriter writer = new PrintWriter(new FileWriter("log1.txt", true)); // true = append
                writer.println("\n SIMULATION RESULTS - " + new Date());
                writer.println("\n");

                writer.println(logArea.getText());
                writer.close();

                currentSimulationLogged = true; //impiedica adaugarea unei simulari de 2 ori in log

                JOptionPane.showMessageDialog(this,
                        "Results exported to log1.txt",
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "These simulation results have already been exported to log1.txt",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to export results: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showGeneratedClients(final List<Client> clients) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                StringBuilder sb = new StringBuilder();
                for (Client c : clients) {
                    sb.append(" Client ").append(c.getId()).append("   Arrival time: ").append(c.getArrivalTime())
                            .append("   Service time: ").append(c.getServiceTime())
                            .append("\n");
                }
                generatedClientsArea.setText(sb.toString());
                log("Generated " + clients.size() + " clients");
            }
        });
    }

    private boolean currentSimulationLogged = false;

    public void log(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                logArea.append(message + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new SimulationFrame();
            }
        });
    }
}