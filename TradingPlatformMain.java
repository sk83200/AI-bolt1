import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Main Trading Platform Application
 * Replicates all functionality from the Vue.js version with Java Swing
 */
public class TradingPlatformMain extends JFrame {
    
    // Application state
    private AuthenticationManager authManager;
    private WorkspaceManager workspaceManager;
    private boolean isDarkMode = false;
    private boolean showGuestBanner = true;
    
    // Main components
    private JPanel mainPanel;
    private AppHeader headerPanel;
    private JSplitPane mainSplitPane;
    private AiAssistantPanel aiAssistantPanel;
    private MessagesPanel messagesPanel;
    private StrategiesPanel strategiesPanel;
    private CenteredAiAssistant centeredAssistant;
    
    public TradingPlatformMain() {
        initializeApplication();
        setupUI();
        setupEventHandlers();
    }
    
    private void initializeApplication() {
        authManager = new AuthenticationManager();
        workspaceManager = new WorkspaceManager();
        
        setTitle("AI Trader - Professional Trading Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setupUI() {
        mainPanel = new JPanel(new BorderLayout());
        
        // Create header
        headerPanel = new AppHeader(authManager, this);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create guest banner
        if (authManager.isGuestMode() && showGuestBanner) {
            JPanel guestBanner = createGuestBanner();
            mainPanel.add(guestBanner, BorderLayout.NORTH);
        }
        
        // Create main content area
        createMainContent();
        
        add(mainPanel);
        updateTheme();
    }
    
    private JPanel createGuestBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(239, 246, 255)); // primary-50
        banner.setBorder(new EmptyBorder(8, 16, 8, 16));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        JLabel infoIcon = new JLabel("ℹ");
        infoIcon.setForeground(new Color(37, 99, 235)); // primary-600
        leftPanel.add(infoIcon);
        
        JLabel message = new JLabel("You're in Guest Mode. Sign up to run backtests, analyze performance, and save your work.");
        message.setForeground(new Color(30, 58, 138)); // primary-800
        leftPanel.add(message);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JButton signUpBtn = new JButton("Sign Up Free");
        signUpBtn.setBackground(new Color(37, 99, 235));
        signUpBtn.setForeground(Color.WHITE);
        signUpBtn.addActionListener(e -> showRegisterDialog());
        rightPanel.add(signUpBtn);
        
        JButton closeBtn = new JButton("×");
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.addActionListener(e -> {
            showGuestBanner = false;
            refreshUI();
        });
        rightPanel.add(closeBtn);
        
        banner.add(leftPanel, BorderLayout.WEST);
        banner.add(rightPanel, BorderLayout.EAST);
        
        return banner;
    }
    
    private void createMainContent() {
        if (shouldShowCenteredAssistant()) {
            // Show centered AI assistant for new authenticated users
            centeredAssistant = new CenteredAiAssistant(authManager, this);
            mainPanel.add(centeredAssistant, BorderLayout.CENTER);
        } else if (authManager.isAuthenticated() && !authManager.isGuestMode()) {
            // Full workspace for authenticated users
            createFullWorkspace();
        } else {
            // Guest mode or non-authenticated - show strategies panel
            strategiesPanel = new StrategiesPanel(authManager);
            mainPanel.add(strategiesPanel, BorderLayout.CENTER);
        }
    }
    
    private boolean shouldShowCenteredAssistant() {
        return authManager.isAuthenticated() && 
               !authManager.isGuestMode() && 
               !authManager.isReturningUser() && 
               !workspaceManager.hasEngaged();
    }
    
    private void createFullWorkspace() {
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(0.3);
        mainSplitPane.setResizeWeight(0.3);
        
        // Left panel with AI Assistant and Messages
        JPanel leftPanel = createLeftPanel();
        mainSplitPane.setLeftComponent(leftPanel);
        
        // Right panel with main content
        strategiesPanel = new StrategiesPanel(authManager);
        mainSplitPane.setRightComponent(strategiesPanel);
        
        mainPanel.add(mainSplitPane, BorderLayout.CENTER);
    }
    
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        
        // AI Assistant
        aiAssistantPanel = new AiAssistantPanel(authManager);
        leftPanel.add(aiAssistantPanel, BorderLayout.CENTER);
        
        // Messages toggle and panel
        JPanel messagesContainer = new JPanel(new BorderLayout());
        
        JButton messagesToggle = new JButton("Messages");
        messagesToggle.addActionListener(e -> toggleMessagesPanel());
        messagesContainer.add(messagesToggle, BorderLayout.NORTH);
        
        messagesPanel = new MessagesPanel(authManager);
        messagesPanel.setVisible(false);
        messagesContainer.add(messagesPanel, BorderLayout.CENTER);
        
        leftPanel.add(messagesContainer, BorderLayout.SOUTH);
        
        return leftPanel;
    }
    
    private void toggleMessagesPanel() {
        messagesPanel.setVisible(!messagesPanel.isVisible());
        revalidate();
        repaint();
    }
    
    public void handleUserEngagement() {
        workspaceManager.setEngaged(true);
        refreshUI();
    }
    
    public void refreshUI() {
        mainPanel.removeAll();
        setupUI();
        revalidate();
        repaint();
    }
    
    public void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        updateTheme();
    }
    
    private void updateTheme() {
        Color bgColor = isDarkMode ? new Color(31, 41, 55) : Color.WHITE;
        Color textColor = isDarkMode ? Color.WHITE : Color.BLACK;
        
        updateComponentTheme(this, bgColor, textColor);
    }
    
    private void updateComponentTheme(Container container, Color bgColor, Color textColor) {
        container.setBackground(bgColor);
        container.setForeground(textColor);
        
        for (Component component : container.getComponents()) {
            if (component instanceof Container) {
                updateComponentTheme((Container) component, bgColor, textColor);
            } else {
                component.setBackground(bgColor);
                component.setForeground(textColor);
            }
        }
    }
    
    public void showLoginDialog() {
        LoginDialog dialog = new LoginDialog(this, authManager);
        dialog.setVisible(true);
    }
    
    public void showRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(this, authManager);
        dialog.setVisible(true);
    }
    
    private void setupEventHandlers() {
        // Window closing event
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                workspaceManager.saveWorkspaceState();
                System.exit(0);
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TradingPlatformMain().setVisible(true);
        });
    }
}

/**
 * Authentication Manager - Handles user authentication and state
 */
class AuthenticationManager {
    private User currentUser;
    private boolean guestMode = false;
    private String token;
    
    public boolean isAuthenticated() {
        return token != null && !guestMode;
    }
    
    public boolean isGuestMode() {
        return guestMode;
    }
    
    public boolean isProUser() {
        return currentUser != null && currentUser.isPro();
    }
    
    public boolean isReturningUser() {
        return currentUser != null && currentUser.isReturning();
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean login(String email, String password) {
        // Simulate login process
        try {
            Thread.sleep(800); // Simulate API call
            currentUser = new User("1", "Demo User", email, "Trader", false, true);
            token = "demo-jwt-token";
            guestMode = false;
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }
    
    public boolean register(String name, String email, String password, String role) {
        try {
            Thread.sleep(800); // Simulate API call
            currentUser = new User("1", name, email, role, false, false);
            token = "demo-jwt-token";
            guestMode = false;
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }
    
    public void loginAsGuest() {
        guestMode = true;
        currentUser = new User("guest", "Guest User", "", "Guest", false, false);
        token = "guest-token";
    }
    
    public void logout() {
        currentUser = null;
        token = null;
        guestMode = false;
    }
    
    public void upgradeToPro() {
        if (currentUser != null) {
            currentUser.setPro(true);
        }
    }
}

/**
 * User class representing a user in the system
 */
class User {
    private String id;
    private String name;
    private String email;
    private String role;
    private boolean isPro;
    private boolean isReturning;
    
    public User(String id, String name, String email, String role, boolean isPro, boolean isReturning) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.isPro = isPro;
        this.isReturning = isReturning;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isPro() { return isPro; }
    public boolean isReturning() { return isReturning; }
    public void setPro(boolean pro) { this.isPro = pro; }
}

/**
 * Workspace Manager - Handles workspace state and persistence
 */
class WorkspaceManager {
    private boolean hasEngaged = false;
    private Map<String, Object> workspaceState = new HashMap<>();
    
    public boolean hasEngaged() {
        return hasEngaged;
    }
    
    public void setEngaged(boolean engaged) {
        this.hasEngaged = engaged;
    }
    
    public void saveWorkspaceState() {
        // Implementation for saving workspace state
        workspaceState.put("lastModified", LocalDateTime.now());
    }
    
    public Map<String, Object> getWorkspaceState() {
        return workspaceState;
    }
}

/**
 * Application Header Component
 */
class AppHeader extends JPanel {
    private AuthenticationManager authManager;
    private TradingPlatformMain mainFrame;
    
    public AppHeader(AuthenticationManager authManager, TradingPlatformMain mainFrame) {
        this.authManager = authManager;
        this.mainFrame = mainFrame;
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 16, 8, 16));
        setBackground(Color.WHITE);
        
        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setOpaque(false);
        JLabel logo = new JLabel("AI Trader");
        logo.setFont(new Font("Arial", Font.BOLD, 18));
        logo.setForeground(new Color(30, 58, 138));
        logoPanel.add(logo);
        
        // Navigation
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        navPanel.setOpaque(false);
        String[] navItems = {"Strategies", "Portfolios", "Indicators", "Charts", "Alerts"};
        for (String item : navItems) {
            JButton navBtn = new JButton(item);
            navBtn.setBorderPainted(false);
            navBtn.setContentAreaFilled(false);
            navBtn.addActionListener(e -> navigateTo(item.toLowerCase()));
            navPanel.add(navBtn);
        }
        
        // Right panel
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        // Theme toggle
        JButton themeBtn = new JButton("🌙");
        themeBtn.setBorderPainted(false);
        themeBtn.setContentAreaFilled(false);
        themeBtn.addActionListener(e -> mainFrame.toggleDarkMode());
        rightPanel.add(themeBtn);
        
        // User controls
        if (authManager.isGuestMode()) {
            JLabel guestLabel = new JLabel("Guest Mode");
            guestLabel.setForeground(new Color(217, 119, 6));
            rightPanel.add(guestLabel);
        }
        
        if (!authManager.isAuthenticated() || authManager.isGuestMode()) {
            JButton loginBtn = new JButton("Log in");
            loginBtn.addActionListener(e -> mainFrame.showLoginDialog());
            rightPanel.add(loginBtn);
            
            JButton signUpBtn = new JButton("Sign Up");
            signUpBtn.setBackground(new Color(37, 99, 235));
            signUpBtn.setForeground(Color.WHITE);
            signUpBtn.addActionListener(e -> mainFrame.showRegisterDialog());
            rightPanel.add(signUpBtn);
        } else {
            // User menu for authenticated users
            JButton userBtn = new JButton(authManager.getCurrentUser().getName());
            rightPanel.add(userBtn);
        }
        
        add(logoPanel, BorderLayout.WEST);
        add(navPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    private void navigateTo(String page) {
        // Implementation for navigation
        System.out.println("Navigating to: " + page);
    }
}

/**
 * Centered AI Assistant for new users
 */
class CenteredAiAssistant extends JPanel {
    private AuthenticationManager authManager;
    private TradingPlatformMain mainFrame;
    private JTextArea chatArea;
    private JTextField inputField;
    private java.util.List<ChatMessage> messages;
    private boolean isTyping = false;
    
    public CenteredAiAssistant(AuthenticationManager authManager, TradingPlatformMain mainFrame) {
        this.authManager = authManager;
        this.mainFrame = mainFrame;
        this.messages = new ArrayList<>();
        setupUI();
        initializeChat();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(800, 600));
        chatPanel.setBackground(Color.WHITE);
        chatPanel.setBorder(BorderFactory.createRoundedBorder(16));
        
        // Welcome section
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBorder(new EmptyBorder(32, 32, 16, 32));
        welcomePanel.setOpaque(false);
        
        JLabel welcomeTitle = new JLabel("Welcome to AI Trader", JLabel.CENTER);
        welcomeTitle.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel welcomeSubtitle = new JLabel("Describe your trading strategy and I'll help you build it", JLabel.CENTER);
        welcomeSubtitle.setForeground(Color.GRAY);
        
        welcomePanel.add(welcomeTitle, BorderLayout.NORTH);
        welcomePanel.add(welcomeSubtitle, BorderLayout.CENTER);
        
        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setPreferredSize(new Dimension(700, 300));
        
        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(16, 32, 32, 32));
        inputPanel.setOpaque(false);
        
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        
        JButton sendBtn = new JButton("Send");
        sendBtn.setBackground(new Color(37, 99, 235));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.addActionListener(e -> sendMessage());
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        
        // Quick prompts
        JPanel promptsPanel = new JPanel(new FlowLayout());
        promptsPanel.setOpaque(false);
        String[] prompts = {"Momentum strategy for tech stocks", "Low-risk dividend portfolio", 
                           "Crypto swing trading strategy", "Market volatility hedge"};
        for (String prompt : prompts) {
            JButton promptBtn = new JButton(prompt);
            promptBtn.setFont(new Font("Arial", Font.PLAIN, 12));
            promptBtn.addActionListener(e -> {
                inputField.setText(prompt);
                sendMessage();
            });
            promptsPanel.add(promptBtn);
        }
        
        chatPanel.add(welcomePanel, BorderLayout.NORTH);
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        bottomPanel.add(promptsPanel, BorderLayout.SOUTH);
        chatPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        centerPanel.add(chatPanel);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void initializeChat() {
        messages.add(new ChatMessage("assistant", 
            "Hello! I'm your AI trading assistant. Describe the trading strategy you'd like to create, and I'll help you build it step by step.", 
            false));
        updateChatDisplay();
    }
    
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;
        
        messages.add(new ChatMessage("user", text, false));
        inputField.setText("");
        mainFrame.handleUserEngagement();
        
        updateChatDisplay();
        
        // Simulate AI response
        isTyping = true;
        updateChatDisplay();
        
        CompletableFuture.delayedExecutor(1500, TimeUnit.MILLISECONDS).execute(() -> {
            SwingUtilities.invokeLater(() -> {
                isTyping = false;
                String response = generateResponse(text);
                messages.add(new ChatMessage("assistant", response, true));
                updateChatDisplay();
            });
        });
    }
    
    private String generateResponse(String query) {
        String lowerQuery = query.toLowerCase();
        
        if (lowerQuery.contains("momentum") || lowerQuery.contains("tech")) {
            return "Here's a sample strategy that aligns with your goals:\n\n" +
                   "**Tech Momentum Strategy**\n" +
                   "This sample momentum strategy allocates equally to AAPL, TSLA, and NVDA when their 3-month returns exceed 12% and volatility is under 15%. It rebalances monthly and exits positions if drawdown exceeds 10%.\n\n" +
                   "**Key Features:**\n" +
                   "• Equal weight allocation (33.3% each)\n" +
                   "• Monthly rebalancing\n" +
                   "• 10% stop-loss protection\n" +
                   "• Targets 15-20% annual returns\n\n" +
                   "This is a general template. To customize parameters, run backtests with real data, and analyze performance metrics, please log in.";
        }
        
        return "I can help you build a trading strategy! Here are some popular approaches:\n\n" +
               "• **Growth/Momentum** - Target stocks with strong price momentum\n" +
               "• **Value/Contrarian** - Buy undervalued assets\n" +
               "• **Income/Dividend** - Focus on dividend-paying stocks\n" +
               "• **Sector Rotation** - Rotate between market sectors\n" +
               "• **Risk Parity** - Balance risk across asset classes\n\n" +
               "Tell me more about your investment goals, risk tolerance, or preferred asset classes, and I'll create a sample strategy for you.";
    }
    
    private void updateChatDisplay() {
        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : messages) {
            sb.append(msg.getRole().equals("user") ? "You: " : "AI: ");
            sb.append(msg.getContent()).append("\n\n");
        }
        
        if (isTyping) {
            sb.append("AI: Typing...\n\n");
        }
        
        chatArea.setText(sb.toString());
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}

/**
 * Chat Message class
 */
class ChatMessage {
    private String role;
    private String content;
    private boolean showLoginPrompt;
    
    public ChatMessage(String role, String content, boolean showLoginPrompt) {
        this.role = role;
        this.content = content;
        this.showLoginPrompt = showLoginPrompt;
    }
    
    public String getRole() { return role; }
    public String getContent() { return content; }
    public boolean shouldShowLoginPrompt() { return showLoginPrompt; }
}

/**
 * AI Assistant Panel for authenticated users
 */
class AiAssistantPanel extends JPanel {
    private AuthenticationManager authManager;
    private JTextArea chatArea;
    private JTextField inputField;
    private java.util.List<ChatMessage> messages;
    
    public AiAssistantPanel(AuthenticationManager authManager) {
        this.authManager = authManager;
        this.messages = new ArrayList<>();
        setupUI();
        initializeChat();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("AI Trading Assistant"));
        
        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Monospace", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        
        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        
        JButton sendBtn = new JButton("Send");
        sendBtn.addActionListener(e -> sendMessage());
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }
    
    private void initializeChat() {
        messages.add(new ChatMessage("assistant", 
            "Hello! I'm your AI trading assistant. How can I help you today?", false));
        updateChatDisplay();
    }
    
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;
        
        messages.add(new ChatMessage("user", text, false));
        inputField.setText("");
        
        // Generate AI response
        String response = generateAIResponse(text);
        messages.add(new ChatMessage("assistant", response, false));
        
        updateChatDisplay();
    }
    
    private String generateAIResponse(String query) {
        String lowerQuery = query.toLowerCase();
        
        if (lowerQuery.contains("market") && lowerQuery.contains("trend")) {
            return "Based on current data, the market is showing an upward trend with technology and healthcare sectors outperforming. However, be cautious about energy stocks which are facing headwinds from regulatory changes.";
        }
        
        if (lowerQuery.contains("buy") || lowerQuery.contains("sell")) {
            if (!authManager.isProUser()) {
                return "Detailed buy/sell recommendations are available to Pro users only. Would you like to upgrade to access this feature?";
            }
            return "When considering whether to buy or sell, it's important to look at both fundamentals and technical indicators. Would you like me to analyze a specific stock for you?";
        }
        
        return "I can help you with market analysis, trading strategies, portfolio optimization, and technical analysis. Is there a specific area you'd like to focus on?";
    }
    
    private void updateChatDisplay() {
        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : messages) {
            sb.append(msg.getRole().equals("user") ? "You: " : "AI: ");
            sb.append(msg.getContent()).append("\n\n");
        }
        chatArea.setText(sb.toString());
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}

/**
 * Messages Panel for notifications and alerts
 */
class MessagesPanel extends JPanel {
    private AuthenticationManager authManager;
    private DefaultListModel<NotificationMessage> listModel;
    private JList<NotificationMessage> messagesList;
    
    public MessagesPanel(AuthenticationManager authManager) {
        this.authManager = authManager;
        setupUI();
        loadSampleMessages();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Messages & Alerts"));
        setPreferredSize(new Dimension(300, 200));
        
        listModel = new DefaultListModel<>();
        messagesList = new JList<>(listModel);
        messagesList.setCellRenderer(new MessageCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(messagesList);
        add(scrollPane, BorderLayout.CENTER);
        
        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton markAllBtn = new JButton("Mark All Read");
        JButton clearBtn = new JButton("Clear All");
        
        markAllBtn.addActionListener(e -> markAllRead());
        clearBtn.addActionListener(e -> clearAll());
        
        buttonPanel.add(markAllBtn);
        buttonPanel.add(clearBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadSampleMessages() {
        listModel.addElement(new NotificationMessage("alert", 
            "AAPL price alert triggered: Above $190", 
            LocalDateTime.now().minusMinutes(5), false));
        listModel.addElement(new NotificationMessage("notification", 
            "New AI-generated trading strategy available", 
            LocalDateTime.now().minusMinutes(30), false));
        listModel.addElement(new NotificationMessage("message", 
            "Your portfolio rebalancing is complete", 
            LocalDateTime.now().minusHours(2), true));
    }
    
    private void markAllRead() {
        for (int i = 0; i < listModel.size(); i++) {
            listModel.getElementAt(i).setRead(true);
        }
        messagesList.repaint();
    }
    
    private void clearAll() {
        listModel.clear();
    }
}

/**
 * Notification Message class
 */
class NotificationMessage {
    private String type;
    private String content;
    private LocalDateTime timestamp;
    private boolean read;
    
    public NotificationMessage(String type, String content, LocalDateTime timestamp, boolean read) {
        this.type = type;
        this.content = content;
        this.timestamp = timestamp;
        this.read = read;
    }
    
    // Getters and setters
    public String getType() { return type; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    
    @Override
    public String toString() {
        return content;
    }
}

/**
 * Custom cell renderer for messages
 */
class MessageCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if (value instanceof NotificationMessage) {
            NotificationMessage msg = (NotificationMessage) value;
            setText(msg.getContent());
            
            if (!msg.isRead()) {
                setFont(getFont().deriveFont(Font.BOLD));
                setBackground(new Color(239, 246, 255)); // primary-50
            }
            
            // Set icon based on type
            switch (msg.getType()) {
                case "alert":
                    setForeground(new Color(239, 68, 68)); // red-500
                    break;
                case "notification":
                    setForeground(new Color(245, 158, 11)); // yellow-500
                    break;
                default:
                    setForeground(new Color(34, 197, 94)); // green-500
                    break;
            }
        }
        
        return this;
    }
}

/**
 * Main Strategies Panel - handles both authenticated and guest modes
 */
class StrategiesPanel extends JPanel {
    private AuthenticationManager authManager;
    private JSplitPane mainSplitPane;
    private StrategiesAiAssistant aiAssistant;
    private StrategyGuiEditor guiEditor;
    private StrategyNotifications notifications;
    private StrategyOutputPanel outputPanel;
    
    public StrategiesPanel(AuthenticationManager authManager) {
        this.authManager = authManager;
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        if (authManager.isAuthenticated() && !authManager.isGuestMode()) {
            // Authenticated users: Strategy Builder without AI Assistant panel
            createAuthenticatedLayout();
        } else {
            // Guest users: Layout with AI Assistant panel
            createGuestLayout();
        }
    }
    
    private void createAuthenticatedLayout() {
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(0.6);
        mainSplitPane.setResizeWeight(0.6);
        
        // Left: GUI Editor Form
        guiEditor = new StrategyGuiEditor(authManager);
        mainSplitPane.setLeftComponent(guiEditor);
        
        // Right: Split pane with Messages/Notifications (top) and Output/Code (bottom)
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setDividerLocation(0.4);
        rightSplitPane.setResizeWeight(0.4);
        
        notifications = new StrategyNotifications(authManager);
        outputPanel = new StrategyOutputPanel(authManager);
        
        rightSplitPane.setTopComponent(notifications);
        rightSplitPane.setBottomComponent(outputPanel);
        
        mainSplitPane.setRightComponent(rightSplitPane);
        add(mainSplitPane, BorderLayout.CENTER);
    }
    
    private void createGuestLayout() {
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(0.3);
        mainSplitPane.setResizeWeight(0.3);
        
        // Left: AI Assistant Panel
        aiAssistant = new StrategiesAiAssistant(authManager);
        mainSplitPane.setLeftComponent(aiAssistant);
        
        // Center-Right: Another split pane
        JSplitPane centerRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerRightSplitPane.setDividerLocation(0.64); // 45% of remaining 70%
        centerRightSplitPane.setResizeWeight(0.64);
        
        // Center: GUI Editor Form with grayed overlay
        JPanel centerPanel = new JPanel(new BorderLayout());
        guiEditor = new StrategyGuiEditor(authManager);
        
        // Add grayed overlay for guest mode
        JPanel overlayPanel = new JPanel();
        overlayPanel.setBackground(new Color(128, 128, 128, 76)); // 30% opacity gray
        overlayPanel.setOpaque(false);
        
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(guiEditor.getPreferredSize());
        layeredPane.add(guiEditor, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);
        
        centerPanel.add(layeredPane, BorderLayout.CENTER);
        centerRightSplitPane.setLeftComponent(centerPanel);
        
        // Right: Split pane with Messages/Notifications (top) and Output/Code (bottom)
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setDividerLocation(0.5);
        rightSplitPane.setResizeWeight(0.5);
        
        notifications = new StrategyNotifications(authManager);
        outputPanel = new StrategyOutputPanel(authManager);
        
        rightSplitPane.setTopComponent(notifications);
        rightSplitPane.setBottomComponent(outputPanel);
        
        centerRightSplitPane.setRightComponent(rightSplitPane);
        mainSplitPane.setRightComponent(centerRightSplitPane);
        
        add(mainSplitPane, BorderLayout.CENTER);
    }
}

/**
 * Strategies AI Assistant Panel
 */
class StrategiesAiAssistant extends JPanel {
    private AuthenticationManager authManager;
    private JTextArea chatArea;
    private JTextField inputField;
    private java.util.List<ChatMessage> messages;
    private boolean isTyping = false;
    
    public StrategiesAiAssistant(AuthenticationManager authManager) {
        this.authManager = authManager;
        this.messages = new ArrayList<>();
        setupUI();
        initializeChat();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        JLabel titleLabel = new JLabel("AI Strategy Assistant");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel statusLabel = new JLabel(authManager.isGuestMode() ? "Guest Mode" : "");
        statusLabel.setForeground(authManager.isGuestMode() ? new Color(217, 119, 6) : Color.GREEN);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 12));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        
        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        inputField = new JTextField();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        
        JButton sendBtn = new JButton("Send");
        sendBtn.addActionListener(e -> sendMessage());
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        
        // Footer
        JLabel footerLabel = new JLabel(authManager.isGuestMode() ? 
            "Guest mode provides sample responses. Sign up for full AI capabilities." :
            "AI assistant provides general information only, not financial advice.");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setHorizontalAlignment(JLabel.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(chatScroll, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }
    
    private void initializeChat() {
        String initialMessage = authManager.isGuestMode() ?
            "Hello! I'm your AI strategy assistant in guest mode. I can provide sample strategies and general guidance. For full customization, please sign up!" :
            "Hello! I'm your AI strategy assistant. I can help you build, optimize, and analyze trading strategies. What kind of strategy would you like to create?";
        
        messages.add(new ChatMessage("assistant", initialMessage, false));
        updateChatDisplay();
    }
    
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;
        
        messages.add(new ChatMessage("user", text, false));
        inputField.setText("");
        
        updateChatDisplay();
        
        // Simulate AI response
        isTyping = true;
        updateChatDisplay();
        
        CompletableFuture.delayedExecutor(1200, TimeUnit.MILLISECONDS).execute(() -> {
            SwingUtilities.invokeLater(() -> {
                isTyping = false;
                ChatMessage response = generateStrategyResponse(text);
                messages.add(response);
                updateChatDisplay();
            });
        });
    }
    
    private ChatMessage generateStrategyResponse(String query) {
        String lowerQuery = query.toLowerCase();
        
        if (authManager.isGuestMode()) {
            return generateGuestResponse(lowerQuery);
        } else {
            return generateFullResponse(lowerQuery);
        }
    }
    
    private ChatMessage generateGuestResponse(String query) {
        if (query.contains("momentum") || query.contains("trend")) {
            return new ChatMessage("assistant",
                "Here's a sample momentum strategy template:\n\n" +
                "**Momentum Strategy Framework**\n" +
                "• Entry: RSI > 70, Price > 20-day MA\n" +
                "• Exit: RSI < 30 or 5% stop loss\n" +
                "• Position size: 2% risk per trade\n" +
                "• Timeframe: Daily\n\n" +
                "This is a basic template. For full customization and backtesting, please sign up.", true);
        }
        
        return new ChatMessage("assistant",
            "I can help you explore different strategy types:\n\n" +
            "• **Momentum** - Follow price trends\n" +
            "• **Mean Reversion** - Buy oversold, sell overbought\n" +
            "• **Breakout** - Trade price breakouts\n" +
            "• **Pairs Trading** - Relative value strategies\n\n" +
            "Tell me which interests you, and I'll provide a sample template. For full features, please sign up!", false);
    }
    
    private ChatMessage generateFullResponse(String query) {
        if (query.contains("momentum")) {
            return new ChatMessage("assistant",
                "I'll help you build a momentum strategy. Here are the key components:\n\n" +
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
                "Would you like me to apply these settings to your strategy form?", false);
        }
        
        return new ChatMessage("assistant",
            "I can help you with:\n\n" +
            "• **Strategy Creation** - Build new strategies from scratch\n" +
            "• **Optimization** - Improve existing strategies\n" +
            "• **Risk Management** - Add protective measures\n" +
            "• **Backtesting** - Test historical performance\n" +
            "• **Parameter Tuning** - Find optimal settings\n\n" +
            "What would you like to work on?", false);
    }
    
    private void updateChatDisplay() {
        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : messages) {
            sb.append(msg.getRole().equals("user") ? "You: " : "AI: ");
            sb.append(msg.getContent()).append("\n\n");
            
            if (msg.shouldShowLoginPrompt() && authManager.isGuestMode()) {
                sb.append("📌 To customize and backtest this strategy with real data, please log in.\n\n");
            }
        }
        
        if (isTyping) {
            sb.append("AI: Typing...\n\n");
        }
        
        chatArea.setText(sb.toString());
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}

/**
 * Strategy GUI Editor Panel
 */
class StrategyGuiEditor extends JPanel {
    private AuthenticationManager authManager;
    private Map<String, Object> strategyData;
    private JTextField nameField;
    private JComboBox<String> typeCombo;
    private JComboBox<String> assetClassCombo;
    private JComboBox<String> timeframeCombo;
    private JCheckBox[] indicatorCheckboxes;
    private JTextField profitTargetField;
    private JTextField stopLossField;
    private JCheckBox trailingStopCheck;
    private JTextField maxRiskField;
    private JTextField maxPositionsField;
    
    public StrategyGuiEditor(AuthenticationManager authManager) {
        this.authManager = authManager;
        this.strategyData = new HashMap<>();
        setupUI();
        loadSampleData();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        JLabel titleLabel = new JLabel("Strategy Builder");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel statusLabel = new JLabel(authManager.isGuestMode() ? "Read-only (Guest)" : "");
        statusLabel.setForeground(new Color(217, 119, 6));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        // Form content
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Basic Information section
        addSectionHeader(formPanel, gbc, "Basic Information", 0);
        
        gbc.gridy = 1;
        addFormField(formPanel, gbc, "Strategy Name:", nameField = new JTextField(20));
        
        gbc.gridy = 2;
        typeCombo = new JComboBox<>(new String[]{"momentum", "mean_reversion", "trend_following", "breakout"});
        addFormField(formPanel, gbc, "Strategy Type:", typeCombo);
        
        gbc.gridy = 3;
        assetClassCombo = new JComboBox<>(new String[]{"stocks", "crypto", "forex", "commodities"});
        addFormField(formPanel, gbc, "Asset Class:", assetClassCombo);
        
        gbc.gridy = 4;
        timeframeCombo = new JComboBox<>(new String[]{"1m", "5m", "15m", "1h", "4h", "1d"});
        addFormField(formPanel, gbc, "Timeframe:", timeframeCombo);
        
        // Entry Rules section
        addSectionHeader(formPanel, gbc, "Entry Rules", 5);
        
        gbc.gridy = 6;
        JPanel indicatorsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] indicators = {"RSI", "MACD", "SMA", "EMA", "BB", "ADX"};
        indicatorCheckboxes = new JCheckBox[indicators.length];
        for (int i = 0; i < indicators.length; i++) {
            indicatorCheckboxes[i] = new JCheckBox(indicators[i]);
            indicatorsPanel.add(indicatorCheckboxes[i]);
        }
        addFormField(formPanel, gbc, "Technical Indicators:", indicatorsPanel);
        
        // Exit Rules section
        addSectionHeader(formPanel, gbc, "Exit Rules", 7);
        
        gbc.gridy = 8;
        addFormField(formPanel, gbc, "Profit Target (%):", profitTargetField = new JTextField(10));
        
        gbc.gridy = 9;
        addFormField(formPanel, gbc, "Stop Loss (%):", stopLossField = new JTextField(10));
        
        gbc.gridy = 10;
        addFormField(formPanel, gbc, "Use Trailing Stop:", trailingStopCheck = new JCheckBox());
        
        // Risk Management section
        addSectionHeader(formPanel, gbc, "Risk Management", 11);
        
        gbc.gridy = 12;
        addFormField(formPanel, gbc, "Max Risk Per Trade (%):", maxRiskField = new JTextField(10));
        
        gbc.gridy = 13;
        addFormField(formPanel, gbc, "Max Positions:", maxPositionsField = new JTextField(10));
        
        // Action buttons
        gbc.gridy = 14;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton saveBtn = new JButton(authManager.isGuestMode() ? "Save (Sign up required)" : "Save Strategy");
        JButton backtestBtn = new JButton(authManager.isGuestMode() ? "Backtest (Sign up required)" : "Run Backtest");
        JButton resetBtn = new JButton("Reset");
        
        saveBtn.setEnabled(!authManager.isGuestMode());
        backtestBtn.setEnabled(!authManager.isGuestMode());
        resetBtn.setEnabled(!authManager.isGuestMode());
        
        if (authManager.isGuestMode()) {
            saveBtn.setBackground(Color.LIGHT_GRAY);
            backtestBtn.setBackground(Color.LIGHT_GRAY);
            resetBtn.setBackground(Color.LIGHT_GRAY);
        }
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(backtestBtn);
        buttonPanel.add(resetBtn);
        
        formPanel.add(buttonPanel, gbc);
        
        // Make fields read-only for guests
        if (authManager.isGuestMode()) {
            setFieldsReadOnly();
        }
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addSectionHeader(JPanel panel, GridBagConstraints gbc, String title, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        headerLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
            new EmptyBorder(8, 0, 8, 0)
        ));
        
        panel.add(headerLabel, gbc);
        
        // Reset for next components
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, Component field) {
        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
    
    private void loadSampleData() {
        if (authManager.isGuestMode()) {
            nameField.setText("Sample Momentum Strategy");
            typeCombo.setSelectedItem("momentum");
            assetClassCombo.setSelectedItem("stocks");
            timeframeCombo.setSelectedItem("1d");
            
            // Select some indicators
            indicatorCheckboxes[0].setSelected(true); // RSI
            indicatorCheckboxes[2].setSelected(true); // SMA
            
            profitTargetField.setText("5");
            stopLossField.setText("2");
            trailingStopCheck.setSelected(false);
            maxRiskField.setText("2");
            maxPositionsField.setText("5");
        }
    }
    
    private void setFieldsReadOnly() {
        nameField.setEditable(false);
        typeCombo.setEnabled(false);
        assetClassCombo.setEnabled(false);
        timeframeCombo.setEnabled(false);
        
        for (JCheckBox checkbox : indicatorCheckboxes) {
            checkbox.setEnabled(false);
        }
        
        profitTargetField.setEditable(false);
        stopLossField.setEditable(false);
        trailingStopCheck.setEnabled(false);
        maxRiskField.setEditable(false);
        maxPositionsField.setEditable(false);
        
        // Set gray background for read-only fields
        Color grayBg = new Color(243, 244, 246);
        nameField.setBackground(grayBg);
        profitTargetField.setBackground(grayBg);
        stopLossField.setBackground(grayBg);
        maxRiskField.setBackground(grayBg);
        maxPositionsField.setBackground(grayBg);
    }
}

/**
 * Strategy Notifications Panel
 */
class StrategyNotifications extends JPanel {
    private AuthenticationManager authManager;
    private DefaultListModel<StrategyNotification> listModel;
    private JList<StrategyNotification> notificationsList;
    
    public StrategyNotifications(AuthenticationManager authManager) {
        this.authManager = authManager;
        setupUI();
        loadSampleNotifications();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        JLabel titleLabel = new JLabel("Messages & Notifications");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JButton clearBtn = new JButton("Clear all");
        clearBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        clearBtn.setEnabled(!authManager.isGuestMode());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(clearBtn, BorderLayout.EAST);
        
        // Notifications list
        listModel = new DefaultListModel<>();
        notificationsList = new JList<>(listModel);
        notificationsList.setCellRenderer(new NotificationCellRenderer());
        notificationsList.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(notificationsList);
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Guest mode footer
        if (authManager.isGuestMode()) {
            JPanel footerPanel = new JPanel(new FlowLayout());
            footerPanel.setBackground(new Color(254, 243, 199)); // warning-100
            
            JLabel footerLabel = new JLabel("📌 Guest mode: Limited notification features. Sign up for full access.");
            footerLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            footerLabel.setForeground(new Color(146, 64, 14)); // warning-800
            
            footerPanel.add(footerLabel);
            add(footerPanel, BorderLayout.SOUTH);
        }
    }
    
    private void loadSampleNotifications() {
        listModel.addElement(new StrategyNotification("info", 
            "Strategy builder loaded successfully", LocalDateTime.now()));
        
        String message = authManager.isGuestMode() ? 
            "Guest mode: Save and backtest features are disabled" : 
            "Ready to build strategies";
        
        listModel.addElement(new StrategyNotification("warning", message, LocalDateTime.now()));
    }
}

/**
 * Strategy Notification class
 */
class StrategyNotification {
    private String type;
    private String message;
    private LocalDateTime timestamp;
    
    public StrategyNotification(String type, String message, LocalDateTime timestamp) {
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    public String getType() { return type; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return message;
    }
}

/**
 * Custom cell renderer for notifications
 */
class NotificationCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if (value instanceof StrategyNotification) {
            StrategyNotification notification = (StrategyNotification) value;
            setText(notification.getMessage());
            
            // Set color based on type
            switch (notification.getType()) {
                case "success":
                    setForeground(new Color(34, 197, 94)); // green-500
                    break;
                case "warning":
                    setForeground(new Color(245, 158, 11)); // yellow-500
                    break;
                case "error":
                    setForeground(new Color(239, 68, 68)); // red-500
                    break;
                default:
                    setForeground(new Color(59, 130, 246)); // blue-500
                    break;
            }
        }
        
        return this;
    }
}

/**
 * Strategy Output Panel - shows generated code
 */
class StrategyOutputPanel extends JPanel {
    private AuthenticationManager authManager;
    private JTabbedPane tabbedPane;
    private JTextArea jsonArea;
    private JTextArea pythonArea;
    private JTextArea javaArea;
    
    public StrategyOutputPanel(AuthenticationManager authManager) {
        this.authManager = authManager;
        setupUI();
        loadSampleCode();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        JLabel titleLabel = new JLabel("Strategy Output");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JButton copyBtn = new JButton("Copy");
        copyBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        copyBtn.setEnabled(!authManager.isGuestMode());
        copyBtn.addActionListener(e -> copyCurrentCode());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(copyBtn, BorderLayout.EAST);
        
        // Tabbed pane for different languages
        tabbedPane = new JTabbedPane();
        
        jsonArea = createCodeArea();
        pythonArea = createCodeArea();
        javaArea = createCodeArea();
        
        tabbedPane.addTab("JSON", new JScrollPane(jsonArea));
        tabbedPane.addTab("Python", new JScrollPane(pythonArea));
        tabbedPane.addTab("Java", new JScrollPane(javaArea));
        
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        // Guest mode banner
        if (authManager.isGuestMode()) {
            JPanel bannerPanel = new JPanel(new BorderLayout());
            bannerPanel.setBackground(new Color(254, 243, 199)); // warning-100
            bannerPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
            
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            leftPanel.setOpaque(false);
            
            JLabel warningIcon = new JLabel("⚠");
            JLabel warningText = new JLabel("Guest Mode - Code is read-only");
            warningText.setFont(new Font("Arial", Font.BOLD, 10));
            warningText.setForeground(new Color(146, 64, 14)); // warning-800
            
            leftPanel.add(warningIcon);
            leftPanel.add(warningText);
            
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightPanel.setOpaque(false);
            
            JLabel signUpText = new JLabel("Sign up to edit and export code");
            signUpText.setFont(new Font("Arial", Font.PLAIN, 10));
            signUpText.setForeground(new Color(180, 83, 9)); // warning-700
            
            JButton signUpBtn = new JButton("Sign Up Free");
            signUpBtn.setBackground(new Color(217, 119, 6)); // warning-600
            signUpBtn.setForeground(Color.WHITE);
            signUpBtn.setFont(new Font("Arial", Font.PLAIN, 10));
            
            rightPanel.add(signUpText);
            rightPanel.add(signUpBtn);
            
            bannerPanel.add(leftPanel, BorderLayout.WEST);
            bannerPanel.add(rightPanel, BorderLayout.EAST);
            
            add(bannerPanel, BorderLayout.SOUTH);
        }
    }
    
    private JTextArea createCodeArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font("Courier New", Font.PLAIN, 11));
        area.setBackground(new Color(17, 24, 39)); // gray-900
        area.setForeground(new Color(34, 197, 94)); // green-400
        area.setEditable(false);
        area.setTabSize(2);
        
        if (authManager.isGuestMode()) {
            // Add slight opacity for guest mode
            area.setBackground(new Color(17, 24, 39, 180));
        }
        
        return area;
    }
    
    private void loadSampleCode() {
        // JSON sample
        jsonArea.setText("{\n" +
            "  \"strategy\": {\n" +
            "    \"name\": \"Sample Momentum Strategy\",\n" +
            "    \"type\": \"momentum\",\n" +
            "    \"assetClass\": \"stocks\",\n" +
            "    \"timeframe\": \"1d\",\n" +
            "    \"entryRules\": {\n" +
            "      \"conditions\": [\n" +
            "        \"RSI > 70\",\n" +
            "        \"Price > SMA20\",\n" +
            "        \"Volume > 1.5x average\"\n" +
            "      ],\n" +
            "      \"indicators\": [\"RSI\", \"SMA\", \"Volume\"]\n" +
            "    },\n" +
            "    \"exitRules\": {\n" +
            "      \"profitTarget\": 5,\n" +
            "      \"stopLoss\": 2,\n" +
            "      \"useTrailingStop\": false\n" +
            "    },\n" +
            "    \"riskManagement\": {\n" +
            "      \"maxRiskPerTrade\": 2,\n" +
            "      \"maxPositions\": 5\n" +
            "    }\n" +
            "  }\n" +
            "}");
        
        // Python sample
        pythonArea.setText("# Sample Trading Strategy - Python Implementation\n" +
            "import pandas as pd\n" +
            "import numpy as np\n" +
            "from trading_framework import Strategy, Indicator\n\n" +
            "class SampleMomentumStrategy(Strategy):\n" +
            "    def __init__(self):\n" +
            "        super().__init__()\n" +
            "        self.asset_class = \"stocks\"\n" +
            "        self.timeframe = \"1d\"\n" +
            "        \n" +
            "    def setup_indicators(self):\n" +
            "        # Technical indicators\n" +
            "        self.rsi = Indicator.RSI(period=14)\n" +
            "        self.sma_20 = Indicator.SMA(period=20)\n" +
            "        self.volume_avg = Indicator.SMA(period=20, field='volume')\n" +
            "        \n" +
            "    def entry_conditions(self, data):\n" +
            "        # Sample entry logic\n" +
            "        return (\n" +
            "            data['rsi'] > 70 and\n" +
            "            data['close'] > data['sma_20'] and\n" +
            "            data['volume'] > data['volume_avg'] * 1.5\n" +
            "        )\n" +
            "        \n" +
            "    def exit_conditions(self, data, position):\n" +
            "        # Sample exit logic\n" +
            "        profit_target = 5.0  # 5%\n" +
            "        stop_loss = 2.0      # 2%\n" +
            "        \n" +
            "        return (\n" +
            "            position.unrealized_pnl_pct >= profit_target or\n" +
            "            position.unrealized_pnl_pct <= -stop_loss\n" +
            "        )\n" +
            "        \n" +
            "    def position_size(self, data):\n" +
            "        # Risk management\n" +
            "        max_risk = 2.0  # 2% risk per trade\n" +
            "        return self.calculate_position_size(max_risk)\n\n" +
            "# Sign up to generate custom strategies!");
        
        // Java sample
        javaArea.setText("// Sample Trading Strategy - Java Implementation\n" +
            "package com.trading.strategies;\n\n" +
            "import com.trading.framework.Strategy;\n" +
            "import com.trading.framework.Indicator;\n" +
            "import com.trading.framework.MarketData;\n" +
            "import com.trading.framework.Position;\n\n" +
            "public class SampleMomentumStrategy extends Strategy {\n" +
            "    \n" +
            "    private Indicator rsi;\n" +
            "    private Indicator sma20;\n" +
            "    private Indicator volumeAvg;\n" +
            "    \n" +
            "    public SampleMomentumStrategy() {\n" +
            "        super();\n" +
            "        this.assetClass = \"stocks\";\n" +
            "        this.timeframe = \"1d\";\n" +
            "        setupIndicators();\n" +
            "    }\n" +
            "    \n" +
            "    private void setupIndicators() {\n" +
            "        this.rsi = new Indicator.RSI(14);\n" +
            "        this.sma20 = new Indicator.SMA(20);\n" +
            "        this.volumeAvg = new Indicator.SMA(20, \"volume\");\n" +
            "    }\n" +
            "    \n" +
            "    @Override\n" +
            "    public boolean entryConditions(MarketData data) {\n" +
            "        return data.getRsi() > 70 &&\n" +
            "               data.getClose() > data.getSma20() &&\n" +
            "               data.getVolume() > data.getVolumeAvg() * 1.5;\n" +
            "    }\n" +
            "    \n" +
            "    @Override\n" +
            "    public boolean exitConditions(MarketData data, Position position) {\n" +
            "        double profitTarget = 5.0; // 5%\n" +
            "        double stopLoss = 2.0;     // 2%\n" +
            "        \n" +
            "        return position.getUnrealizedPnlPct() >= profitTarget ||\n" +
            "               position.getUnrealizedPnlPct() <= -stopLoss;\n" +
            "    }\n" +
            "    \n" +
            "    @Override\n" +
            "    public double positionSize(MarketData data) {\n" +
            "        double maxRisk = 2.0; // 2% risk per trade\n" +
            "        return calculatePositionSize(maxRisk);\n" +
            "    }\n" +
            "}\n\n" +
            "// Sign up to generate custom strategies!");
    }
    
    private void copyCurrentCode() {
        if (authManager.isGuestMode()) return;
        
        JTextArea currentArea = getCurrentCodeArea();
        if (currentArea != null) {
            currentArea.selectAll();
            currentArea.copy();
            currentArea.setSelectionStart(0);
            currentArea.setSelectionEnd(0);
            
            // Show feedback
            JOptionPane.showMessageDialog(this, "Code copied to clipboard!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private JTextArea getCurrentCodeArea() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        switch (selectedIndex) {
            case 0: return jsonArea;
            case 1: return pythonArea;
            case 2: return javaArea;
            default: return null;
        }
    }
}

/**
 * Login Dialog
 */
class LoginDialog extends JDialog {
    private AuthenticationManager authManager;
    private TradingPlatformMain mainFrame;
    private JTextField emailField;
    private JPasswordField passwordField;
    
    public LoginDialog(TradingPlatformMain parent, AuthenticationManager authManager) {
        super(parent, "Log In", true);
        this.authManager = authManager;
        this.mainFrame = parent;
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Log in to your account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        contentPanel.add(titleLabel, gbc);
        
        // Email field
        gbc.gridwidth = 1; gbc.gridy = 1;
        contentPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        contentPanel.add(emailField, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        contentPanel.add(passwordField, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginBtn = new JButton("Sign In");
        JButton cancelBtn = new JButton("Cancel");
        JButton registerBtn = new JButton("Create Account");
        
        loginBtn.addActionListener(e -> handleLogin());
        cancelBtn.addActionListener(e -> dispose());
        registerBtn.addActionListener(e -> {
            dispose();
            mainFrame.showRegisterDialog();
        });
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(registerBtn);
        
        contentPanel.add(buttonPanel, gbc);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Enter key handling
        getRootPane().setDefaultButton(loginBtn);
    }
    
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show loading
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // Simulate login in background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return authManager.login(email, password);
            }
            
            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    boolean success = get();
                    if (success) {
                        dispose();
                        mainFrame.refreshUI();
                    } else {
                        JOptionPane.showMessageDialog(LoginDialog.this, 
                            "Invalid email or password", "Login Failed", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginDialog.this, 
                        "An error occurred. Please try again.", "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
}

/**
 * Register Dialog
 */
class RegisterDialog extends JDialog {
    private AuthenticationManager authManager;
    private TradingPlatformMain mainFrame;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleCombo;
    
    public RegisterDialog(TradingPlatformMain parent, AuthenticationManager authManager) {
        super(parent, "Create Account", true);
        this.authManager = authManager;
        this.mainFrame = parent;
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Create your account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        contentPanel.add(titleLabel, gbc);
        
        // Name field
        gbc.gridwidth = 1; gbc.gridy = 1;
        contentPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        contentPanel.add(nameField, gbc);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        contentPanel.add(emailField, gbc);
        
        // Role field
        gbc.gridx = 0; gbc.gridy = 3;
        contentPanel.add(new JLabel("I am a:"), gbc);
        gbc.gridx = 1;
        roleCombo = new JComboBox<>(new String[]{"Trader", "Analyst"});
        contentPanel.add(roleCombo, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 4;
        contentPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        contentPanel.add(passwordField, gbc);
        
        // Confirm password field
        gbc.gridx = 0; gbc.gridy = 5;
        contentPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        contentPanel.add(confirmPasswordField, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton registerBtn = new JButton("Create Account");
        JButton cancelBtn = new JButton("Cancel");
        JButton loginBtn = new JButton("Sign In Instead");
        
        registerBtn.addActionListener(e -> handleRegister());
        cancelBtn.addActionListener(e -> dispose());
        loginBtn.addActionListener(e -> {
            dispose();
            mainFrame.showLoginDialog();
        });
        
        buttonPanel.add(registerBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(loginBtn);
        
        contentPanel.add(buttonPanel, gbc);
        
        // Terms
        gbc.gridy = 7;
        JLabel termsLabel = new JLabel("<html><center>By signing up, you agree to our Terms of Service and Privacy Policy</center></html>");
        termsLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        termsLabel.setForeground(Color.GRAY);
        contentPanel.add(termsLabel, gbc);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Enter key handling
        getRootPane().setDefaultButton(registerBtn);
    }
    
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();
        
        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show loading
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // Simulate registration in background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return authManager.register(name, email, password, role);
            }
            
            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    boolean success = get();
                    if (success) {
                        dispose();
                        mainFrame.refreshUI();
                    } else {
                        JOptionPane.showMessageDialog(RegisterDialog.this, 
                            "Registration failed. Please try again.", "Registration Failed", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RegisterDialog.this, 
                        "An error occurred. Please try again.", "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
}

/**
 * Custom BorderFactory for rounded borders
 */
class BorderFactory {
    public static javax.swing.border.Border createRoundedBorder(int radius) {
        return new RoundedBorder(radius);
    }
    
    private static class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        
        public RoundedBorder(int radius) {
            this.radius = radius;
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius, radius, radius, radius);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}