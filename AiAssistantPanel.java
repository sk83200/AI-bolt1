import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AiAssistantPanel extends JPanel {
    private ThemeManager themeManager;
    private AuthManager authManager;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private List<ChatMessage> messages;
    private Consumer<StrategyData> strategyUpdateListener;
    
    public AiAssistantPanel(ThemeManager themeManager, AuthManager authManager) {
        this.themeManager = themeManager;
        this.authManager = authManager;
        this.messages = new ArrayList<>();
        
        setLayout(new BorderLayout());
        initializeComponents();
        setupLayout();
        
        // Add initial message
        addMessage(new ChatMessage("assistant", 
            authManager.isGuestMode() ? 
            "Hello! I'm your AI strategy assistant in guest mode. I can provide sample strategies and general guidance. For full customization, please sign up!" :
            "Hello! I'm your AI strategy assistant. I can help you build, optimize, and analyze trading strategies. What kind of strategy would you like to create?"));
    }
    
    private void initializeComponents() {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.addActionListener(e -> sendMessage());
        
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
    }
    
    private void setupLayout() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("AI Strategy Assistant");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel statusLabel = new JLabel(authManager.isGuestMode() ? "Guest Mode" : "Active");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(authManager.isGuestMode() ? themeManager.getWarning() : themeManager.getSuccess());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        // Chat area
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        // Disclaimer
        JLabel disclaimerLabel = new JLabel(authManager.isGuestMode() ? 
            "Guest mode provides sample responses. Sign up for full AI capabilities." :
            "AI assistant provides general information only, not financial advice.");
        disclaimerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        disclaimerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        disclaimerLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
        
        add(headerPanel, BorderLayout.NORTH);
        add(chatScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(disclaimerLabel, BorderLayout.PAGE_END);
    }
    
    private void sendMessage() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) return;
        
        // Add user message
        addMessage(new ChatMessage("user", userInput));
        inputField.setText("");
        
        // Simulate AI response
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                Thread.sleep(1000); // Simulate processing time
                return generateAiResponse(userInput);
            }
            
            @Override
            protected void done() {
                try {
                    String response = get();
                    addMessage(new ChatMessage("assistant", response));
                    
                    // Check if response includes strategy suggestion
                    if (response.contains("momentum") || response.contains("strategy")) {
                        generateStrategySuggestion(userInput);
                    }
                } catch (Exception e) {
                    addMessage(new ChatMessage("assistant", "I apologize, but I encountered an error. Please try again."));
                }
            }
        };
        worker.execute();
    }
    
    private String generateAiResponse(String userInput) {
        String lowerInput = userInput.toLowerCase();
        
        if (authManager.isGuestMode()) {
            return generateGuestResponse(lowerInput);
        } else {
            return generateFullResponse(lowerInput);
        }
    }
    
    private String generateGuestResponse(String input) {
        if (input.contains("momentum") || input.contains("trend")) {
            return "Here's a sample momentum strategy template:\n\n" +
                   "**Momentum Strategy Framework**\n" +
                   "• Entry: RSI > 70, Price > 20-day MA\n" +
                   "• Exit: RSI < 30 or 5% stop loss\n" +
                   "• Position size: 2% risk per trade\n" +
                   "• Timeframe: Daily\n\n" +
                   "This is a basic template. For full customization and backtesting, please sign up.";
        }
        
        if (input.contains("mean reversion") || input.contains("oversold")) {
            return "Here's a sample mean reversion strategy:\n\n" +
                   "**Mean Reversion Strategy**\n" +
                   "• Entry: RSI < 30, Price below 20-day MA\n" +
                   "• Exit: RSI > 70 or 3% profit target\n" +
                   "• Stop loss: 2%\n" +
                   "• Best for: Range-bound markets\n\n" +
                   "Sign up to customize parameters and run backtests.";
        }
        
        return "I can help you explore different strategy types:\n\n" +
               "• **Momentum** - Follow price trends\n" +
               "• **Mean Reversion** - Buy oversold, sell overbought\n" +
               "• **Breakout** - Trade price breakouts\n" +
               "• **Pairs Trading** - Relative value strategies\n\n" +
               "Tell me which interests you, and I'll provide a sample template. For full features, please sign up!";
    }
    
    private String generateFullResponse(String input) {
        if (input.contains("momentum")) {
            return "I'll help you build a momentum strategy. Here are the key components:\n\n" +
                   "**Entry Conditions:**\n" +
                   "• RSI(14) > 60\n" +
                   "• Price > 20-day SMA\n" +
                   "• Volume > 1.5x average\n" +
                   "• ADX > 25 (trend strength)\n\n" +
                   "**Exit Conditions:**\n" +
                   "• RSI < 40\n" +
                   "• Price < 20-day SMA\n" +
                   "• 5% stop loss\n" +
                   "• 10% profit target\n\n" +
                   "Would you like me to apply these settings to your strategy form?";
        }
        
        if (input.contains("optimize") || input.contains("improve")) {
            return "I can help optimize your strategy. Here are some suggestions:\n\n" +
                   "**Risk Management Improvements:**\n" +
                   "• Reduce position size during high volatility\n" +
                   "• Add correlation filters\n" +
                   "• Implement dynamic stop losses\n\n" +
                   "**Entry/Exit Enhancements:**\n" +
                   "• Add volume confirmation\n" +
                   "• Use multiple timeframe analysis\n" +
                   "• Implement trailing stops\n\n" +
                   "Would you like me to apply these optimizations?";
        }
        
        return "I can help you with:\n\n" +
               "• **Strategy Creation** - Build new strategies from scratch\n" +
               "• **Optimization** - Improve existing strategies\n" +
               "• **Risk Management** - Add protective measures\n" +
               "• **Backtesting** - Test historical performance\n" +
               "• **Parameter Tuning** - Find optimal settings\n\n" +
               "What would you like to work on?";
    }
    
    private void generateStrategySuggestion(String userInput) {
        if (strategyUpdateListener != null && !authManager.isGuestMode()) {
            StrategyData suggestion = new StrategyData();
            suggestion.setName("AI Generated Strategy");
            suggestion.setType("momentum");
            suggestion.setAssetClass("stocks");
            suggestion.setTimeframe("1d");
            
            strategyUpdateListener.accept(suggestion);
        }
    }
    
    private void addMessage(ChatMessage message) {
        messages.add(message);
        updateChatDisplay();
    }
    
    private void updateChatDisplay() {
        StringBuilder sb = new StringBuilder();
        for (ChatMessage message : messages) {
            sb.append(message.getRole().equals("user") ? "You: " : "AI: ");
            sb.append(message.getContent());
            sb.append("\n\n");
        }
        chatArea.setText(sb.toString());
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    public void setStrategyUpdateListener(Consumer<StrategyData> listener) {
        this.strategyUpdateListener = listener;
    }
    
    public void applyTheme() {
        setBackground(themeManager.getCardBackground());
        chatArea.setBackground(themeManager.getBackground());
        chatArea.setForeground(themeManager.getText());
        inputField.setBackground(themeManager.getCardBackground());
        inputField.setForeground(themeManager.getText());
        sendButton.setBackground(themeManager.getPrimary());
        sendButton.setForeground(Color.WHITE);
        
        applyThemeToComponent(this);
        repaint();
    }
    
    private void applyThemeToComponent(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel) {
                component.setForeground(themeManager.getText());
            } else if (component instanceof Container) {
                component.setBackground(themeManager.getCardBackground());
                applyThemeToComponent((Container) component);
            }
        }
    }
    
    // Inner class for chat messages
    private static class ChatMessage {
        private String role;
        private String content;
        
        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        public String getRole() { return role; }
        public String getContent() { return content; }
    }
}