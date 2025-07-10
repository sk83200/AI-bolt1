import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class TradingPlatformMain extends JFrame {
    private AuthManager authManager;
    private ThemeManager themeManager;
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
    private JButton themeToggleBtn;
    private JLabel logoLabel;
    
    public TradingPlatformMain() {
        authManager = new AuthManager();
        themeManager = new ThemeManager();
        initializeUI();
        updateUIForAuthState();
    }
    
    private void initializeUI() {
        setTitle("AI Trading Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        // Apply initial theme
        themeManager.applyTheme(this);
        
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
        header.setBackground(themeManager.getHeaderBackground());
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Logo - clickable to return home
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setOpaque(false);
        logoLabel = new JLabel("AI Trader");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(themeManager.getHeaderText());
        logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add click listener to logo for home navigation
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
        
        // Taskbar with functionality indicators
        taskbarPanel = createTaskbar();
        
        // User controls with theme toggle
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
        button.setBackground(themeManager.getPrimaryColor());
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(themeManager.getPrimaryHover());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(themeManager.getPrimaryColor());
            }
        });
        
        return button;
    }
    
    private JPanel createUserControls() {
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        // Theme toggle button
        themeToggleBtn = new JButton(themeManager.isDarkMode() ? "☀️" : "🌙");
        themeToggleBtn.setPreferredSize(new Dimension(40, 30));
        themeToggleBtn.setBackground(themeManager.getSecondaryColor());
        themeToggleBtn.setForeground(themeManager.getHeaderText());
        themeToggleBtn.setBorder(BorderFactory.createEmptyBorder());
        themeToggleBtn.setFocusPainted(false);
        themeToggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeToggleBtn.addActionListener(e -> toggleTheme());
        userPanel.add(themeToggleBtn);
        
        if (authManager.isAuthenticated() && !authManager.isGuestMode()) {
            // Authenticated user controls
            JLabel userLabel = new JLabel("Welcome, " + authManager.getCurrentUser().getName());
            userLabel.setForeground(themeManager.getHeaderText());
            userPanel.add(userLabel);
            
            JButton logoutBtn = new JButton("Logout");
            logoutBtn.setBackground(themeManager.getSecondaryColor());
            logoutBtn.setForeground(themeManager.getHeaderText());
            logoutBtn.addActionListener(e -> logout());
            userPanel.add(logoutBtn);
            
        } else if (authManager.isGuestMode()) {
            // Guest mode indicator
            JLabel guestLabel = new JLabel("Guest Mode");
            guestLabel.setForeground(themeManager.getWarningColor());
            userPanel.add(guestLabel);
            
            JButton loginBtn = new JButton("Sign Up");
            loginBtn.setBackground(themeManager.getPrimaryColor());
            loginBtn.setForeground(Color.WHITE);
            loginBtn.addActionListener(e -> showLoginDialog());
            userPanel.add(loginBtn);
            
        } else {
            // Non-authenticated user controls
            JButton loginBtn = new JButton("Login");
            loginBtn.setBackground(themeManager.getSecondaryColor());
            loginBtn.setForeground(themeManager.getHeaderText());
            loginBtn.addActionListener(e -> showLoginDialog());
            userPanel.add(loginBtn);
            
            JButton registerBtn = new JButton("Sign Up");
            registerBtn.setBackground(themeManager.getPrimaryColor());
            registerBtn.setForeground(Color.WHITE);
            registerBtn.addActionListener(e -> showRegisterDialog());
            userPanel.add(registerBtn);
        }
        
        return userPanel;
    }
    
    private void toggleTheme() {
        themeManager.toggleTheme();
        themeToggleBtn.setText(themeManager.isDarkMode() ? "☀️" : "🌙");
        
        // Refresh the entire UI
        refreshUI();
    }
    
    private void refreshUI() {
        // Apply theme to main frame
        themeManager.applyTheme(this);
        
        // Recreate header with new theme
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && comp.equals(getContentPane().getComponent(0))) {
                remove(comp);
                break;
            }
        }
        add(createHeader(), BorderLayout.NORTH, 0);
        
        // Refresh current content
        if (showWorkspace) {
            showWorkspaceLayout();
        } else {
            showHomePage();
        }
        
        revalidate();
        repaint();
    }
    
    private void navigateToHome() {
        showWorkspace = false;
        currentPage = "home";
        showHomePage();
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
        homePanel.setBackground(themeManager.getBackgroundColor());
        
        // Hero section with enhanced layout
        JPanel heroPanel = new JPanel(new BorderLayout());
        heroPanel.setBackground(themeManager.getHeaderBackground());
        heroPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        // Left side - Text content
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel titleLabel = new JLabel("<html><h1 style='color: " + colorToHex(themeManager.getHeaderText()) + "; font-size: 36px;'>Trade Smarter with <span style='color: " + colorToHex(themeManager.getAccentColor()) + ";'>AI-Powered</span> Insights</h1></html>");
        gbc.gridx = 0; gbc.gridy = 0;
        textPanel.add(titleLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("<html><p style='color: " + colorToHex(themeManager.getSecondaryText()) + "; font-size: 18px; margin-top: 20px; max-width: 500px;'>Harness the power of artificial intelligence to optimize your trading strategies and maximize returns in today's complex markets.</p></html>");
        gbc.gridy = 1; gbc.insets = new Insets(20, 0, 0, 0);
        textPanel.add(subtitleLabel, gbc);
        
        // Call-to-action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        
        JButton getStartedBtn = new JButton("Get Started");
        getStartedBtn.setBackground(themeManager.getAccentColor());
        getStartedBtn.setForeground(Color.WHITE);
        getStartedBtn.setPreferredSize(new Dimension(120, 40));
        getStartedBtn.setFocusPainted(false);
        getStartedBtn.addActionListener(e -> showRegisterDialog());
        
        JButton loginBtn = new JButton("Log In");
        loginBtn.setBackground(themeManager.getSecondaryColor());
        loginBtn.setForeground(themeManager.getText());
        loginBtn.setPreferredSize(new Dimension(120, 40));
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> showLoginDialog());
        
        buttonPanel.add(getStartedBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(loginBtn);
        
        gbc.gridy = 2; gbc.insets = new Insets(30, 0, 0, 0);
        textPanel.add(buttonPanel, gbc);
        
        heroPanel.add(textPanel, BorderLayout.WEST);
        
        // Right side - Enhanced chart preview
        JPanel chartPanel = createEnhancedChart();
        chartPanel.setPreferredSize(new Dimension(500, 350));
        heroPanel.add(chartPanel, BorderLayout.EAST);
        
        homePanel.add(heroPanel, BorderLayout.NORTH);
        
        // Enhanced features section
        JPanel featuresPanel = createEnhancedFeaturesSection();
        homePanel.add(featuresPanel, BorderLayout.CENTER);
        
        // Add pricing section
        JPanel pricingPanel = createPricingSection();
        homePanel.add(pricingPanel, BorderLayout.SOUTH);
        
        mainContentPanel.add(homePanel, BorderLayout.CENTER);
    }
    
    private JPanel createEnhancedChart() {
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw chart background
                g2d.setColor(themeManager.getCardBackground());
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
                
                // Draw chart border
                g2d.setColor(themeManager.getBorderColor());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
                
                // Draw sample price line
                g2d.setColor(themeManager.getPrimaryColor());
                g2d.setStroke(new BasicStroke(3));
                
                int[] xPoints = new int[20];
                int[] yPoints = new int[20];
                
                for (int i = 0; i < 20; i++) {
                    xPoints[i] = 30 + (i * (getWidth() - 60) / 19);
                    yPoints[i] = 50 + (int)(Math.sin(i * 0.3) * 30 + Math.random() * 20) + 100;
                }
                
                for (int i = 0; i < 19; i++) {
                    g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
                }
                
                // Draw chart title
                g2d.setColor(themeManager.getText());
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString("AAPL - Real-time Chart", 30, 40);
                
                // Draw price
                g2d.setColor(themeManager.getSuccessColor());
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("$198.11 (+2.75%)", 30, getHeight() - 30);
                
                g2d.dispose();
            }
        };
        
        chartPanel.setBackground(themeManager.getBackgroundColor());
        chartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor()),
            "Live Market Data",
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            themeManager.getText()
        ));
        
        return chartPanel;
    }
    
    private void showAuthenticatedHomePage() {
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(themeManager.getBackgroundColor());
        
        // Welcome section
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(themeManager.getHeaderBackground());
        welcomePanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel welcomeLabel = new JLabel("<html><h1 style='color: " + colorToHex(themeManager.getHeaderText()) + "; font-size: 32px;'>Welcome back, " + authManager.getCurrentUser().getName() + "</h1></html>");
        gbc.gridx = 0; gbc.gridy = 0;
        textPanel.add(welcomeLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("<html><p style='color: " + colorToHex(themeManager.getSecondaryText()) + "; font-size: 18px;'>Continue building your AI-powered trading strategies.</p></html>");
        gbc.gridy = 1; gbc.insets = new Insets(10, 0, 0, 0);
        textPanel.add(subtitleLabel, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        
        JButton dashboardBtn = new JButton("Go to Dashboard");
        dashboardBtn.setBackground(themeManager.getAccentColor());
        dashboardBtn.setForeground(Color.WHITE);
        dashboardBtn.setPreferredSize(new Dimension(150, 40));
        dashboardBtn.setFocusPainted(false);
        dashboardBtn.addActionListener(e -> navigateToPage("dashboard"));
        
        JButton strategiesBtn = new JButton("Strategy Builder");
        strategiesBtn.setBackground(themeManager.getSecondaryColor());
        strategiesBtn.setForeground(themeManager.getText());
        strategiesBtn.setPreferredSize(new Dimension(150, 40));
        strategiesBtn.setFocusPainted(false);
        strategiesBtn.addActionListener(e -> handleTaskbarClick("strategies"));
        
        buttonPanel.add(dashboardBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(strategiesBtn);
        
        gbc.gridy = 2; gbc.insets = new Insets(30, 0, 0, 0);
        textPanel.add(buttonPanel, gbc);
        
        welcomePanel.add(textPanel, BorderLayout.WEST);
        welcomePanel.add(createEnhancedChart(), BorderLayout.EAST);
        
        homePanel.add(welcomePanel, BorderLayout.NORTH);
        homePanel.add(createEnhancedFeaturesSection(), BorderLayout.CENTER);
        
        mainContentPanel.add(homePanel, BorderLayout.CENTER);
    }
    
    private JPanel createEnhancedFeaturesSection() {
        JPanel featuresPanel = new JPanel(new GridLayout(1, 3, 30, 30));
        featuresPanel.setBorder(new EmptyBorder(60, 60, 60, 60));
        featuresPanel.setBackground(themeManager.getBackgroundColor());
        
        // Enhanced feature cards with better styling
        String[] features = {
            "📊|Advanced Analytics|Gain insights with powerful analytics tools and real-time market data visualization. Monitor trends and performance metrics.",
            "🤖|Smart Trading|Execute trades with confidence using AI-driven recommendations and intelligent automation. Optimize your trading decisions.",
            "💡|AI Assistant|Get personalized trading suggestions and market analysis from our advanced AI system. Available 24/7 for guidance."
        };
        
        for (String feature : features) {
            String[] parts = feature.split("\\|");
            JPanel card = createEnhancedFeatureCard(parts[0], parts[1], parts[2]);
            featuresPanel.add(card);
        }
        
        return featuresPanel;
    }
    
    private JPanel createEnhancedFeatureCard(String icon, String title, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(themeManager.getCardBackground());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Title
        JLabel titleLabel = new JLabel("<html><h3 style='color: " + colorToHex(themeManager.getText()) + "; text-align: center;'>" + title + "</h3></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Description
        JLabel descLabel = new JLabel("<html><p style='color: " + colorToHex(themeManager.getSecondaryText()) + "; text-align: center; line-height: 1.5;'>" + description + "</p></html>");
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 15, 0);
        contentPanel.add(iconLabel, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 10, 0);
        contentPanel.add(titleLabel, gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(descLabel, gbc);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(themeManager.getHoverColor());
                card.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(themeManager.getCardBackground());
                card.repaint();
            }
        });
        
        return card;
    }
    
    private JPanel createPricingSection() {
        JPanel pricingPanel = new JPanel(new BorderLayout());
        pricingPanel.setBackground(themeManager.getSecondaryBackground());
        pricingPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        // Title
        JLabel titleLabel = new JLabel("<html><h2 style='color: " + colorToHex(themeManager.getText()) + "; text-align: center;'>Choose Your Plan</h2></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        
        // Plans
        JPanel plansPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        plansPanel.setOpaque(false);
        plansPanel.setBorder(new EmptyBorder(30, 100, 0, 100));
        
        // Free plan
        JPanel freePlan = createPricingCard("Free", "$0", new String[]{
            "✓ Basic charting tools",
            "✓ Limited market data",
            "✓ Sample strategies"
        }, false);
        
        // Pro plan
        JPanel proPlan = createPricingCard("Pro", "$49", new String[]{
            "✓ Advanced AI insights",
            "✓ Real-time market data",
            "✓ Custom strategies",
            "✓ Backtesting",
            "✓ Portfolio optimization"
        }, true);
        
        plansPanel.add(freePlan);
        plansPanel.add(proPlan);
        
        pricingPanel.add(titleLabel, BorderLayout.NORTH);
        pricingPanel.add(plansPanel, BorderLayout.CENTER);
        
        return pricingPanel;
    }
    
    private JPanel createPricingCard(String planName, String price, String[] features, boolean isHighlighted) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(themeManager.getCardBackground());
        
        if (isHighlighted) {
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getPrimaryColor(), 2),
                new EmptyBorder(25, 25, 25, 25)
            ));
        } else {
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
                new EmptyBorder(25, 25, 25, 25)
            ));
        }
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Plan name
        JLabel nameLabel = new JLabel(planName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nameLabel.setForeground(themeManager.getText());
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 10, 0);
        contentPanel.add(nameLabel, gbc);
        
        // Price
        JLabel priceLabel = new JLabel(price);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 36));
        priceLabel.setForeground(themeManager.getText());
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 20, 0);
        contentPanel.add(priceLabel, gbc);
        
        // Features
        JPanel featuresPanel = new JPanel(new GridLayout(features.length, 1, 0, 5));
        featuresPanel.setOpaque(false);
        
        for (String feature : features) {
            JLabel featureLabel = new JLabel("<html><span style='color: " + colorToHex(themeManager.getSecondaryText()) + ";'>" + feature + "</span></html>");
            featuresPanel.add(featureLabel);
        }
        
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 20, 0);
        contentPanel.add(featuresPanel, gbc);
        
        // Button
        JButton actionBtn = new JButton(planName.equals("Free") ? "Get Started" : "Upgrade to Pro");
        actionBtn.setBackground(isHighlighted ? themeManager.getPrimaryColor() : themeManager.getSecondaryColor());
        actionBtn.setForeground(isHighlighted ? Color.WHITE : themeManager.getText());
        actionBtn.setPreferredSize(new Dimension(150, 40));
        actionBtn.setFocusPainted(false);
        actionBtn.addActionListener(e -> showRegisterDialog());
        
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(actionBtn, gbc);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    // Helper method to convert Color to hex string
    private String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
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
        banner.setBackground(themeManager.getWarningBackground());
        banner.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel messageLabel = new JLabel("⚠️ You're in Guest Mode. Sign up to run backtests, analyze performance, and save your work.");
        messageLabel.setForeground(themeManager.getWarningText());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton signUpBtn = new JButton("Sign Up Free");
        signUpBtn.setBackground(themeManager.getPrimaryColor());
        signUpBtn.setForeground(Color.WHITE);
        signUpBtn.setFocusPainted(false);
        signUpBtn.addActionListener(e -> showRegisterDialog());
        
        JButton closeBtn = new JButton("×");
        closeBtn.setBackground(Color.TRANSPARENT);
        closeBtn.setForeground(themeManager.getWarningText());
        closeBtn.setBorder(BorderFactory.createEmptyBorder());
        closeBtn.setFocusPainted(false);
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
        rightSplitPane.setBackground(themeManager.getBackgroundColor());
        
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, guiEditorPanel, rightSplitPane);
        mainSplitPane.setDividerLocation(800);
        mainSplitPane.setResizeWeight(0.6);
        mainSplitPane.setBackground(themeManager.getBackgroundColor());
        
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
        rightSplitPane.setBackground(themeManager.getBackgroundColor());
        
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, guiEditorPanel, rightSplitPane);
        centerSplitPane.setDividerLocation(500);
        centerSplitPane.setResizeWeight(0.7);
        centerSplitPane.setBackground(themeManager.getBackgroundColor());
        
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, aiAssistantPanel, centerSplitPane);
        mainSplitPane.setDividerLocation(300);
        mainSplitPane.setResizeWeight(0.3);
        mainSplitPane.setBackground(themeManager.getBackgroundColor());
        
        mainContentPanel.add(mainSplitPane, BorderLayout.CENTER);
    }
    
    private JPanel createAiAssistantPanel(boolean isGuest) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor()),
            "AI Assistant" + (isGuest ? " (Guest Mode)" : ""),
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            themeManager.getText()
        ));
        panel.setBackground(themeManager.getCardBackground());
        
        // Chat area
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(themeManager.getBackgroundColor());
        chatArea.setForeground(themeManager.getText());
        chatArea.setFont(new Font("Arial", Font.PLAIN, 12));
        chatArea.setText("AI Assistant: Hello! I'm your AI trading assistant" + 
                        (isGuest ? " in guest mode. I can provide sample strategies and general guidance. For full features, please sign up!" : 
                         ". How can I help you build your trading strategy today?"));
        
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setPreferredSize(new Dimension(280, 300));
        
        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(themeManager.getCardBackground());
        
        JTextField inputField = new JTextField();
        inputField.setEnabled(!isGuest);
        inputField.setBackground(isGuest ? themeManager.getDisabledBackground() : themeManager.getBackgroundColor());
        inputField.setForeground(themeManager.getText());
        if (isGuest) {
            inputField.setText("Sign up for full AI capabilities");
        }
        
        JButton sendBtn = new JButton("Send");
        sendBtn.setEnabled(!isGuest);
        sendBtn.setBackground(themeManager.getPrimaryColor());
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
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
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor()),
            "Strategy Builder" + (isGuest ? " (Read-only)" : ""),
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            themeManager.getText()
        ));
        panel.setBackground(themeManager.getCardBackground());
        
        // Create form components
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(themeManager.getCardBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Strategy Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Strategy Name:");
        nameLabel.setForeground(themeManager.getText());
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(isGuest ? "Sample Momentum Strategy" : "", 20);
        nameField.setEnabled(!isGuest);
        nameField.setBackground(isGuest ? themeManager.getDisabledBackground() : themeManager.getBackgroundColor());
        nameField.setForeground(themeManager.getText());
        formPanel.add(nameField, gbc);
        
        // Strategy Type
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel typeLabel = new JLabel("Strategy Type:");
        typeLabel.setForeground(themeManager.getText());
        formPanel.add(typeLabel, gbc);
        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Momentum", "Mean Reversion", "Trend Following", "Breakout"});
        typeCombo.setEnabled(!isGuest);
        typeCombo.setBackground(themeManager.getBackgroundColor());
        typeCombo.setForeground(themeManager.getText());
        formPanel.add(typeCombo, gbc);
        
        // Asset Class
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel assetLabel = new JLabel("Asset Class:");
        assetLabel.setForeground(themeManager.getText());
        formPanel.add(assetLabel, gbc);
        gbc.gridx = 1;
        JComboBox<String> assetCombo = new JComboBox<>(new String[]{"Stocks", "Crypto", "Forex", "Commodities"});
        assetCombo.setEnabled(!isGuest);
        assetCombo.setBackground(themeManager.getBackgroundColor());
        assetCombo.setForeground(themeManager.getText());
        formPanel.add(assetCombo, gbc);
        
        // Entry Rules
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel entryLabel = new JLabel("Entry Rules:");
        entryLabel.setForeground(themeManager.getText());
        formPanel.add(entryLabel, gbc);
        gbc.gridx = 1;
        JTextArea entryRules = new JTextArea(isGuest ? "RSI > 70\nPrice > SMA20\nVolume > 1.5x average" : "", 3, 20);
        entryRules.setEnabled(!isGuest);
        entryRules.setBackground(isGuest ? themeManager.getDisabledBackground() : themeManager.getBackgroundColor());
        entryRules.setForeground(themeManager.getText());
        formPanel.add(new JScrollPane(entryRules), gbc);
        
        // Exit Rules
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel exitLabel = new JLabel("Exit Rules:");
        exitLabel.setForeground(themeManager.getText());
        formPanel.add(exitLabel, gbc);
        gbc.gridx = 1;
        JTextArea exitRules = new JTextArea(isGuest ? "Profit Target: 5%\nStop Loss: 2%\nTrailing Stop: No" : "", 3, 20);
        exitRules.setEnabled(!isGuest);
        exitRules.setBackground(isGuest ? themeManager.getDisabledBackground() : themeManager.getBackgroundColor());
        exitRules.setForeground(themeManager.getText());
        formPanel.add(new JScrollPane(exitRules), gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(themeManager.getCardBackground());
        
        JButton saveBtn = new JButton(isGuest ? "Save (Sign up required)" : "Save Strategy");
        saveBtn.setEnabled(!isGuest);
        saveBtn.setBackground(isGuest ? themeManager.getDisabledBackground() : themeManager.getPrimaryColor());
        saveBtn.setForeground(isGuest ? themeManager.getSecondaryText() : Color.WHITE);
        saveBtn.setFocusPainted(false);
        if (isGuest) saveBtn.addActionListener(e -> showRegisterDialog());
        
        JButton backtestBtn = new JButton(isGuest ? "Backtest (Sign up required)" : "Run Backtest");
        backtestBtn.setEnabled(!isGuest);
        backtestBtn.setBackground(isGuest ? themeManager.getDisabledBackground() : themeManager.getSecondaryColor());
        backtestBtn.setForeground(isGuest ? themeManager.getSecondaryText() : themeManager.getText());
        backtestBtn.setFocusPainted(false);
        if (isGuest) backtestBtn.addActionListener(e -> showRegisterDialog());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(backtestBtn);
        formPanel.add(buttonPanel, gbc);
        
        panel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMessagePanel(boolean isGuest) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor()),
            "Messages & Notifications",
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            themeManager.getText()
        ));
        panel.setBackground(themeManager.getCardBackground());
        
        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setBackground(themeManager.getBackgroundColor());
        messageArea.setForeground(themeManager.getText());
        messageArea.setFont(new Font("Arial", Font.PLAIN, 12));
        
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
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(themeManager.getBorderColor()),
            "Strategy Output",
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            themeManager.getText()
        ));
        panel.setBackground(themeManager.getCardBackground());
        
        // Language tabs
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tabPanel.setBackground(themeManager.getCardBackground());
        
        JButton jsonTab = new JButton("JSON");
        JButton pythonTab = new JButton("Python");
        JButton javaTab = new JButton("Java");
        
        // Style tabs
        JButton[] tabs = {jsonTab, pythonTab, javaTab};
        for (JButton tab : tabs) {
            tab.setBackground(themeManager.getSecondaryColor());
            tab.setForeground(themeManager.getText());
            tab.setFocusPainted(false);
        }
        
        tabPanel.add(jsonTab);
        tabPanel.add(pythonTab);
        tabPanel.add(javaTab);
        
        // Code area
        JTextArea codeArea = new JTextArea();
        codeArea.setEditable(false);
        codeArea.setBackground(themeManager.isDarkMode() ? new Color(17, 24, 39) : new Color(248, 250, 252));
        codeArea.setForeground(themeManager.isDarkMode() ? new Color(34, 197, 94) : new Color(21, 128, 61));
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
            guestPanel.setBackground(themeManager.getWarningBackground());
            guestPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
            
            JLabel guestLabel = new JLabel("⚠️ Guest Mode - Code is read-only. Sign up to edit and export code.");
            guestLabel.setForeground(themeManager.getWarningText());
            
            JButton signUpBtn = new JButton("Sign Up Free");
            signUpBtn.setBackground(themeManager.getAccentColor());
            signUpBtn.setForeground(Color.WHITE);
            signUpBtn.setFocusPainted(false);
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
        dialog.getContentPane().setBackground(themeManager.getBackgroundColor());
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(themeManager.getBackgroundColor());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JTextField emailField = new JTextField(20);
        emailField.setBackground(themeManager.getCardBackground());
        emailField.setForeground(themeManager.getText());
        
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBackground(themeManager.getCardBackground());
        passwordField.setForeground(themeManager.getText());
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(themeManager.getText());
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(themeManager.getText());
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(themeManager.getPrimaryColor());
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
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
        dialog.getContentPane().setBackground(themeManager.getBackgroundColor());
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(themeManager.getBackgroundColor());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JTextField nameField = new JTextField(20);
        nameField.setBackground(themeManager.getCardBackground());
        nameField.setForeground(themeManager.getText());
        
        JTextField emailField = new JTextField(20);
        emailField.setBackground(themeManager.getCardBackground());
        emailField.setForeground(themeManager.getText());
        
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBackground(themeManager.getCardBackground());
        passwordField.setForeground(themeManager.getText());
        
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(themeManager.getText());
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(themeManager.getText());
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(themeManager.getText());
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(themeManager.getPrimaryColor());
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
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
        // Refresh the entire UI to reflect authentication state changes
        refreshUI();
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

// Theme Manager for dark/light mode support
class ThemeManager {
    private boolean darkMode = false;
    
    // Light theme colors
    private final Color LIGHT_BACKGROUND = Color.WHITE;
    private final Color LIGHT_CARD_BACKGROUND = Color.WHITE;
    private final Color LIGHT_SECONDARY_BACKGROUND = new Color(249, 250, 251);
    private final Color LIGHT_HEADER_BACKGROUND = new Color(31, 41, 55);
    private final Color LIGHT_TEXT = new Color(17, 24, 39);
    private final Color LIGHT_SECONDARY_TEXT = new Color(107, 114, 128);
    private final Color LIGHT_HEADER_TEXT = Color.WHITE;
    private final Color LIGHT_BORDER = new Color(229, 231, 235);
    private final Color LIGHT_HOVER = new Color(243, 244, 246);
    private final Color LIGHT_DISABLED = new Color(243, 244, 246);
    
    // Dark theme colors
    private final Color DARK_BACKGROUND = new Color(17, 24, 39);
    private final Color DARK_CARD_BACKGROUND = new Color(31, 41, 55);
    private final Color DARK_SECONDARY_BACKGROUND = new Color(55, 65, 81);
    private final Color DARK_HEADER_BACKGROUND = new Color(17, 24, 39);
    private final Color DARK_TEXT = new Color(243, 244, 246);
    private final Color DARK_SECONDARY_TEXT = new Color(156, 163, 175);
    private final Color DARK_HEADER_TEXT = Color.WHITE;
    private final Color DARK_BORDER = new Color(75, 85, 99);
    private final Color DARK_HOVER = new Color(55, 65, 81);
    private final Color DARK_DISABLED = new Color(55, 65, 81);
    
    // Common colors
    private final Color PRIMARY = new Color(79, 70, 229);
    private final Color PRIMARY_HOVER = new Color(67, 56, 202);
    private final Color SECONDARY = new Color(107, 114, 128);
    private final Color ACCENT = new Color(245, 158, 11);
    private final Color SUCCESS = new Color(34, 197, 94);
    private final Color WARNING = new Color(251, 191, 36);
    private final Color WARNING_BACKGROUND = new Color(254, 243, 199);
    private final Color WARNING_TEXT = new Color(146, 64, 14);
    
    public void toggleTheme() {
        darkMode = !darkMode;
    }
    
    public boolean isDarkMode() {
        return darkMode;
    }
    
    public void applyTheme(JFrame frame) {
        frame.getContentPane().setBackground(getBackgroundColor());
    }
    
    // Getter methods for colors
    public Color getBackgroundColor() { return darkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND; }
    public Color getCardBackground() { return darkMode ? DARK_CARD_BACKGROUND : LIGHT_CARD_BACKGROUND; }
    public Color getSecondaryBackground() { return darkMode ? DARK_SECONDARY_BACKGROUND : LIGHT_SECONDARY_BACKGROUND; }
    public Color getHeaderBackground() { return darkMode ? DARK_HEADER_BACKGROUND : LIGHT_HEADER_BACKGROUND; }
    public Color getText() { return darkMode ? DARK_TEXT : LIGHT_TEXT; }
    public Color getSecondaryText() { return darkMode ? DARK_SECONDARY_TEXT : LIGHT_SECONDARY_TEXT; }
    public Color getHeaderText() { return darkMode ? DARK_HEADER_TEXT : LIGHT_HEADER_TEXT; }
    public Color getBorderColor() { return darkMode ? DARK_BORDER : LIGHT_BORDER; }
    public Color getHoverColor() { return darkMode ? DARK_HOVER : LIGHT_HOVER; }
    public Color getDisabledBackground() { return darkMode ? DARK_DISABLED : LIGHT_DISABLED; }
    
    public Color getPrimaryColor() { return PRIMARY; }
    public Color getPrimaryHover() { return PRIMARY_HOVER; }
    public Color getSecondaryColor() { return SECONDARY; }
    public Color getAccentColor() { return ACCENT; }
    public Color getSuccessColor() { return SUCCESS; }
    public Color getWarningColor() { return WARNING; }
    public Color getWarningBackground() { return WARNING_BACKGROUND; }
    public Color getWarningText() { return WARNING_TEXT; }
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