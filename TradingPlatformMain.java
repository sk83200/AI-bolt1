import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class TradingPlatformMain extends JFrame {
    private ThemeManager themeManager;
    private boolean showWorkspace = false;
    private String currentPage = "home";
    private boolean isAuthenticated = false;
    private boolean isGuestMode = false;
    private boolean isProUser = false;
    private String userName = "";
    
    public TradingPlatformMain() {
        themeManager = new ThemeManager();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("AI Trading Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Apply theme
        themeManager.applyTheme(this);
        
        // Create main layout
        setLayout(new BorderLayout());
        
        // Add header
        add(createHeader(), BorderLayout.NORTH);
        
        // Show home page initially
        showHomePage();
        
        setVisible(true);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(themeManager.getHeaderBackground());
        header.setBorder(new EmptyBorder(12, 20, 12, 20));
        header.setPreferredSize(new Dimension(0, 70));
        
        // Left side - Logo (clickable)
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("AI Trader");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(themeManager.getHeaderText());
        logoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToHome();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                logoLabel.setForeground(themeManager.getAccentColor());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                logoLabel.setForeground(themeManager.getHeaderText());
            }
        });
        logoPanel.add(logoLabel);
        
        // Center - Navigation (only show when workspace is active)
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        navPanel.setOpaque(false);
        
        if (showWorkspace) {
            String[] navItems = {"Portfolios", "Strategies", "Indicators", "Charts", "Forecasts", "Alerts"};
            for (String item : navItems) {
                JButton navButton = createNavButton(item);
                navPanel.add(navButton);
            }
        }
        
        // Right side - Theme toggle and user menu
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        // Theme toggle button
        JButton themeButton = new JButton(themeManager.isDarkMode() ? "☀" : "🌙");
        themeButton.setFont(new Font("Arial", Font.PLAIN, 16));
        themeButton.setForeground(themeManager.getHeaderText());
        themeButton.setBackground(themeManager.getHeaderBackground());
        themeButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        themeButton.setFocusPainted(false);
        themeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        themeButton.addActionListener(e -> toggleTheme());
        rightPanel.add(themeButton);
        
        // User menu
        if (isAuthenticated && !isGuestMode) {
            JLabel userLabel = new JLabel(userName + (isProUser ? " (Pro)" : " (Basic)"));
            userLabel.setForeground(themeManager.getHeaderText());
            userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            rightPanel.add(userLabel);
            
            JButton logoutButton = new JButton("Logout");
            logoutButton.setForeground(themeManager.getHeaderText());
            logoutButton.setBackground(themeManager.getHeaderBackground());
            logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            logoutButton.setFocusPainted(false);
            logoutButton.addActionListener(e -> logout());
            rightPanel.add(logoutButton);
        } else if (isGuestMode) {
            JLabel guestLabel = new JLabel("Guest Mode");
            guestLabel.setForeground(themeManager.getWarningText());
            guestLabel.setFont(new Font("Arial", Font.BOLD, 12));
            rightPanel.add(guestLabel);
            
            JButton signUpButton = new JButton("Sign Up");
            signUpButton.setForeground(Color.WHITE);
            signUpButton.setBackground(themeManager.getPrimaryColor());
            signUpButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            signUpButton.setFocusPainted(false);
            signUpButton.addActionListener(e -> showRegisterDialog());
            rightPanel.add(signUpButton);
        } else {
            JButton loginButton = new JButton("Login");
            loginButton.setForeground(themeManager.getHeaderText());
            loginButton.setBackground(themeManager.getHeaderBackground());
            loginButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            loginButton.setFocusPainted(false);
            loginButton.addActionListener(e -> showLoginDialog());
            rightPanel.add(loginButton);
            
            JButton registerButton = new JButton("Register");
            registerButton.setForeground(Color.WHITE);
            registerButton.setBackground(themeManager.getPrimaryColor());
            registerButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            registerButton.setFocusPainted(false);
            registerButton.addActionListener(e -> showRegisterDialog());
            rightPanel.add(registerButton);
        }
        
        header.add(logoPanel, BorderLayout.WEST);
        header.add(navPanel, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(themeManager.getHeaderText());
        button.setBackground(themeManager.getHeaderBackground());
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(themeManager.getHoverColor());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(themeManager.getHeaderBackground());
            }
        });
        button.addActionListener(e -> handleNavigation(text));
        return button;
    }
    
    private void navigateToHome() {
        showWorkspace = false;
        currentPage = "home";
        showHomePage();
    }
    
    private void handleNavigation(String item) {
        if (!isAuthenticated && !isGuestMode) {
            showGuestModeDialog();
            return;
        }
        
        showWorkspace = true;
        currentPage = item.toLowerCase();
        showWorkspacePage(item);
    }
    
    private void showHomePage() {
        // Remove existing content
        getContentPane().removeAll();
        add(createHeader(), BorderLayout.NORTH);
        
        if (isAuthenticated && !isGuestMode) {
            add(createAuthenticatedHomePage(), BorderLayout.CENTER);
        } else {
            add(createPublicHomePage(), BorderLayout.CENTER);
        }
        
        refreshUI();
    }
    
    private JPanel createPublicHomePage() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(themeManager.getBackgroundColor());
        
        // Create scrollable content
        JScrollPane scrollPane = new JScrollPane(createHomeContent());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }
    
    private JPanel createHomeContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(themeManager.getBackgroundColor());
        
        // Hero Section
        content.add(createHeroSection());
        
        // AI Input Section
        content.add(createAIInputSection());
        
        // Features Section
        content.add(createFeaturesSection());
        
        // Functionality Explanation Section
        content.add(createFunctionalitySection());
        
        // Pricing Section (at the end)
        content.add(createPricingSection());
        
        return content;
    }
    
    private JPanel createHeroSection() {
        JPanel heroPanel = new JPanel(new BorderLayout());
        heroPanel.setBackground(themeManager.getPrimaryColor());
        heroPanel.setBorder(new EmptyBorder(60, 40, 60, 40));
        heroPanel.setPreferredSize(new Dimension(0, 500));
        
        // Left side - Text content
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(0, 0, 0, 40));
        
        JLabel titleLabel = new JLabel("<html><h1 style='color: white; font-size: 36px; margin-bottom: 10px;'>Trade Smarter with <span style='color: #F59E0B;'>AI-Powered</span> Insights</h1></html>");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("<html><p style='color: rgba(255,255,255,0.8); font-size: 18px; line-height: 1.6; margin-top: 20px;'>Harness the power of artificial intelligence to optimize your trading strategies and maximize returns in today's complex markets.</p></html>");
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(20));
        textPanel.add(subtitleLabel);
        
        // Right side - Chart
        JPanel chartPanel = createEnhancedChart();
        chartPanel.setPreferredSize(new Dimension(400, 300));
        
        heroPanel.add(textPanel, BorderLayout.WEST);
        heroPanel.add(chartPanel, BorderLayout.EAST);
        
        return heroPanel;
    }
    
    private JPanel createAIInputSection() {
        JPanel aiPanel = new JPanel();
        aiPanel.setLayout(new BoxLayout(aiPanel, BoxLayout.Y_AXIS));
        aiPanel.setBackground(themeManager.getBackgroundColor());
        aiPanel.setBorder(new EmptyBorder(60, 40, 60, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Describe Your Trading Strategy");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(themeManager.getText());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Tell our AI what you're looking for and we'll help you build it");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(themeManager.getSecondaryText());
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setMaximumSize(new Dimension(800, 150));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        inputPanel.setBackground(themeManager.getCardBackground());
        
        JTextArea inputArea = new JTextArea(3, 50);
        inputArea.setFont(new Font("Arial", Font.PLAIN, 14));
        inputArea.setForeground(themeManager.getText());
        inputArea.setBackground(themeManager.getCardBackground());
        inputArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setText("Describe your trading strategy... (e.g., 'Create a momentum strategy for tech stocks with 20% annual returns')");
        
        JButton sendButton = new JButton("Get Started");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBackground(themeManager.getPrimaryColor());
        sendButton.setBorder(new EmptyBorder(12, 24, 12, 24));
        sendButton.setFocusPainted(false);
        sendButton.addActionListener(e -> handleAIInput(inputArea.getText()));
        
        JPanel inputContainer = new JPanel(new BorderLayout());
        inputContainer.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        inputContainer.add(sendButton, BorderLayout.EAST);
        
        inputPanel.add(inputContainer, BorderLayout.CENTER);
        
        aiPanel.add(titleLabel);
        aiPanel.add(Box.createVerticalStrut(10));
        aiPanel.add(subtitleLabel);
        aiPanel.add(Box.createVerticalStrut(30));
        aiPanel.add(inputPanel);
        
        return aiPanel;
    }
    
    private JPanel createFeaturesSection() {
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setBackground(themeManager.getCardBackground());
        featuresPanel.setBorder(new EmptyBorder(60, 40, 60, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Professional-Grade Trading Tools");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(themeManager.getText());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Features grid
        JPanel gridPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        gridPanel.setOpaque(false);
        gridPanel.setMaximumSize(new Dimension(1200, 300));
        
        gridPanel.add(createFeatureCard("📊", "Advanced Analytics", 
            "Gain insights with our powerful analytics tools. Monitor market trends and visualize your portfolio performance in real-time."));
        gridPanel.add(createFeatureCard("💰", "Smart Trading", 
            "Execute trades with confidence using our intelligent trading platform enhanced by AI-driven recommendations."));
        gridPanel.add(createFeatureCard("🤖", "AI Assistant", 
            "Get personalized trading suggestions from our AI assistant, helping you make informed decisions based on market data."));
        
        featuresPanel.add(titleLabel);
        featuresPanel.add(Box.createVerticalStrut(40));
        featuresPanel.add(gridPanel);
        
        return featuresPanel;
    }
    
    private JPanel createFunctionalitySection() {
        JPanel funcPanel = new JPanel();
        funcPanel.setLayout(new BoxLayout(funcPanel, BoxLayout.Y_AXIS));
        funcPanel.setBackground(themeManager.getBackgroundColor());
        funcPanel.setBorder(new EmptyBorder(60, 40, 60, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Comprehensive Trading Platform Features");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(themeManager.getText());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Features grid - 2x3 layout
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridPanel.setOpaque(false);
        gridPanel.setMaximumSize(new Dimension(1200, 400));
        
        // Row 1
        gridPanel.add(createFunctionalityCard("📈", "Portfolios", 
            "Create and manage diversified portfolios with AI-powered allocation suggestions and real-time performance tracking."));
        gridPanel.add(createFunctionalityCard("⚡", "Strategies", 
            "Build custom trading strategies using our visual editor with backtesting and optimization capabilities."));
        gridPanel.add(createFunctionalityCard("📊", "Indicators", 
            "Access 50+ technical indicators including RSI, MACD, Bollinger Bands, and custom indicator creation tools."));
        
        // Row 2
        gridPanel.add(createFunctionalityCard("📉", "Charts", 
            "Professional-grade charting with multiple timeframes, drawing tools, and real-time market data visualization."));
        gridPanel.add(createFunctionalityCard("🔮", "Forecasts", 
            "AI-powered price forecasting using machine learning models trained on historical market data and sentiment analysis."));
        gridPanel.add(createFunctionalityCard("🔔", "Alerts", 
            "Set up intelligent price alerts, strategy signals, and market condition notifications with multiple delivery methods."));
        
        funcPanel.add(titleLabel);
        funcPanel.add(Box.createVerticalStrut(40));
        funcPanel.add(gridPanel);
        
        return funcPanel;
    }
    
    private JPanel createPricingSection() {
        JPanel pricingPanel = new JPanel();
        pricingPanel.setLayout(new BoxLayout(pricingPanel, BoxLayout.Y_AXIS));
        pricingPanel.setBackground(themeManager.getCardBackground());
        pricingPanel.setBorder(new EmptyBorder(60, 40, 60, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Choose Your Plan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(themeManager.getText());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Pricing cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(800, 400));
        
        cardsPanel.add(createPricingCard("Free", "$0", "Perfect for getting started", 
            new String[]{"Basic charting tools", "Limited market data", "Sample strategies"}, false));
        cardsPanel.add(createPricingCard("Pro", "$49", "For serious traders", 
            new String[]{"Advanced AI insights", "Real-time market data", "Custom strategies", "Portfolio optimization", "Priority support"}, true));
        
        pricingPanel.add(titleLabel);
        pricingPanel.add(Box.createVerticalStrut(40));
        pricingPanel.add(cardsPanel);
        
        return pricingPanel;
    }
    
    private JPanel createFeatureCard(String icon, String title, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(themeManager.getBackgroundColor());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(30, 20, 30, 20)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(themeManager.getText());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descArea.setForeground(themeManager.getSecondaryText());
        descArea.setBackground(themeManager.getBackgroundColor());
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(descArea);
        
        return card;
    }
    
    private JPanel createFunctionalityCard(String icon, String title, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(themeManager.getCardBackground());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(20, 15, 20, 15)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(themeManager.getText());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setForeground(themeManager.getSecondaryText());
        descArea.setBackground(themeManager.getCardBackground());
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(descArea);
        
        return card;
    }
    
    private JPanel createPricingCard(String planName, String price, String description, String[] features, boolean isPopular) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(themeManager.getCardBackground());
        
        if (isPopular) {
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getPrimaryColor(), 2),
                new EmptyBorder(30, 20, 30, 20)
            ));
        } else {
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
                new EmptyBorder(30, 20, 30, 20)
            ));
        }
        
        JLabel planLabel = new JLabel(planName);
        planLabel.setFont(new Font("Arial", Font.BOLD, 24));
        planLabel.setForeground(themeManager.getText());
        planLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel priceLabel = new JLabel(price);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 36));
        priceLabel.setForeground(themeManager.getText());
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(themeManager.getSecondaryText());
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Features list
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setOpaque(false);
        
        for (String feature : features) {
            JLabel featureLabel = new JLabel("✓ " + feature);
            featureLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            featureLabel.setForeground(themeManager.getSecondaryText());
            featureLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            featuresPanel.add(featureLabel);
            featuresPanel.add(Box.createVerticalStrut(5));
        }
        
        JButton actionButton = new JButton(planName.equals("Free") ? "Get Started" : "Upgrade to Pro");
        actionButton.setFont(new Font("Arial", Font.BOLD, 14));
        actionButton.setForeground(Color.WHITE);
        actionButton.setBackground(isPopular ? themeManager.getPrimaryColor() : themeManager.getSecondaryColor());
        actionButton.setBorder(new EmptyBorder(12, 24, 12, 24));
        actionButton.setFocusPainted(false);
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionButton.addActionListener(e -> {
            if (planName.equals("Free")) {
                showRegisterDialog();
            } else {
                showUpgradeDialog();
            }
        });
        
        card.add(planLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(priceLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(featuresPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(actionButton);
        
        return card;
    }
    
    private JPanel createEnhancedChart() {
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Chart background
                g2d.setColor(new Color(255, 255, 255, 240));
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
                
                // Chart border
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
                
                // Chart title
                g2d.setColor(new Color(60, 60, 60));
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString("AAPL - Apple Inc.", 25, 35);
                
                // Current price
                g2d.setColor(new Color(34, 197, 94));
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("$198.11 (+2.75%)", 25, 55);
                
                // Sample price line
                g2d.setColor(themeManager.getPrimaryColor());
                g2d.setStroke(new BasicStroke(2));
                
                int[] xPoints = new int[20];
                int[] yPoints = new int[20];
                int baseY = getHeight() / 2;
                
                for (int i = 0; i < 20; i++) {
                    xPoints[i] = 30 + (i * (getWidth() - 60) / 19);
                    yPoints[i] = baseY + (int)(Math.sin(i * 0.3) * 30 + Math.random() * 20 - 10);
                }
                
                for (int i = 1; i < 20; i++) {
                    g2d.drawLine(xPoints[i-1], yPoints[i-1], xPoints[i], yPoints[i]);
                }
                
                g2d.dispose();
            }
        };
        
        chartPanel.setOpaque(false);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        return chartPanel;
    }
    
    private JPanel createAuthenticatedHomePage() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(themeManager.getBackgroundColor());
        
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(themeManager.getBackgroundColor());
        welcomePanel.setBorder(new EmptyBorder(60, 40, 60, 40));
        
        JLabel welcomeLabel = new JLabel("Welcome back, " + userName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setForeground(themeManager.getText());
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Continue building your AI-powered trading strategies");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setForeground(themeManager.getSecondaryText());
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        JButton dashboardButton = new JButton("Go to Dashboard");
        dashboardButton.setFont(new Font("Arial", Font.BOLD, 14));
        dashboardButton.setForeground(Color.WHITE);
        dashboardButton.setBackground(themeManager.getAccentColor());
        dashboardButton.setBorder(new EmptyBorder(12, 24, 12, 24));
        dashboardButton.setFocusPainted(false);
        dashboardButton.addActionListener(e -> handleNavigation("Dashboard"));
        
        JButton strategiesButton = new JButton("Strategy Builder");
        strategiesButton.setFont(new Font("Arial", Font.BOLD, 14));
        strategiesButton.setForeground(themeManager.getText());
        strategiesButton.setBackground(themeManager.getCardBackground());
        strategiesButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(12, 24, 12, 24)
        ));
        strategiesButton.setFocusPainted(false);
        strategiesButton.addActionListener(e -> handleNavigation("Strategies"));
        
        buttonPanel.add(dashboardButton);
        buttonPanel.add(strategiesButton);
        
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(subtitleLabel);
        welcomePanel.add(Box.createVerticalStrut(30));
        welcomePanel.add(buttonPanel);
        
        mainPanel.add(welcomePanel, BorderLayout.CENTER);
        return mainPanel;
    }
    
    private void showWorkspacePage(String page) {
        // Remove existing content
        getContentPane().removeAll();
        add(createHeader(), BorderLayout.NORTH);
        
        // Show guest mode banner if applicable
        if (isGuestMode) {
            add(createGuestBanner(), BorderLayout.NORTH);
        }
        
        // Create workspace layout
        JPanel workspacePanel = new JPanel(new BorderLayout());
        workspacePanel.setBackground(themeManager.getBackgroundColor());
        
        // Left panel - AI Assistant
        JPanel leftPanel = createAIAssistantPanel();
        leftPanel.setPreferredSize(new Dimension(350, 0));
        
        // Center panel - Main content
        JPanel centerPanel = createMainContentPanel(page);
        
        // Right panel - Messages and Output
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(300, 0));
        
        // Split right panel vertically
        JPanel messagesPanel = createMessagesPanel();
        messagesPanel.setPreferredSize(new Dimension(0, 200));
        
        JPanel outputPanel = createOutputPanel();
        
        rightPanel.add(messagesPanel, BorderLayout.NORTH);
        rightPanel.add(outputPanel, BorderLayout.CENTER);
        
        workspacePanel.add(leftPanel, BorderLayout.WEST);
        workspacePanel.add(centerPanel, BorderLayout.CENTER);
        workspacePanel.add(rightPanel, BorderLayout.EAST);
        
        add(workspacePanel, BorderLayout.CENTER);
        refreshUI();
    }
    
    private JPanel createGuestBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(254, 243, 199)); // Light yellow
        banner.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel messageLabel = new JLabel("You're in Guest Mode. Sign up to run backtests, analyze performance, and save your work.");
        messageLabel.setForeground(new Color(146, 64, 14)); // Dark yellow
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton signUpButton = new JButton("Sign Up Free");
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setBackground(themeManager.getPrimaryColor());
        signUpButton.setBorder(new EmptyBorder(8, 16, 8, 16));
        signUpButton.setFocusPainted(false);
        signUpButton.addActionListener(e -> showRegisterDialog());
        
        banner.add(messageLabel, BorderLayout.CENTER);
        banner.add(signUpButton, BorderLayout.EAST);
        
        return banner;
    }
    
    private JPanel createAIAssistantPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(themeManager.getCardBackground());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, themeManager.getBorderColor()),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("AI Trading Assistant");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(themeManager.getText());
        
        JTextArea chatArea = new JTextArea(15, 25);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 12));
        chatArea.setForeground(themeManager.getText());
        chatArea.setBackground(themeManager.getCardBackground());
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setText("Hello! I'm your AI trading assistant. How can I help you today?\n\n" +
                        (isGuestMode ? "Note: You're in guest mode with limited functionality." : ""));
        
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 12));
        inputField.setForeground(themeManager.getText());
        inputField.setBackground(themeManager.getCardBackground());
        inputField.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        JButton sendButton = new JButton("Send");
        sendButton.setForeground(Color.WHITE);
        sendButton.setBackground(themeManager.getPrimaryColor());
        sendButton.setBorder(new EmptyBorder(8, 16, 8, 16));
        sendButton.setFocusPainted(false);
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(chatScroll, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createMainContentPanel(String page) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(themeManager.getBackgroundColor());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(page + " - " + (isGuestMode ? "Guest Mode" : "Full Access"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(themeManager.getText());
        
        JTextArea contentArea = new JTextArea();
        contentArea.setFont(new Font("Arial", Font.PLAIN, 14));
        contentArea.setForeground(themeManager.getText());
        contentArea.setBackground(themeManager.getCardBackground());
        contentArea.setEditable(!isGuestMode);
        contentArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentArea.setText(getPageContent(page));
        
        if (isGuestMode) {
            // Add overlay for guest mode
            JPanel overlay = new JPanel();
            overlay.setBackground(new Color(128, 128, 128, 100));
            overlay.setLayout(new BorderLayout());
            
            JLabel overlayLabel = new JLabel("Guest Mode - Limited Functionality", SwingConstants.CENTER);
            overlayLabel.setFont(new Font("Arial", Font.BOLD, 18));
            overlayLabel.setForeground(Color.DARK_GRAY);
            overlay.add(overlayLabel, BorderLayout.CENTER);
            
            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setLayout(new BorderLayout());
            layeredPane.add(new JScrollPane(contentArea), BorderLayout.CENTER);
            layeredPane.add(overlay, BorderLayout.CENTER);
            
            panel.add(titleLabel, BorderLayout.NORTH);
            panel.add(layeredPane, BorderLayout.CENTER);
        } else {
            panel.add(titleLabel, BorderLayout.NORTH);
            panel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    private JPanel createMessagesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(themeManager.getCardBackground());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, themeManager.getBorderColor()),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel("Messages & Alerts");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(themeManager.getText());
        
        JTextArea messagesArea = new JTextArea(8, 20);
        messagesArea.setFont(new Font("Arial", Font.PLAIN, 11));
        messagesArea.setForeground(themeManager.getText());
        messagesArea.setBackground(themeManager.getCardBackground());
        messagesArea.setEditable(false);
        messagesArea.setText("System: Welcome to AI Trader\n" +
                           (isGuestMode ? "Warning: Guest mode active - limited features\n" : "") +
                           "Info: Ready to assist with trading strategies");
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(messagesArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(themeManager.getCardBackground());
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Output / Code");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(themeManager.getText());
        
        JTextArea outputArea = new JTextArea();
        outputArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        outputArea.setForeground(themeManager.getText());
        outputArea.setBackground(new Color(40, 44, 52));
        outputArea.setEditable(false);
        outputArea.setText(isGuestMode ? 
            "// Guest Mode - Code preview only\n// Sign up to edit and export\n\nclass SampleStrategy {\n  // Strategy code here...\n}" :
            "// Generated code will appear here\n// Ready for strategy development");
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private String getPageContent(String page) {
        switch (page.toLowerCase()) {
            case "portfolios":
                return "Portfolio Management\n\n" +
                       "Create and manage diversified portfolios with AI-powered allocation suggestions.\n\n" +
                       "Features:\n" +
                       "• Portfolio optimization\n" +
                       "• Risk analysis\n" +
                       "• Performance tracking\n" +
                       "• Rebalancing recommendations\n\n" +
                       (isGuestMode ? "Sign up to create custom portfolios and run optimizations." : "Ready to build your portfolio strategy.");
                       
            case "strategies":
                return "Strategy Builder\n\n" +
                       "Build custom trading strategies using our visual editor.\n\n" +
                       "Features:\n" +
                       "• Visual strategy designer\n" +
                       "• Backtesting engine\n" +
                       "• Performance optimization\n" +
                       "• Code generation\n\n" +
                       (isGuestMode ? "Sign up to create and backtest custom strategies." : "Ready to build your trading strategy.");
                       
            case "indicators":
                return "Technical Indicators\n\n" +
                       "Access 50+ technical indicators and create custom ones.\n\n" +
                       "Available Indicators:\n" +
                       "• RSI, MACD, Bollinger Bands\n" +
                       "• Moving Averages (SMA, EMA)\n" +
                       "• Momentum indicators\n" +
                       "• Custom indicator builder\n\n" +
                       (isGuestMode ? "Sign up to access all indicators and create custom ones." : "Ready to analyze market data.");
                       
            case "charts":
                return "Professional Charting\n\n" +
                       "Advanced charting tools with real-time data.\n\n" +
                       "Features:\n" +
                       "• Multiple timeframes\n" +
                       "• Drawing tools\n" +
                       "• Technical overlays\n" +
                       "• Real-time data feeds\n\n" +
                       (isGuestMode ? "Sign up to access real-time charts and drawing tools." : "Ready to analyze market charts.");
                       
            case "forecasts":
                return "AI-Powered Forecasting\n\n" +
                       "Machine learning models for price prediction.\n\n" +
                       "Capabilities:\n" +
                       "• Price forecasting\n" +
                       "• Trend analysis\n" +
                       "• Sentiment analysis\n" +
                       "• Market predictions\n\n" +
                       (isGuestMode ? "Sign up to access AI forecasting models." : "Ready to generate market forecasts.");
                       
            case "alerts":
                return "Intelligent Alerts\n\n" +
                       "Set up smart alerts for price movements and strategy signals.\n\n" +
                       "Alert Types:\n" +
                       "• Price alerts\n" +
                       "• Strategy signals\n" +
                       "• Market conditions\n" +
                       "• Portfolio notifications\n\n" +
                       (isGuestMode ? "Sign up to create custom alerts and notifications." : "Ready to set up intelligent alerts.");
                       
            default:
                return "Welcome to " + page + "\n\n" +
                       "This section is under development.\n\n" +
                       (isGuestMode ? "Sign up for full access to all features." : "Full functionality available.");
        }
    }
    
    private void handleAIInput(String input) {
        if (input.trim().isEmpty() || input.contains("Describe your trading strategy")) {
            return;
        }
        
        // Enable guest mode and show workspace
        isGuestMode = true;
        showWorkspace = true;
        currentPage = "strategies";
        showWorkspacePage("Strategies");
    }
    
    private void showGuestModeDialog() {
        JDialog dialog = new JDialog(this, "Choose Access Level", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(themeManager.getCardBackground());
        
        JLabel messageLabel = new JLabel("You need to sign in to access this feature.");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(themeManager.getText());
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        JButton guestButton = new JButton("Continue as Guest");
        guestButton.setForeground(themeManager.getText());
        guestButton.setBackground(themeManager.getCardBackground());
        guestButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(8, 16, 8, 16)
        ));
        guestButton.setFocusPainted(false);
        guestButton.addActionListener(e -> {
            isGuestMode = true;
            dialog.dispose();
            showWorkspace = true;
            showWorkspacePage("Strategies");
        });
        
        JButton loginButton = new JButton("Sign In");
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(themeManager.getPrimaryColor());
        loginButton.setBorder(new EmptyBorder(8, 16, 8, 16));
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> {
            dialog.dispose();
            showLoginDialog();
        });
        
        buttonPanel.add(guestButton);
        buttonPanel.add(loginButton);
        
        content.add(messageLabel);
        content.add(Box.createVerticalStrut(20));
        content.add(buttonPanel);
        
        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void showLoginDialog() {
        JDialog dialog = new JDialog(this, "Login", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(themeManager.getCardBackground());
        
        JLabel titleLabel = new JLabel("Sign In to Your Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(themeManager.getText());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setForeground(themeManager.getText());
        emailField.setBackground(themeManager.getCardBackground());
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setForeground(themeManager.getText());
        passwordField.setBackground(themeManager.getCardBackground());
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        JButton loginButton = new JButton("Sign In");
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(themeManager.getPrimaryColor());
        loginButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> {
            // Mock login
            isAuthenticated = true;
            isGuestMode = false;
            userName = emailField.getText().isEmpty() ? "Demo User" : emailField.getText();
            dialog.dispose();
            showHomePage();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(themeManager.getText());
        cancelButton.setBackground(themeManager.getCardBackground());
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(10, 20, 10, 20)
        ));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(20));
        content.add(new JLabel("Email:"));
        content.add(emailField);
        content.add(Box.createVerticalStrut(10));
        content.add(new JLabel("Password:"));
        content.add(passwordField);
        content.add(Box.createVerticalStrut(20));
        content.add(buttonPanel);
        
        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Create Account", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(themeManager.getCardBackground());
        
        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(themeManager.getText());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setForeground(themeManager.getText());
        nameField.setBackground(themeManager.getCardBackground());
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setForeground(themeManager.getText());
        emailField.setBackground(themeManager.getCardBackground());
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setForeground(themeManager.getText());
        passwordField.setBackground(themeManager.getCardBackground());
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        JButton registerButton = new JButton("Create Account");
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(themeManager.getPrimaryColor());
        registerButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> {
            // Mock registration
            isAuthenticated = true;
            isGuestMode = false;
            userName = nameField.getText().isEmpty() ? "New User" : nameField.getText();
            dialog.dispose();
            showHomePage();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(themeManager.getText());
        cancelButton.setBackground(themeManager.getCardBackground());
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(10, 20, 10, 20)
        ));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(20));
        content.add(new JLabel("Full Name:"));
        content.add(nameField);
        content.add(Box.createVerticalStrut(10));
        content.add(new JLabel("Email:"));
        content.add(emailField);
        content.add(Box.createVerticalStrut(10));
        content.add(new JLabel("Password:"));
        content.add(passwordField);
        content.add(Box.createVerticalStrut(20));
        content.add(buttonPanel);
        
        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void showUpgradeDialog() {
        JDialog dialog = new JDialog(this, "Upgrade to Pro", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(themeManager.getCardBackground());
        
        JLabel titleLabel = new JLabel("Upgrade to Pro - $49/month");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(themeManager.getText());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextArea featuresArea = new JTextArea();
        featuresArea.setFont(new Font("Arial", Font.PLAIN, 14));
        featuresArea.setForeground(themeManager.getText());
        featuresArea.setBackground(themeManager.getCardBackground());
        featuresArea.setEditable(false);
        featuresArea.setOpaque(false);
        featuresArea.setText("Pro Features:\n\n" +
                            "✓ Advanced AI insights\n" +
                            "✓ Real-time market data\n" +
                            "✓ Custom strategies\n" +
                            "✓ Portfolio optimization\n" +
                            "✓ Priority support\n" +
                            "✓ Unlimited backtesting");
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        JButton upgradeButton = new JButton("Upgrade Now");
        upgradeButton.setForeground(Color.WHITE);
        upgradeButton.setBackground(themeManager.getPrimaryColor());
        upgradeButton.setBorder(new EmptyBorder(12, 24, 12, 24));
        upgradeButton.setFocusPainted(false);
        upgradeButton.addActionListener(e -> {
            isProUser = true;
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Successfully upgraded to Pro!", "Upgrade Complete", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton cancelButton = new JButton("Maybe Later");
        cancelButton.setForeground(themeManager.getText());
        cancelButton.setBackground(themeManager.getCardBackground());
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(12, 24, 12, 24)
        ));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(upgradeButton);
        buttonPanel.add(cancelButton);
        
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(20));
        content.add(featuresArea);
        content.add(Box.createVerticalStrut(20));
        content.add(buttonPanel);
        
        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void logout() {
        isAuthenticated = false;
        isGuestMode = false;
        isProUser = false;
        userName = "";
        showWorkspace = false;
        currentPage = "home";
        showHomePage();
    }
    
    private void toggleTheme() {
        themeManager.toggleTheme();
        refreshUI();
    }
    
    private void refreshUI() {
        themeManager.applyTheme(this);
        // Recreate header with new theme
        remove(getContentPane().getComponent(0));
        add(createHeader(), BorderLayout.NORTH, 0);
        // Refresh all content
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
            new TradingPlatformMain();
        });
    }
}

// Theme Manager Class
class ThemeManager {
    private boolean darkMode = false;
    
    // Light theme colors
    private final Color LIGHT_BACKGROUND = Color.WHITE;
    private final Color LIGHT_CARD_BACKGROUND = new Color(249, 250, 251);
    private final Color LIGHT_TEXT = new Color(17, 24, 39);
    private final Color LIGHT_SECONDARY_TEXT = new Color(107, 114, 128);
    private final Color LIGHT_HEADER_BACKGROUND = Color.WHITE;
    private final Color LIGHT_HEADER_TEXT = new Color(17, 24, 39);
    private final Color LIGHT_BORDER = new Color(229, 231, 235);
    private final Color LIGHT_HOVER = new Color(243, 244, 246);
    
    // Dark theme colors
    private final Color DARK_BACKGROUND = new Color(17, 24, 39);
    private final Color DARK_CARD_BACKGROUND = new Color(31, 41, 55);
    private final Color DARK_TEXT = new Color(243, 244, 246);
    private final Color DARK_SECONDARY_TEXT = new Color(156, 163, 175);
    private final Color DARK_HEADER_BACKGROUND = new Color(17, 24, 39);
    private final Color DARK_HEADER_TEXT = new Color(243, 244, 246);
    private final Color DARK_BORDER = new Color(75, 85, 99);
    private final Color DARK_HOVER = new Color(55, 65, 81);
    
    // Common colors
    private final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private final Color SECONDARY_COLOR = new Color(107, 114, 128);
    private final Color ACCENT_COLOR = new Color(245, 158, 11);
    private final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private final Color WARNING_COLOR = new Color(245, 158, 11);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    
    public boolean isDarkMode() {
        return darkMode;
    }
    
    public void toggleTheme() {
        darkMode = !darkMode;
    }
    
    public Color getBackgroundColor() {
        return darkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }
    
    public Color getCardBackground() {
        return darkMode ? DARK_CARD_BACKGROUND : LIGHT_CARD_BACKGROUND;
    }
    
    public Color getText() {
        return darkMode ? DARK_TEXT : LIGHT_TEXT;
    }
    
    public Color getSecondaryText() {
        return darkMode ? DARK_SECONDARY_TEXT : LIGHT_SECONDARY_TEXT;
    }
    
    public Color getHeaderBackground() {
        return darkMode ? DARK_HEADER_BACKGROUND : LIGHT_HEADER_BACKGROUND;
    }
    
    public Color getHeaderText() {
        return darkMode ? DARK_HEADER_TEXT : LIGHT_HEADER_TEXT;
    }
    
    public Color getBorderColor() {
        return darkMode ? DARK_BORDER : LIGHT_BORDER;
    }
    
    public Color getHoverColor() {
        return darkMode ? DARK_HOVER : LIGHT_HOVER;
    }
    
    public Color getPrimaryColor() {
        return PRIMARY_COLOR;
    }
    
    public Color getSecondaryColor() {
        return SECONDARY_COLOR;
    }
    
    public Color getAccentColor() {
        return ACCENT_COLOR;
    }
    
    public Color getSuccessColor() {
        return SUCCESS_COLOR;
    }
    
    public Color getWarningColor() {
        return WARNING_COLOR;
    }
    
    public Color getWarningText() {
        return new Color(146, 64, 14);
    }
    
    public Color getErrorColor() {
        return ERROR_COLOR;
    }
    
    public void applyTheme(JFrame frame) {
        frame.getContentPane().setBackground(getBackgroundColor());
    }
}