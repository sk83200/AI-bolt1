import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class TradingPlatformMain extends JFrame {
    private AuthManager authManager;
    private JPanel mainContentPanel;
    private JPanel taskbarPanel;
    private JPanel aiAssistantPanel;
    private JPanel guiEditorPanel;
    private JPanel messagePanel;
    private JPanel outputPanel;
    private JPanel guestBannerPanel;
    private JSplitPane mainSplitPane;
    private JSplitPane rightSplitPane;
    private boolean showWorkspace = false;
    private String currentPage = "home";
    
    // Taskbar buttons
    private JButton portfoliosBtn, strategiesBtn, indicatorsBtn, chartsBtn, forecastsBtn, alertsBtn;
    
    public TradingPlatformMain() {
        authManager = new AuthManager();
        initializeUI();
        updateUIForAuthState();
    }
    
    private void initializeUI() {
        setTitle("AI Trading Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        // Create main layout
        setLayout(new BorderLayout());
        
        // Create header
        add(createHeader(), BorderLayout.NORTH);
        
        // Create main content area
        mainContentPanel = new JPanel(new BorderLayout());
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Initialize with home page
        showHomePage();
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(31, 41, 55)); // Dark gray
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("AI Trader");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel);
        
        // Taskbar with functionality indicators
        taskbarPanel = createTaskbar();
        
        // User controls
        JPanel userPanel = createUserControls();
        
        header.add(logoPanel, BorderLayout.WEST);
        header.add(taskbarPanel, BorderLayout.CENTER);
        header.add(userPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createTaskbar() {
        JPanel taskbar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        taskbar.setOpaque(false);
        
        // Create taskbar buttons with functionality indicators
        portfoliosBtn = createTaskbarButton("Portfolios", "📊");
        strategiesBtn = createTaskbarButton("Strategies", "⚡");
        indicatorsBtn = createTaskbarButton("Indicators", "📈");
        chartsBtn = createTaskbarButton("Charts", "📉");
        forecastsBtn = createTaskbarButton("Forecasts", "🔮");
        alertsBtn = createTaskbarButton("Alerts", "🔔");
        
        // Add action listeners
        portfoliosBtn.addActionListener(e -> handleTaskbarClick("portfolios"));
        strategiesBtn.addActionListener(e -> handleTaskbarClick("strategies"));
        indicatorsBtn.addActionListener(e -> handleTaskbarClick("indicators"));
        chartsBtn.addActionListener(e -> handleTaskbarClick("charts"));
        forecastsBtn.addActionListener(e -> handleTaskbarClick("forecasts"));
        alertsBtn.addActionListener(e -> handleTaskbarClick("alerts"));
        
        taskbar.add(portfoliosBtn);
        taskbar.add(strategiesBtn);
        taskbar.add(indicatorsBtn);
        taskbar.add(chartsBtn);
        taskbar.add(forecastsBtn);
        taskbar.add(alertsBtn);
        
        return taskbar;
    }
    
    private JButton createTaskbarButton(String text, String icon) {
        JButton button = new JButton("<html><center>" + icon + "<br>" + text + "</center></html>");
        button.setPreferredSize(new Dimension(80, 50));
        button.setBackground(new Color(79, 70, 229)); // Primary color
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(67, 56, 202)); // Darker primary
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(79, 70, 229)); // Original primary
            }
        });
        
        return button;
    }
    
    private JPanel createUserControls() {
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        if (authManager.isAuthenticated() && !authManager.isGuestMode()) {
            // Authenticated user controls
            JLabel userLabel = new JLabel("Welcome, " + authManager.getCurrentUser().getName());
            userLabel.setForeground(Color.WHITE);
            userPanel.add(userLabel);
            
            JButton logoutBtn = new JButton("Logout");
            logoutBtn.addActionListener(e -> logout());
            userPanel.add(logoutBtn);
            
        } else if (authManager.isGuestMode()) {
            // Guest mode indicator
            JLabel guestLabel = new JLabel("Guest Mode");
            guestLabel.setForeground(new Color(251, 191, 36)); // Warning color
            userPanel.add(guestLabel);
            
            JButton loginBtn = new JButton("Sign Up");
            loginBtn.setBackground(new Color(79, 70, 229));
            loginBtn.setForeground(Color.WHITE);
            loginBtn.addActionListener(e -> showLoginDialog());
            userPanel.add(loginBtn);
            
        } else {
            // Non-authenticated user controls
            JButton loginBtn = new JButton("Login");
            loginBtn.addActionListener(e -> showLoginDialog());
            userPanel.add(loginBtn);
            
            JButton registerBtn = new JButton("Sign Up");
            registerBtn.setBackground(new Color(79, 70, 229));
            registerBtn.setForeground(Color.WHITE);
            registerBtn.addActionListener(e -> showRegisterDialog());
            userPanel.add(registerBtn);
        }
        
        return userPanel;
    }
    
    private void handleTaskbarClick(String functionality) {
        currentPage = functionality;
        showWorkspace = true;
        
        // If not authenticated, enable guest mode
        if (!authManager.isAuthenticated()) {
            authManager.loginAsGuest();
        }
        
        showWorkspaceLayout();
        updateUIForAuthState();
    }
    
    private void showHomePage() {
        mainContentPanel.removeAll();
        showWorkspace = false;
        
        if (authManager.isAuthenticated() && !authManager.isGuestMode()) {
            // Authenticated user home page
            showAuthenticatedHomePage();
        } else {
            // Non-authenticated user home page
            showPublicHomePage();
        }
        
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
    
    private void showPublicHomePage() {
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(Color.WHITE);
        
        // Hero section
        JPanel heroPanel = new JPanel(new BorderLayout());
        heroPanel.setBackground(new Color(31, 41, 55));
        heroPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        
        JLabel titleLabel = new JLabel("<html><h1 style='color: white; font-size: 36px;'>Trade Smarter with <span style='color: #F59E0B;'>AI-Powered</span> Insights</h1></html>");
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        textPanel.add(titleLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("<html><p style='color: #D1D5DB; font-size: 18px; margin-top: 20px;'>Harness the power of artificial intelligence to optimize your trading strategies and maximize returns.</p></html>");
        gbc.gridy = 1;
        textPanel.add(subtitleLabel, gbc);
        
        // Call-to-action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        
        JButton getStartedBtn = new JButton("Get Started");
        getStartedBtn.setBackground(new Color(245, 158, 11)); // Accent color
        getStartedBtn.setForeground(Color.WHITE);
        getStartedBtn.setPreferredSize(new Dimension(120, 40));
        getStartedBtn.addActionListener(e -> showRegisterDialog());
        
        JButton loginBtn = new JButton("Log In");
        loginBtn.setBackground(Color.WHITE);
        loginBtn.setForeground(new Color(31, 41, 55));
        loginBtn.setPreferredSize(new Dimension(120, 40));
        loginBtn.addActionListener(e -> showLoginDialog());
        
        buttonPanel.add(getStartedBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(loginBtn);
        
        gbc.gridy = 2; gbc.insets = new Insets(30, 0, 0, 0);
        textPanel.add(buttonPanel, gbc);
        
        heroPanel.add(textPanel, BorderLayout.WEST);
        
        // Chart preview (placeholder)
        JPanel chartPanel = new JPanel();
        chartPanel.setBackground(new Color(55, 65, 81));
        chartPanel.setBorder(BorderFactory.createTitledBorder("Sample Chart"));
        chartPanel.setPreferredSize(new Dimension(400, 300));
        heroPanel.add(chartPanel, BorderLayout.EAST);
        
        homePanel.add(heroPanel, BorderLayout.NORTH);
        
        // Features section
        JPanel featuresPanel = createFeaturesSection();
        homePanel.add(featuresPanel, BorderLayout.CENTER);
        
        mainContentPanel.add(homePanel, BorderLayout.CENTER);
    }
    
    private void showAuthenticatedHomePage() {
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(Color.WHITE);
        
        // Welcome section
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(31, 41, 55));
        welcomePanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        JLabel welcomeLabel = new JLabel("<html><h1 style='color: white; font-size: 32px;'>Welcome back, " + authManager.getCurrentUser().getName() + "</h1></html>");
        JLabel subtitleLabel = new JLabel("<html><p style='color: #D1D5DB; font-size: 18px;'>Continue building your AI-powered trading strategies.</p></html>");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        
        JButton dashboardBtn = new JButton("Go to Dashboard");
        dashboardBtn.setBackground(new Color(245, 158, 11));
        dashboardBtn.setForeground(Color.WHITE);
        dashboardBtn.setPreferredSize(new Dimension(150, 40));
        dashboardBtn.addActionListener(e -> navigateToPage("dashboard"));
        
        JButton strategiesBtn = new JButton("Strategy Builder");
        strategiesBtn.setBackground(Color.WHITE);
        strategiesBtn.setForeground(new Color(31, 41, 55));
        strategiesBtn.setPreferredSize(new Dimension(150, 40));
        strategiesBtn.addActionListener(e -> handleTaskbarClick("strategies"));
        
        buttonPanel.add(dashboardBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(strategiesBtn);
        
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        textPanel.add(welcomeLabel, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(10, 0, 0, 0);
        textPanel.add(subtitleLabel, gbc);
        gbc.gridy = 2; gbc.insets = new Insets(30, 0, 0, 0);
        textPanel.add(buttonPanel, gbc);
        
        welcomePanel.add(textPanel, BorderLayout.WEST);
        
        homePanel.add(welcomePanel, BorderLayout.NORTH);
        homePanel.add(createFeaturesSection(), BorderLayout.CENTER);
        
        mainContentPanel.add(homePanel, BorderLayout.CENTER);
    }
    
    private JPanel createFeaturesSection() {
        JPanel featuresPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        featuresPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        featuresPanel.setBackground(Color.WHITE);
        
        // Feature cards
        String[] features = {
            "Advanced Analytics|Gain insights with powerful analytics tools and real-time market data visualization.",
            "Smart Trading|Execute trades with confidence using AI-driven recommendations and intelligent automation.",
            "AI Assistant|Get personalized trading suggestions and market analysis from our advanced AI system."
        };
        
        for (String feature : features) {
            String[] parts = feature.split("\\|");
            JPanel card = createFeatureCard(parts[0], parts[1]);
            featuresPanel.add(card);
        }
        
        return featuresPanel;
    }
    
    private JPanel createFeatureCard(String title, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("<html><h3>" + title + "</h3></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel descLabel = new JLabel("<html><p style='color: #6B7280;'>" + description + "</p></html>");
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void showWorkspaceLayout() {
        mainContentPanel.removeAll();
        
        // Create guest banner if in guest mode
        if (authManager.isGuestMode()) {
            guestBannerPanel = createGuestBanner();
            mainContentPanel.add(guestBannerPanel, BorderLayout.NORTH);
        }
        
        // Create main workspace
        if (authManager.isAuthenticated() && !authManager.isGuestMode()) {
            // Full workspace for authenticated users
            createFullWorkspace();
        } else {
            // Limited workspace for guests
            createGuestWorkspace();
        }
        
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
    
    private JPanel createGuestBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(254, 243, 199)); // Light yellow
        banner.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel messageLabel = new JLabel("⚠️ You're in Guest Mode. Sign up to run backtests, analyze performance, and save your work.");
        messageLabel.setForeground(new Color(146, 64, 14)); // Dark yellow
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton signUpBtn = new JButton("Sign Up Free");
        signUpBtn.setBackground(new Color(79, 70, 229));
        signUpBtn.setForeground(Color.WHITE);
        signUpBtn.addActionListener(e -> showRegisterDialog());
        
        JButton closeBtn = new JButton("×");
        closeBtn.setBackground(Color.TRANSPARENT);
        closeBtn.setBorder(BorderFactory.createEmptyBorder());
        closeBtn.addActionListener(e -> {
            mainContentPanel.remove(guestBannerPanel);
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        
        buttonPanel.add(signUpBtn);
        buttonPanel.add(closeBtn);
        
        banner.add(messageLabel, BorderLayout.WEST);
        banner.add(buttonPanel, BorderLayout.EAST);
        
        return banner;
    }
    
    private void createFullWorkspace() {
        // Create panels
        guiEditorPanel = createGuiEditorPanel(false);
        messagePanel = createMessagePanel(false);
        outputPanel = createOutputPanel(false);
        
        // Create split panes
        rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, messagePanel, outputPanel);
        rightSplitPane.setDividerLocation(200);
        rightSplitPane.setResizeWeight(0.4);
        
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, guiEditorPanel, rightSplitPane);
        mainSplitPane.setDividerLocation(800);
        mainSplitPane.setResizeWeight(0.6);
        
        mainContentPanel.add(mainSplitPane, BorderLayout.CENTER);
    }
    
    private void createGuestWorkspace() {
        // Create panels with guest restrictions
        aiAssistantPanel = createAiAssistantPanel(true);
        guiEditorPanel = createGuiEditorPanel(true);
        messagePanel = createMessagePanel(true);
        outputPanel = createOutputPanel(true);
        
        // Create split panes
        rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, messagePanel, outputPanel);
        rightSplitPane.setDividerLocation(150);
        rightSplitPane.setResizeWeight(0.4);
        
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, guiEditorPanel, rightSplitPane);
        centerSplitPane.setDividerLocation(500);
        centerSplitPane.setResizeWeight(0.7);
        
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, aiAssistantPanel, centerSplitPane);
        mainSplitPane.setDividerLocation(300);
        mainSplitPane.setResizeWeight(0.3);
        
        mainContentPanel.add(mainSplitPane, BorderLayout.CENTER);
    }
    
    private JPanel createAiAssistantPanel(boolean isGuest) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("AI Assistant" + (isGuest ? " (Guest Mode)" : "")));
        panel.setBackground(Color.WHITE);
        
        // Chat area
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(249, 250, 251));
        chatArea.setText("AI Assistant: Hello! I'm your AI trading assistant" + 
                        (isGuest ? " in guest mode. I can provide sample strategies and general guidance. For full features, please sign up!" : 
                         ". How can I help you build your trading strategy today?"));
        
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setPreferredSize(new Dimension(280, 300));
        
        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField();
        inputField.setEnabled(!isGuest);
        if (isGuest) {
            inputField.setText("Sign up for full AI capabilities");
            inputField.setBackground(new Color(243, 244, 246));
        }
        
        JButton sendBtn = new JButton("Send");
        sendBtn.setEnabled(!isGuest);
        if (isGuest) {
            sendBtn.addActionListener(e -> showRegisterDialog());
        }
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        
        panel.add(chatScroll, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createGuiEditorPanel(boolean isGuest) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Strategy Builder" + (isGuest ? " (Read-only)" : "")));
        panel.setBackground(Color.WHITE);
        
        // Create form components
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Strategy Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Strategy Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(isGuest ? "Sample Momentum Strategy" : "", 20);
        nameField.setEnabled(!isGuest);
        if (isGuest) nameField.setBackground(new Color(243, 244, 246));
        formPanel.add(nameField, gbc);
        
        // Strategy Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Strategy Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Momentum", "Mean Reversion", "Trend Following", "Breakout"});
        typeCombo.setEnabled(!isGuest);
        formPanel.add(typeCombo, gbc);
        
        // Asset Class
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Asset Class:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> assetCombo = new JComboBox<>(new String[]{"Stocks", "Crypto", "Forex", "Commodities"});
        assetCombo.setEnabled(!isGuest);
        formPanel.add(assetCombo, gbc);
        
        // Entry Rules
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Entry Rules:"), gbc);
        gbc.gridx = 1;
        JTextArea entryRules = new JTextArea(isGuest ? "RSI > 70\nPrice > SMA20\nVolume > 1.5x average" : "", 3, 20);
        entryRules.setEnabled(!isGuest);
        if (isGuest) entryRules.setBackground(new Color(243, 244, 246));
        formPanel.add(new JScrollPane(entryRules), gbc);
        
        // Exit Rules
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Exit Rules:"), gbc);
        gbc.gridx = 1;
        JTextArea exitRules = new JTextArea(isGuest ? "Profit Target: 5%\nStop Loss: 2%\nTrailing Stop: No" : "", 3, 20);
        exitRules.setEnabled(!isGuest);
        if (isGuest) exitRules.setBackground(new Color(243, 244, 246));
        formPanel.add(new JScrollPane(exitRules), gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton saveBtn = new JButton(isGuest ? "Save (Sign up required)" : "Save Strategy");
        saveBtn.setEnabled(!isGuest);
        if (isGuest) saveBtn.addActionListener(e -> showRegisterDialog());
        
        JButton backtestBtn = new JButton(isGuest ? "Backtest (Sign up required)" : "Run Backtest");
        backtestBtn.setEnabled(!isGuest);
        if (isGuest) backtestBtn.addActionListener(e -> showRegisterDialog());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(backtestBtn);
        formPanel.add(buttonPanel, gbc);
        
        // Add grayed overlay for guest mode
        if (isGuest) {
            JPanel overlay = new JPanel();
            overlay.setBackground(new Color(128, 128, 128, 50)); // Semi-transparent gray
            panel.setLayout(new OverlayLayout(panel));
            panel.add(overlay);
        }
        
        panel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMessagePanel(boolean isGuest) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Messages & Notifications"));
        panel.setBackground(Color.WHITE);
        
        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(249, 250, 251));
        
        String messages = "✓ Strategy builder loaded successfully\n";
        if (isGuest) {
            messages += "⚠️ Guest mode: Save and backtest features are disabled\n";
            messages += "📌 Sign up for full functionality";
        } else {
            messages += "✓ Ready to build strategies\n";
            messages += "💡 Use AI assistant for suggestions";
        }
        
        messageArea.setText(messages);
        
        JScrollPane scroll = new JScrollPane(messageArea);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createOutputPanel(boolean isGuest) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Strategy Output"));
        panel.setBackground(Color.WHITE);
        
        // Language tabs
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton jsonTab = new JButton("JSON");
        JButton pythonTab = new JButton("Python");
        JButton javaTab = new JButton("Java");
        
        tabPanel.add(jsonTab);
        tabPanel.add(pythonTab);
        tabPanel.add(javaTab);
        
        // Code area
        JTextArea codeArea = new JTextArea();
        codeArea.setEditable(false);
        codeArea.setBackground(new Color(17, 24, 39)); // Dark background
        codeArea.setForeground(new Color(34, 197, 94)); // Green text
        codeArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        
        String sampleCode = isGuest ? 
            "{\n  \"strategy\": {\n    \"name\": \"Sample Momentum Strategy\",\n    \"type\": \"momentum\",\n    \"entryRules\": [\n      \"RSI > 70\",\n      \"Price > SMA20\"\n    ]\n  }\n}\n\n// Sign up to generate custom code!" :
            "// Generated strategy code will appear here\n// Save your strategy to see the output";
        
        codeArea.setText(sampleCode);
        
        JScrollPane codeScroll = new JScrollPane(codeArea);
        
        panel.add(tabPanel, BorderLayout.NORTH);
        panel.add(codeScroll, BorderLayout.CENTER);
        
        // Guest mode banner
        if (isGuest) {
            JPanel guestPanel = new JPanel(new BorderLayout());
            guestPanel.setBackground(new Color(254, 243, 199));
            guestPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
            
            JLabel guestLabel = new JLabel("⚠️ Guest Mode - Code is read-only. Sign up to edit and export code.");
            guestLabel.setForeground(new Color(146, 64, 14));
            
            JButton signUpBtn = new JButton("Sign Up Free");
            signUpBtn.setBackground(new Color(245, 158, 11));
            signUpBtn.setForeground(Color.WHITE);
            signUpBtn.addActionListener(e -> showRegisterDialog());
            
            guestPanel.add(guestLabel, BorderLayout.WEST);
            guestPanel.add(signUpBtn, BorderLayout.EAST);
            
            panel.add(guestPanel, BorderLayout.SOUTH);
        }
        
        return panel;
    }
    
    private void showLoginDialog() {
        JDialog dialog = new JDialog(this, "Login", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> {
            if (authManager.login(emailField.getText(), new String(passwordField.getPassword()))) {
                dialog.dispose();
                updateUIForAuthState();
                if (showWorkspace) {
                    showWorkspaceLayout();
                } else {
                    showHomePage();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Invalid credentials");
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Register", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> {
            if (authManager.register(nameField.getText(), emailField.getText(), new String(passwordField.getPassword()))) {
                dialog.dispose();
                updateUIForAuthState();
                if (showWorkspace) {
                    showWorkspaceLayout();
                } else {
                    showHomePage();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Registration failed");
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(registerBtn, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void logout() {
        authManager.logout();
        showWorkspace = false;
        currentPage = "home";
        updateUIForAuthState();
        showHomePage();
    }
    
    private void navigateToPage(String page) {
        currentPage = page;
        // Implementation for different pages
        JOptionPane.showMessageDialog(this, "Navigating to " + page);
    }
    
    private void updateUIForAuthState() {
        // Update header
        remove(getContentPane().getComponent(0)); // Remove old header
        add(createHeader(), BorderLayout.NORTH, 0); // Add new header
        
        // Update taskbar button states based on authentication
        boolean isAuthenticated = authManager.isAuthenticated() && !authManager.isGuestMode();
        
        portfoliosBtn.setEnabled(true);
        strategiesBtn.setEnabled(true);
        indicatorsBtn.setEnabled(true);
        chartsBtn.setEnabled(true);
        forecastsBtn.setEnabled(true);
        alertsBtn.setEnabled(true);
        
        // Add tooltips for guest mode
        if (authManager.isGuestMode()) {
            portfoliosBtn.setToolTipText("Limited functionality in guest mode");
            strategiesBtn.setToolTipText("Limited functionality in guest mode");
            indicatorsBtn.setToolTipText("Limited functionality in guest mode");
            chartsBtn.setToolTipText("Limited functionality in guest mode");
            forecastsBtn.setToolTipText("Limited functionality in guest mode");
            alertsBtn.setToolTipText("Limited functionality in guest mode");
        }
        
        revalidate();
        repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new TradingPlatformMain().setVisible(true);
        });
    }
}

// Supporting classes
class AuthManager {
    private User currentUser;
    private boolean isAuthenticated = false;
    private boolean guestMode = false;
    
    public boolean login(String email, String password) {
        // Mock authentication
        if ("demo@example.com".equals(email) && "password".equals(password)) {
            currentUser = new User("Demo User", email, false);
            isAuthenticated = true;
            guestMode = false;
            return true;
        }
        return false;
    }
    
    public boolean register(String name, String email, String password) {
        // Mock registration
        currentUser = new User(name, email, false);
        isAuthenticated = true;
        guestMode = false;
        return true;
    }
    
    public void loginAsGuest() {
        currentUser = new User("Guest User", "", false);
        isAuthenticated = true;
        guestMode = true;
    }
    
    public void logout() {
        currentUser = null;
        isAuthenticated = false;
        guestMode = false;
    }
    
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
    
    public boolean isGuestMode() {
        return guestMode;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
}

class User {
    private String name;
    private String email;
    private boolean isPro;
    
    public User(String name, String email, boolean isPro) {
        this.name = name;
        this.email = email;
        this.isPro = isPro;
    }
    
    public String getName() { return name; }
    public String getEmail() { return email; }
    public boolean isPro() { return isPro; }
}