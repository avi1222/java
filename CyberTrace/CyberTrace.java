import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class CyberTrace extends JFrame {

    private final JLabel totalLabel = new JLabel("0");
    private final JLabel failuresLabel = new JLabel("0");
    private final JLabel alertsLabel = new JLabel("0");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{"Timestamp", "Level", "IP Address",
                            "Event", "Detection"},
                    0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(tableModel);
    private final JTextArea reportArea = new JTextArea(5, 30);

    private final List<LogEntry> entries = new ArrayList<>();
    private final Map<String, Integer> failedLogins = new HashMap<>();

    private static final int ALERT_THRESHOLD = 3;

    public CyberTrace() {
        setTitle("CyberTrace | Security Log Analyzer");
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildInterface();
    }

    private void buildInterface() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(20, 26, 38));
        setContentPane(root);

        JLabel title = new JLabel("CYBERTRACE");
        title.setFont(new Font("Monospaced", Font.BOLD, 28));
        title.setForeground(new Color(70, 220, 170));

        JLabel subtitle = new JLabel(
                "Security Log Analyzer | Defensive Monitoring"
        );
        subtitle.setForeground(Color.LIGHT_GRAY);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);

        JButton openButton = new JButton("Open Log File");
        openButton.addActionListener(e -> chooseLogFile());

        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);
        top.add(heading, BorderLayout.CENTER);
        top.add(openButton, BorderLayout.EAST);

        JPanel cards = new JPanel(new GridLayout(1, 3, 12, 0));
        cards.setOpaque(false);
        cards.add(createCard("TOTAL ENTRIES", totalLabel));
        cards.add(createCard("FAILED LOGINS", failuresLabel));
        cards.add(createCard("SUSPICIOUS IPs", alertsLabel));

        table.setRowHeight(26);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        JScrollPane tableScroll = new JScrollPane(table);

        reportArea.setEditable(false);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        reportArea.setBackground(new Color(14, 19, 29));
        reportArea.setForeground(new Color(100, 230, 180));
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setText(
                "Welcome to CyberTrace.\n" +
                "Open a log file to begin analysis.\n" +
                "Alert threshold: " + ALERT_THRESHOLD +
                " failed logins from one IP address."
        );

        JScrollPane reportScroll = new JScrollPane(reportArea);
        reportScroll.setBorder(
                BorderFactory.createTitledBorder("Analysis Report")
        );

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.add(cards, BorderLayout.NORTH);
        center.add(tableScroll, BorderLayout.CENTER);
        center.add(reportScroll, BorderLayout.SOUTH);

        root.add(top, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, JLabel value) {
        JPanel card = new JPanel(new BorderLayout(4, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 75, 95)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setBackground(new Color(30, 39, 54));

        JLabel heading = new JLabel(title);
        heading.setForeground(Color.LIGHT_GRAY);
        heading.setFont(new Font("SansSerif", Font.BOLD, 12));

        value.setForeground(new Color(70, 220, 170));
        value.setFont(new Font("SansSerif", Font.BOLD, 26));

        card.add(heading, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    private void chooseLogFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a server log file");

        int result = chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        try {
            analyzeFile(file);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not read the file:\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void analyzeFile(File file) throws IOException {
        entries.clear();
        failedLogins.clear();
        tableModel.setRowCount(0);

        int totalLines = 0;
        int failures = 0;
        int malformed = 0;

        try (BufferedReader reader = Files.newBufferedReader(
                file.toPath(), StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                totalLines++;

                String[] parts = line.trim().split("\\s+", 5);

                // Expected format:
                // DATE TIME LEVEL IP ADDRESS EVENT
                if (parts.length < 5) {
                    malformed++;
                    continue;
                }

                String timestamp = parts[0] + " " + parts[1];
                String level = parts[2];
                String ip = parts[3];
                String event = parts[4];

                boolean failed = event.toLowerCase(Locale.ROOT)
                        .contains("login failed");

                if (failed) {
                    failures++;
                    failedLogins.merge(ip, 1, Integer::sum);
                }

                LogEntry entry = new LogEntry(
                        timestamp, level, ip, event
                );

                entries.add(entry);
            }
        }

        int suspiciousIPs = 0;

        for (LogEntry entry : entries) {
            int count = failedLogins.getOrDefault(entry.ip, 0);

            String detection = count >= ALERT_THRESHOLD
                    ? "REPEATED FAILURES"
                    : "-";

            if (count >= ALERT_THRESHOLD
                    && entry.event.toLowerCase(Locale.ROOT)
                    .contains("login failed")) {
                // Detection is shown per event, but the summary
                // counts each suspicious IP only once.
            }

            tableModel.addRow(new Object[]{
                    entry.timestamp,
                    entry.level,
                    entry.ip,
                    entry.event,
                    detection
            });
        }

        for (int count : failedLogins.values()) {
            if (count >= ALERT_THRESHOLD) {
                suspiciousIPs++;
            }
        }

        totalLabel.setText(String.valueOf(totalLines));
        failuresLabel.setText(String.valueOf(failures));
        alertsLabel.setText(String.valueOf(suspiciousIPs));

        StringBuilder report = new StringBuilder();
        report.append("File: ").append(file.getName()).append("\n");
        report.append("Total lines: ").append(totalLines).append("\n");
        report.append("Parsed entries: ").append(entries.size()).append("\n");
        report.append("Malformed or unsupported lines: ")
                .append(malformed).append("\n");
        report.append("Failed login events: ").append(failures).append("\n");
        report.append("Suspicious IPs: ").append(suspiciousIPs).append("\n\n");

        report.append("IP ADDRESSES WITH REPEATED FAILURES\n");
        report.append("----------------------------------\n");

        boolean found = false;

        for (Map.Entry<String, Integer> item : failedLogins.entrySet()) {
            if (item.getValue() >= ALERT_THRESHOLD) {
                report.append(item.getKey())
                        .append(" - ")
                        .append(item.getValue())
                        .append(" failed logins\n");
                found = true;
            }
        }

        if (!found) {
            report.append("No IP reached the alert threshold.\n");
        }

        report.append("\nNote: These are indicators for investigation, ");
        report.append("not proof of malicious activity.");

        reportArea.setText(report.toString());
    }

    private static class LogEntry {
        String timestamp;
        String level;
        String ip;
        String event;

        LogEntry(String timestamp, String level,
                 String ip, String event) {
            this.timestamp = timestamp;
            this.level = level;
            this.ip = ip;
            this.event = event;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
                // Use the default Swing appearance.
            }

            new CyberTrace().setVisible(true);
        });
    }
}
