import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagePanel extends JPanel {
    private ThemeManager themeManager;
    private AuthManager authManager;
    private JTextArea messageArea;
    private JButton clearButton;
    private List<String> messages;
    private SimpleDateFormat timeFormat;
    
    public MessagePanel(ThemeManager themeManager, AuthManager authManager) {
        this.themeManager = themeManager;
        this.authManager = authManager;
        this.messages = new ArrayList<>();
        this.timeFormat = new SimpleDateFormat("HH:mm:ss");
        
        setLayout(new BorderLayout());
        initializeComponents();
        setupLayout();
        addInitialMessages();
    }
    
    private void initializeComponents() {
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Monospace", Font.PLAIN, 12));
        
        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 11));
        clearButton.addActionListener(e -> clearMessages());
        
        if (authManager.isGuestMode()) {
            clearButton.setEnabled(false);
            clearButton.setBackground(Color.GRAY);
        }
    }
    
    private void setupLayout() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        JLabel titleLabel = new JLabel("Messages & Notifications");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.add(clearButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Message area
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        // Guest mode info
        if (authManager.isGuestMode()) {
            JPanel guestPanel = new JPanel(new BorderLayout());
            guestPanel.setBackground(new Color(255, 243, 205)); // Light yellow
            guestPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            
            JLabel guestLabel = new JLabel("📌 Guest mode: Limited notification features. Sign up for full access.");
            guestLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            guestLabel.setForeground(new Color(146, 64, 14));
            
            guestPanel.add(guestLabel, BorderLayout.CENTER);
            add(guestPanel, BorderLayout.SOUTH);
        }
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addInitialMessages() {
        addMessage("INFO", "Strategy builder loaded successfully");
        
        if (authManager.isGuestMode()) {
            addMessage("WARNING", "Guest mode: Save and backtest features are disabled");
        } else {
            addMessage("SUCCESS", "Ready to build strategies");
        }
    }
    
    public void addMessage(String type, String message) {
        String timestamp = timeFormat.format(new Date());
        String formattedMessage = String.format("[%s] %s: %s", timestamp, type, message);
        messages.add(formattedMessage);
        updateDisplay();
    }
    
    public void addMessage(String message) {
        addMessage("INFO", message);
    }
    
    private void updateDisplay() {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append("\n");
        }
        messageArea.setText(sb.toString());
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }
    
    private void clearMessages() {
        if (authManager.isGuestMode()) return;
        
        messages.clear();
        messageArea.setText("");
        addMessage("INFO", "Messages cleared");
    }
    
    public void applyTheme() {
        setBackground(themeManager.getBackground());
        messageArea.setBackground(themeManager.getCardBackground());
        messageArea.setForeground(themeManager.getText());
        clearButton.setBackground(themeManager.getCardBackground());
        clearButton.setForeground(themeManager.getText());
        
        applyThemeToComponent(this);
        repaint();
    }
    
    private void applyThemeToComponent(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel) {
                component.setForeground(themeManager.getText());
            } else if (component instanceof Container) {
                component.setBackground(themeManager.getBackground());
                applyThemeToComponent((Container) component);
            }
        }
    }
}