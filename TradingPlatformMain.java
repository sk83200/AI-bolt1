import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TradingPlatformMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new TradingPlatform().setVisible(true);
        });
    }
}

class TradingPlatform extends JFrame {
    private AuthenticationManager authManager;
    private WorkspaceManager workspaceManager;
    private boolean isDarkMode = false;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JMenuBar menuBar;
    private JPanel guestBanner;
    
    public TradingPlatform() {
        authManager = new AuthenticationManager();
        workspaceManager = new WorkspaceManager();
        
        initializeUI();
        setupEventHandlers();
        showInitialView();
    }
    
    private void initializeUI() {
        setTitle("AI Trader - Professional Trading Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Create main layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create header
        createHeader();
        
        // Create guest banner (initially hidden)
        createGuestBanner();
        
        // Create main content panels
        createContentPanels();
        
        // Layout
        setLayout(new BorderLayout());
        add(menuBar, BorderLayout.NORTH);
        add(guestBanner, BorderLayout.CENTER);
        
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.add(guestBanner, BorderLayout.NORTH);
        contentWrapper.add(mainPanel, BorderLayout.CENTER);
        add(contentWrapper, BorderLayout.CENTER);
        
        applyTheme();
    }
    
    private void createHeader() {
        menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(0, 60));
        
        // Logo
        JLabel logo = new JLabel("AI Trader");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(new Color(79, 70, 229)); // Primary color
        logo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        // Navigation menu
        JPanel navPanel = new JPanel(new FlowLayout());
        String[] navItems = {"Strategies", "Portfolios", "Indicators", "Charts", "Alerts"};
        
        for (String item : navItems) {
            JButton navButton = new JButton(item);
            navButton.setBorderPainted(false);
            navButton.setContentAreaFilled(false);
            navButton.setFocusPainted(false);
            navButton.addActionListener(e -> navigateToPage(item.toLowerCase()));
            navPanel.add(navButton);
        }
        
        // Right side controls
        JPanel rightPanel = new JPanel(new FlowLayout());
        
        // Theme toggle
        JButton themeToggle = new JButton("🌙");
        themeToggle.setBorderPainted(false);
        themeToggle.setContentAreaFilled(false);
        themeToggle.addActionListener(e -> toggleTheme());
        rightPanel.add(themeToggle);
        
        // User controls (dynamic based on auth state)
        updateUserControls(rightPanel);
        
        // Layout header
        menuBar.setLayout(new BorderLayout());
        menuBar.add(logo, BorderLayout.WEST);
        menuBar.add(navPanel, BorderLayout.CENTER);
        menuBar.add(rightPanel, BorderLayout.EAST);
    }
    
    private void createGuestBanner() {
        guestBanner = new JPanel(new BorderLayout());
        guestBanner.setBackground(new Color(239, 246, 255)); // Light blue
        guestBanner.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        guestBanner.setVisible(false); // Initially hidden
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        JLabel infoIcon = new JLabel("ℹ️");
        JLabel message = new JLabel("You're in Guest Mode. Sign up to run backtests, analyze performance, and save your work.");
        message.setFont(new Font("Arial", Font.PLAIN, 14));
        
        leftPanel.add(infoIcon);
        leftPanel.add(message);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JButton signUpButton = new JButton("Sign Up Free");
        signUpButton.setBackground(new Color(79, 70, 229));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setBorderPainted(false);
        signUpButton.setFocusPainted(false);
        signUpButton.addActionListener(e -> showRegisterDialog());
        
        JButton closeButton = new JButton("✕");
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.addActionListener(e -> guestBanner.setVisible(false));
        
        rightPanel.add(signUpButton);
        rightPanel.add(closeButton);
        
        guestBanner.add(leftPanel, BorderLayout.WEST);
        guestBanner.add(rightPanel, BorderLayout.EAST);
    }
    
    private void createContentPanels() {
        // Home page with AI input
        mainPanel.add(createHomePage(), "home");
        
        // Strategies page with guest mode support
        mainPanel.add(createStrategiesPage(), "strategies");
        
        // Other pages (simplified for demo)
        for (String page : Arrays.asList("portfolios", "indicators", "charts", "alerts")) {
            mainPanel.add(createPlaceholderPage(page), "page");
        }
        
        // Dashboard (authenticated users only)
        mainPanel.add(createDashboard(), "dashboard");
    }
    
    private JPanel createHomePage() {
        JPanel homePanel = new JPanel(new BorderLayout());
        
        if (!authManager.isAuthenticated()) {
            // Non-authenticated home page with AI input
            homePanel.add(createLandingPageWithAI(), BorderLayout.CENTER);
        } else if (authManager.isGuestMode()) {
            // Guest mode - redirect to strategies
            navigateToPage("strategies");
        } else {
            // Authenticated user home
            homePanel.add(createAuthenticatedHome(), BorderLayout.CENTER);
        }
        
        return homePanel;
    }
    
    private JPanel createLandingPageWithAI() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Hero section
        JPanel heroPanel = new JPanel();
        heroPanel.setLayout(new BoxLayout(heroPanel, BoxLayout.Y_AXIS));
        heroPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        heroPanel.setBackground(new Color(30, 58, 138)); // Dark blue
        
        JLabel title = new JLabel("Trade Smarter with AI-Powered Insights");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Harness the power of artificial intelligence to optimize your trading strategies");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitle.setForeground(new Color(200, 200, 200));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        heroPanel.add(title);
        heroPanel.add(Box.createVerticalStrut(20));
        heroPanel.add(subtitle);
        
        // AI Input section
        JPanel aiInputPanel = new JPanel();
        aiInputPanel.setLayout(new BoxLayout(aiInputPanel, BoxLayout.Y_AXIS));
        aiInputPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        JLabel aiTitle = new JLabel("Describe Your Trading Strategy");
        aiTitle.setFont(new Font("Arial", Font.BOLD, 24));
        aiTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextArea aiInput = new JTextArea(3, 50);
        aiInput.setLineWrap(true);
        aiInput.setWrapStyleWord(true);
        aiInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        aiInput.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(aiInput);
        scrollPane.setMaximumSize(new Dimension(800, 100));
        
        JButton submitButton = new JButton("Get Started");
        submitButton.setBackground(new Color(79, 70, 229));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorderPainted(false);
        submitButton.setFocusPainted(false);
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            // Enable guest mode and navigate to strategies
            authManager.loginAsGuest();
            updateUIForAuthState();
            navigateToPage("strategies");
        });
        
        // Quick prompts
        JPanel promptsPanel = new JPanel(new FlowLayout());
        String[] prompts = {"Momentum strategy for tech stocks", "Low-risk dividend portfolio", 
                           "Crypto swing trading strategy", "Market volatility hedge"};
        
        for (String prompt : prompts) {
            JButton promptButton = new JButton(prompt);
            promptButton.setBorderPainted(false);
            promptButton.setContentAreaFilled(false);
            promptButton.addActionListener(e -> {
                aiInput.setText(prompt);
                submitButton.doClick();
            });
            promptsPanel.add(promptButton);
        }
        
        aiInputPanel.add(aiTitle);
        aiInputPanel.add(Box.createVerticalStrut(20));
        aiInputPanel.add(scrollPane);
        aiInputPanel.add(Box.createVerticalStrut(20));
        aiInputPanel.add(submitButton);
        aiInputPanel.add(Box.createVerticalStrut(20));
        aiInputPanel.add(promptsPanel);
        
        panel.add(heroPanel, BorderLayout.NORTH);
        panel.add(aiInputPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStrategiesPage() {
        if (authManager.isAuthenticated() && !authManager.isGuestMode()) {
            // Full strategies page for authenticated users
            return createFullStrategiesPage();
        } else {
            // Guest mode strategies page
            return createGuestStrategiesPage();
        }
    }
    
    private JPanel createGuestStrategiesPage() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.3);
        
        // Left: AI Assistant
        JPanel aiPanel = createGuestAIAssistant();
        
        // Right: Strategy Builder Preview with nested split
        JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightSplit.setDividerLocation(500);
        rightSplit.setResizeWeight(0.6);
        
        // Strategy form (grayed out)
        JPanel strategyForm = createGuestStrategyForm();
        
        // Right side: Notifications and Output
        JSplitPane rightVerticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightVerticalSplit.setDividerLocation(200);
        rightVerticalSplit.setResizeWeight(0.4);
        
        rightVerticalSplit.setTopComponent(createGuestNotificationsPanel());
        rightVerticalSplit.setBottomComponent(createGuestOutputPanel());
        
        rightSplit.setLeftComponent(strategyForm);
        rightSplit.setRightComponent(rightVerticalSplit);
        
        splitPane.setLeftComponent(aiPanel);
        splitPane.setRightComponent(rightSplit);
        
        return splitPane;
    }
    
    private JPanel createFullStrategiesPage() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.6);
        
        // Left: Strategy Form
        JPanel strategyForm = createFullStrategyForm();
        
        // Right: Split between notifications and output
        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplit.setDividerLocation(250);
        rightSplit.setResizeWeight(0.4);
        
        rightSplit.setTopComponent(createNotificationsPanel());
        rightSplit.setBottomComponent(createOutputPanel());
        
        splitPane.setLeftComponent(strategyForm);
        splitPane.setRightComponent(rightSplit);
        
        return splitPane;
    }
    
    private JPanel createGuestAIAssistant() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("AI Assistant (Guest Mode)"));
        
        // Chat area
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setText("Hello! I'm your AI strategy assistant in guest mode. I can provide sample strategies and general guidance. For full features, please sign up!\n\n");
        
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setPreferredSize(new Dimension(380, 400));
        
        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextArea inputArea = new JTextArea(2, 30);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String input = inputArea.getText().trim();
            if (!input.isEmpty()) {
                chatArea.append("You: " + input + "\n\n");
                inputArea.setText("");
                
                // Simulate AI response with guest limitations
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        Thread.sleep(1000); // Simulate processing
                        return generateGuestResponse(input);
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            String response = get();
                            chatArea.append("AI: " + response + "\n\n");
                            if (response.contains("Sign up")) {
                                chatArea.append("📌 To customize and backtest this strategy with real data, please log in.\n");
                                chatArea.append("[Sign Up Free] [Log In]\n\n");
                            }
                            chatArea.setCaretPosition(chatArea.getDocument().getLength());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                worker.execute();
            }
        });
        
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputPanel.add(inputScroll, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        // Rate limiting notice
        JLabel limitNotice = new JLabel("Guest mode: Limited responses. Sign up for unlimited access.");
        limitNotice.setFont(new Font("Arial", Font.ITALIC, 11));
        limitNotice.setForeground(Color.GRAY);
        limitNotice.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(chatScroll, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);
        panel.add(limitNotice, BorderLayout.PAGE_END);
        
        return panel;
    }
    
    private String generateGuestResponse(String input) {
        String lowerInput = input.toLowerCase();
        
        if (lowerInput.contains("momentum") || lowerInput.contains("tech")) {
            return "Here's a sample momentum strategy:\n\n" +
                   "**Tech Momentum Strategy**\n" +
                   "This sample strategy allocates equally to AAPL, TSLA, and NVDA when their 3-month returns exceed 12% and volatility is under 15%.\n\n" +
                   "Key Features:\n" +
                   "• Equal weight allocation (33.3% each)\n" +
                   "• Monthly rebalancing\n" +
                   "• 10% stop-loss protection\n" +
                   "• Targets 15-20% annual returns\n\n" +
                   "Sign up to customize parameters and run backtests.";
        }
        
        if (lowerInput.contains("dividend") || lowerInput.contains("low risk")) {
            return "Here's a sample dividend strategy:\n\n" +
                   "**Dividend Growth Portfolio**\n" +
                   "This conservative strategy focuses on dividend aristocrats with 10+ years of consecutive dividend increases.\n\n" +
                   "Key Features:\n" +
                   "• Focus on dividend aristocrats\n" +
                   "• Quarterly rebalancing\n" +
                   "• 5% maximum position size\n" +
                   "• Target 8-12% annual returns\n\n" +
                   "Sign up to customize this strategy.";
        }
        
        return "I can help you explore different strategy types:\n\n" +
               "• Growth/Momentum - Target stocks with strong price momentum\n" +
               "• Value/Contrarian - Buy undervalued assets\n" +
               "• Income/Dividend - Focus on dividend-paying stocks\n" +
               "• Sector Rotation - Rotate between market sectors\n\n" +
               "Tell me which interests you, and I'll provide a sample template.";
    }
    
    private JPanel createGuestStrategyForm() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Strategy Builder (Read-only)"));
        
        // Create form with sample data
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Strategy name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Strategy Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField("Sample Momentum Strategy", 20);
        nameField.setEditable(false);
        nameField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(nameField, gbc);
        
        // Strategy type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Strategy Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Momentum", "Mean Reversion", "Trend Following"});
        typeCombo.setEnabled(false);
        formPanel.add(typeCombo, gbc);
        
        // Asset class
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Asset Class:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> assetCombo = new JComboBox<>(new String[]{"Stocks", "Crypto", "Forex"});
        assetCombo.setEnabled(false);
        formPanel.add(assetCombo, gbc);
        
        // Entry conditions
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Entry Conditions:"), gbc);
        gbc.gridx = 1;
        JTextArea entryArea = new JTextArea("RSI > 70\nPrice > SMA20\nVolume > 1.5x average", 3, 20);
        entryArea.setEditable(false);
        entryArea.setBackground(Color.LIGHT_GRAY);
        formPanel.add(new JScrollPane(entryArea), gbc);
        
        // Exit rules
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Profit Target (%):"), gbc);
        gbc.gridx = 1;
        JTextField profitField = new JTextField("5.0", 10);
        profitField.setEditable(false);
        profitField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(profitField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Stop Loss (%):"), gbc);
        gbc.gridx = 1;
        JTextField stopField = new JTextField("2.0", 10);
        stopField.setEditable(false);
        stopField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(stopField, gbc);
        
        // Buttons (disabled for guests)
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton saveButton = new JButton("Save (Sign up required)");
        saveButton.setEnabled(false);
        JButton backtestButton = new JButton("Backtest (Sign up required)");
        backtestButton.setEnabled(false);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(backtestButton);
        formPanel.add(buttonPanel, gbc);
        
        // Add grayed overlay
        JPanel overlayPanel = new JPanel();
        overlayPanel.setOpaque(false);
        overlayPanel.setBackground(new Color(128, 128, 128, 100));
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Upgrade prompt at bottom
        JPanel upgradePanel = new JPanel(new BorderLayout());
        upgradePanel.setBackground(new Color(239, 246, 255));
        upgradePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel upgradeLabel = new JLabel("Ready to Create Your Own Strategy?");
        upgradeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        upgradeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton upgradeButton = new JButton("Sign Up Free - Build Custom Strategies");
        upgradeButton.setBackground(new Color(79, 70, 229));
        upgradeButton.setForeground(Color.WHITE);
        upgradeButton.setBorderPainted(false);
        upgradeButton.addActionListener(e -> showRegisterDialog());
        
        upgradePanel.add(upgradeLabel, BorderLayout.NORTH);
        upgradePanel.add(upgradeButton, BorderLayout.CENTER);
        
        panel.add(upgradePanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createGuestNotificationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Messages & Notifications"));
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("ℹ️ Strategy builder loaded successfully");
        listModel.addElement("⚠️ Guest mode: Save and backtest features are disabled");
        listModel.addElement("📌 Guest mode provides sample responses. Sign up for full AI capabilities.");
        
        JList<String> notificationsList = new JList<>(listModel);
        notificationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(notificationsList);
        scrollPane.setPreferredSize(new Dimension(300, 180));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createGuestOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Strategy Output"));
        
        // Language tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // JSON tab
        JTextArea jsonArea = new JTextArea();
        jsonArea.setEditable(false);
        jsonArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        jsonArea.setText("{\n  \"strategy\": {\n    \"name\": \"Sample Momentum Strategy\",\n    \"type\": \"momentum\",\n    \"assetClass\": \"stocks\"\n  }\n}");
        tabbedPane.addTab("JSON", new JScrollPane(jsonArea));
        
        // Python tab
        JTextArea pythonArea = new JTextArea();
        pythonArea.setEditable(false);
        pythonArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        pythonArea.setText("# Sample Trading Strategy - Python\nclass SampleStrategy(Strategy):\n    def __init__(self):\n        self.name = 'Sample Momentum Strategy'\n        # Sign up to generate custom code!");
        tabbedPane.addTab("Python", new JScrollPane(pythonArea));
        
        // Java tab
        JTextArea javaArea = new JTextArea();
        javaArea.setEditable(false);
        javaArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        javaArea.setText("// Sample Trading Strategy - Java\npublic class SampleStrategy {\n    private String name = \"Sample Momentum Strategy\";\n    // Sign up to generate custom code!\n}");
        tabbedPane.addTab("Java", new JScrollPane(javaArea));
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        // Guest mode warning at bottom
        JPanel warningPanel = new JPanel(new BorderLayout());
        warningPanel.setBackground(new Color(255, 243, 205)); // Warning yellow
        warningPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel warningLabel = new JLabel("⚠️ Guest Mode - Code is read-only. Sign up to edit and export code.");
        warningLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JButton signUpButton = new JButton("Sign Up Free");
        signUpButton.setBackground(new Color(245, 158, 11));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setBorderPainted(false);
        signUpButton.addActionListener(e -> showRegisterDialog());
        
        warningPanel.add(warningLabel, BorderLayout.CENTER);
        warningPanel.add(signUpButton, BorderLayout.EAST);
        
        panel.add(warningPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFullStrategyForm() {
        // Full strategy form for authenticated users
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Strategy Builder"));
        
        // Implementation would be similar to guest form but fully functional
        JLabel placeholder = new JLabel("Full Strategy Builder (Authenticated Users)", SwingConstants.CENTER);
        placeholder.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(placeholder, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createNotificationsPanel() {
        // Full notifications panel for authenticated users
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Messages & Notifications"));
        
        JLabel placeholder = new JLabel("Full Notifications Panel", SwingConstants.CENTER);
        panel.add(placeholder, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        // Full output panel for authenticated users
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Strategy Output"));
        
        JLabel placeholder = new JLabel("Full Output Panel with Code Generation", SwingConstants.CENTER);
        panel.add(placeholder, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAuthenticatedHome() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("Welcome back, " + authManager.getCurrentUser().getName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton dashboardButton = new JButton("Go to Dashboard");
        dashboardButton.addActionListener(e -> navigateToPage("dashboard"));
        
        JButton strategiesButton = new JButton("Strategy Builder");
        strategiesButton.addActionListener(e -> navigateToPage("strategies"));
        
        buttonPanel.add(dashboardButton);
        buttonPanel.add(strategiesButton);
        
        panel.add(welcomeLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        
        if (!authManager.isAuthenticated() || authManager.isGuestMode()) {
            JLabel accessDenied = new JLabel("Dashboard access requires authentication", SwingConstants.CENTER);
            panel.add(accessDenied, BorderLayout.CENTER);
        } else {
            JLabel dashboardLabel = new JLabel("Dashboard - Portfolio Overview", SwingConstants.CENTER);
            dashboardLabel.setFont(new Font("Arial", Font.BOLD, 20));
            panel.add(dashboardLabel, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    private JPanel createPlaceholderPage(String pageName) {
        JPanel panel = new JPanel(new BorderLayout());
        
        String title = pageName.substring(0, 1).toUpperCase() + pageName.substring(1);
        JLabel label = new JLabel(title + " - Coming Soon", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        
        if (authManager.isGuestMode()) {
            JPanel guestPanel = new JPanel();
            guestPanel.setLayout(new BoxLayout(guestPanel, BoxLayout.Y_AXIS));
            
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            guestPanel.add(label);
            guestPanel.add(Box.createVerticalStrut(20));
            
            JLabel guestNotice = new JLabel("Limited features in guest mode. Sign up for full access.");
            guestNotice.setAlignmentX(Component.CENTER_ALIGNMENT);
            guestNotice.setForeground(Color.GRAY);
            guestPanel.add(guestNotice);
            
            panel.add(guestPanel, BorderLayout.CENTER);
        } else {
            panel.add(label, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    private void updateUserControls(JPanel rightPanel) {
        // Clear existing user controls
        Component[] components = rightPanel.getComponents();
        for (int i = components.length - 1; i >= 0; i--) {
            if (components[i] instanceof JButton) {
                JButton btn = (JButton) components[i];
                if (btn.getText().contains("Log") || btn.getText().contains("Sign") || 
                    btn.getText().contains("Guest") || btn.getText().contains("User")) {
                    rightPanel.remove(btn);
                }
            }
        }
        
        if (!authManager.isAuthenticated()) {
            // Non-authenticated state
            JButton loginButton = new JButton("Log In");
            loginButton.addActionListener(e -> showLoginDialog());
            
            JButton registerButton = new JButton("Sign Up");
            registerButton.setBackground(new Color(79, 70, 229));
            registerButton.setForeground(Color.WHITE);
            registerButton.setBorderPainted(false);
            registerButton.addActionListener(e -> showRegisterDialog());
            
            rightPanel.add(loginButton);
            rightPanel.add(registerButton);
        } else if (authManager.isGuestMode()) {
            // Guest mode state
            JLabel guestLabel = new JLabel("Guest Mode");
            guestLabel.setForeground(new Color(245, 158, 11)); // Warning color
            guestLabel.setFont(new Font("Arial", Font.BOLD, 12));
            
            JButton loginButton = new JButton("Log In");
            loginButton.addActionListener(e -> showLoginDialog());
            
            JButton signUpButton = new JButton("Sign Up");
            signUpButton.setBackground(new Color(79, 70, 229));
            signUpButton.setForeground(Color.WHITE);
            signUpButton.setBorderPainted(false);
            signUpButton.addActionListener(e -> showRegisterDialog());
            
            rightPanel.add(guestLabel);
            rightPanel.add(loginButton);
            rightPanel.add(signUpButton);
        } else {
            // Authenticated state
            User user = authManager.getCurrentUser();
            JButton userButton = new JButton(user.getName());
            userButton.addActionListener(e -> showUserMenu(userButton));
            
            JLabel statusLabel = new JLabel(user.isPro() ? "Pro" : "Basic");
            statusLabel.setForeground(user.isPro() ? new Color(79, 70, 229) : new Color(245, 158, 11));
            statusLabel.setFont(new Font("Arial", Font.BOLD, 10));
            
            rightPanel.add(statusLabel);
            rightPanel.add(userButton);
        }
        
        rightPanel.revalidate();
        rightPanel.repaint();
    }
    
    private void showUserMenu(JButton userButton) {
        JPopupMenu userMenu = new JPopupMenu();
        
        if (!authManager.getCurrentUser().isPro()) {
            JMenuItem upgradeItem = new JMenuItem("Upgrade to Pro");
            upgradeItem.addActionListener(e -> {
                authManager.upgradeToPro();
                updateUIForAuthState();
            });
            userMenu.add(upgradeItem);
            userMenu.addSeparator();
        }
        
        JMenuItem profileItem = new JMenuItem("Profile Settings");
        profileItem.addActionListener(e -> showProfileDialog());
        userMenu.add(profileItem);
        
        JMenuItem logoutItem = new JMenuItem("Sign Out");
        logoutItem.addActionListener(e -> {
            authManager.logout();
            updateUIForAuthState();
            navigateToPage("home");
        });
        userMenu.add(logoutItem);
        
        userMenu.show(userButton, 0, userButton.getHeight());
    }
    
    private void showLoginDialog() {
        LoginDialog dialog = new LoginDialog(this);
        dialog.setVisible(true);
        
        if (dialog.isSuccessful()) {
            updateUIForAuthState();
            if (authManager.isAuthenticated()) {
                navigateToPage("dashboard");
            }
        }
    }
    
    private void showRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(this);
        dialog.setVisible(true);
        
        if (dialog.isSuccessful()) {
            updateUIForAuthState();
            if (authManager.isAuthenticated()) {
                navigateToPage("dashboard");
            }
        }
    }
    
    private void showProfileDialog() {
        JOptionPane.showMessageDialog(this, "Profile settings would be implemented here.", 
                                    "Profile", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateUIForAuthState() {
        // Update guest banner visibility
        guestBanner.setVisible(authManager.isGuestMode());
        
        // Update user controls in header
        JPanel rightPanel = (JPanel) ((BorderLayout) menuBar.getLayout()).getLayoutComponent(BorderLayout.EAST);
        updateUserControls(rightPanel);
        
        // Recreate content panels to reflect auth state
        mainPanel.removeAll();
        createContentPanels();
        
        // Navigate to appropriate page
        if (authManager.isGuestMode()) {
            navigateToPage("strategies");
        } else if (authManager.isAuthenticated()) {
            navigateToPage("dashboard");
        } else {
            navigateToPage("home");
        }
        
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private void navigateToPage(String page) {
        // Update content based on page and auth state
        switch (page) {
            case "strategies":
                mainPanel.removeAll();
                mainPanel.add(createStrategiesPage(), "current");
                break;
            case "dashboard":
                if (authManager.isAuthenticated() && !authManager.isGuestMode()) {
                    cardLayout.show(mainPanel, "dashboard");
                } else {
                    showLoginDialog();
                    return;
                }
                break;
            default:
                cardLayout.show(mainPanel, page);
                break;
        }
        
        cardLayout.show(mainPanel, "current");
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        applyTheme();
    }
    
    private void applyTheme() {
        Color bgColor = isDarkMode ? new Color(31, 41, 55) : Color.WHITE;
        Color textColor = isDarkMode ? Color.WHITE : Color.BLACK;
        
        // Apply theme to main components
        mainPanel.setBackground(bgColor);
        mainPanel.setForeground(textColor);
        
        // Update theme button
        JPanel rightPanel = (JPanel) ((BorderLayout) menuBar.getLayout()).getLayoutComponent(BorderLayout.EAST);
        for (Component comp : rightPanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().contains("🌙")) {
                ((JButton) comp).setText(isDarkMode ? "☀️" : "🌙");
                break;
            }
        }
        
        repaint();
    }
    
    private void setupEventHandlers() {
        // Window closing handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                workspaceManager.saveWorkspace();
                System.exit(0);
            }
        });
    }
    
    private void showInitialView() {
        if (authManager.isAuthenticated()) {
            if (authManager.isGuestMode()) {
                guestBanner.setVisible(true);
                navigateToPage("strategies");
            } else {
                navigateToPage("dashboard");
            }
        } else {
            navigateToPage("home");
        }
        
        updateUIForAuthState();
    }
}

// Authentication Manager with Guest Mode Support
class AuthenticationManager {
    private User currentUser;
    private boolean isGuestMode = false;
    
    public boolean isAuthenticated() {
        return currentUser != null;
    }
    
    public boolean isGuestMode() {
        return isGuestMode;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean login(String email, String password) {
        // Simulate authentication
        if ("demo@example.com".equals(email) && "password".equals(password)) {
            currentUser = new User("1", "Demo User", email, "Trader", false);
            isGuestMode = false;
            return true;
        }
        return false;
    }
    
    public boolean register(String name, String email, String password, String role) {
        // Simulate registration
        currentUser = new User("1", name, email, role, false);
        isGuestMode = false;
        return true;
    }
    
    public void loginAsGuest() {
        currentUser = new User("guest", "Guest User", "", "Guest", false);
        isGuestMode = true;
    }
    
    public void logout() {
        currentUser = null;
        isGuestMode = false;
    }
    
    public void upgradeToPro() {
        if (currentUser != null && !isGuestMode) {
            currentUser.setPro(true);
        }
    }
}

// User class
class User {
    private String id;
    private String name;
    private String email;
    private String role;
    private boolean isPro;
    
    public User(String id, String name, String email, String role, boolean isPro) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.isPro = isPro;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isPro() { return isPro; }
    public void setPro(boolean pro) { this.isPro = pro; }
}

// Workspace Manager
class WorkspaceManager {
    private Map<String, Object> workspaceData = new HashMap<>();
    
    public void saveWorkspace() {
        // Save workspace state
        System.out.println("Workspace saved");
    }
    
    public void loadWorkspace() {
        // Load workspace state
        System.out.println("Workspace loaded");
    }
    
    public void setData(String key, Object value) {
        workspaceData.put(key, value);
    }
    
    public Object getData(String key) {
        return workspaceData.get(key);
    }
}

// Login Dialog
class LoginDialog extends JDialog {
    private boolean successful = false;
    private JTextField emailField;
    private JPasswordField passwordField;
    
    public LoginDialog(Frame parent) {
        super(parent, "Log In", true);
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Log in to your account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel, gbc);
        
        // Email
        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // Demo credentials hint
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JLabel hintLabel = new JLabel("Demo: demo@example.com / password");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        hintLabel.setForeground(Color.GRAY);
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(hintLabel, gbc);
        
        // Buttons
        gbc.gridy = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton loginButton = new JButton("Sign In");
        loginButton.setBackground(new Color(79, 70, 229));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorderPainted(false);
        loginButton.addActionListener(e -> attemptLogin());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        formPanel.add(buttonPanel, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Enter key handling
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void attemptLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        
        // Get parent's auth manager
        TradingPlatform parent = (TradingPlatform) getParent();
        if (parent.authManager.login(email, password)) {
            successful = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isSuccessful() {
        return successful;
    }
}

// Register Dialog
class RegisterDialog extends JDialog {
    private boolean successful = false;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleCombo;
    
    public RegisterDialog(Frame parent) {
        super(parent, "Create Account", true);
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Create your account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel, gbc);
        
        // Name
        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Email
        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        // Role
        gbc.gridy = 3;
        gbc.gridx = 0;
        formPanel.add(new JLabel("I am a:"), gbc);
        gbc.gridx = 1;
        roleCombo = new JComboBox<>(new String[]{"Trader", "Analyst"});
        formPanel.add(roleCombo, gbc);
        
        // Password
        gbc.gridy = 4;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridy = 5;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);
        
        // Buttons
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton registerButton = new JButton("Create Account");
        registerButton.setBackground(new Color(79, 70, 229));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBorderPainted(false);
        registerButton.addActionListener(e -> attemptRegister());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        formPanel.add(buttonPanel, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Enter key handling
        getRootPane().setDefaultButton(registerButton);
    }
    
    private void attemptRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();
        
        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get parent's auth manager
        TradingPlatform parent = (TradingPlatform) getParent();
        if (parent.authManager.register(name, email, password, role)) {
            successful = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isSuccessful() {
        return successful;
    }
}