import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TradingPlatformMain extends JFrame {
    private ThemeManager themeManager;
    private AuthManager authManager;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private JButton themeToggleButton;
    private JLabel userStatusLabel;
    private String currentPage = "Home";
    
    public TradingPlatformMain() {
        themeManager = new ThemeManager();
        authManager = new AuthManager();
        
        initializeFrame();
        createHeader();
        createContent();
        applyTheme();
        
        setVisible(true);
    }
    
    private void initializeFrame() {
        setTitle("AI Trader - Professional Trading Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 800));
        setLayout(new BorderLayout());
    }
    
    private void createHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setPreferredSize(new Dimension(0, 70));
        
        // Logo section
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("AI Trader");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(themeManager.getPrimary());
        logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navigateTo("Home");
            }
        });
        
        logoPanel.add(logoLabel);
        
        // Navigation section
        JPanel navPanel = new JPanel(new FlowLayout());
        navPanel.setOpaque(false);
        
        String[] navItems = {"Strategies", "Portfolios", "Indicators", "Charts", "Alerts"};
        for (String item : navItems) {
            JButton navButton = createNavButton(item);
            navPanel.add(navButton);
        }
        
        // Right section
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        // Theme toggle button
        themeToggleButton = new JButton(themeManager.isDarkMode() ? "☀" : "🌙");
        themeToggleButton.setFont(new Font("Arial", Font.PLAIN, 16));
        themeToggleButton.setBorderPainted(false);
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.setContentAreaFilled(false);
        themeToggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeToggleButton.addActionListener(e -> toggleTheme());
        
        // User status
        userStatusLabel = new JLabel();
        updateUserStatus();
        
        rightPanel.add(themeToggleButton);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(userStatusLabel);
        
        // Guest mode banner
        if (authManager.isGuestMode()) {
            JPanel guestBanner = createGuestBanner();
            JPanel headerWithBanner = new JPanel(new BorderLayout());
            headerWithBanner.add(headerPanel, BorderLayout.NORTH);
            headerWithBanner.add(guestBanner, BorderLayout.SOUTH);
            add(headerWithBanner, BorderLayout.NORTH);
        } else {
            headerPanel.add(logoPanel, BorderLayout.WEST);
            headerPanel.add(navPanel, BorderLayout.CENTER);
            headerPanel.add(rightPanel, BorderLayout.EAST);
            add(headerPanel, BorderLayout.NORTH);
        }
    }
    
    private JPanel createGuestBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(254, 243, 199)); // Light yellow
        banner.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        JLabel infoLabel = new JLabel("ℹ You're in Guest Mode. Sign up to run backtests, analyze performance, and save your work.");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(146, 64, 14)); // Dark yellow
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        JButton signUpButton = new JButton("Sign Up Free");
        signUpButton.setBackground(themeManager.getPrimary());
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setBorderPainted(false);
        signUpButton.setFocusPainted(false);
        signUpButton.addActionListener(e -> showRegisterDialog());
        
        JButton dismissButton = new JButton("×");
        dismissButton.setBorderPainted(false);
        dismissButton.setContentAreaFilled(false);
        dismissButton.setFocusPainted(false);
        dismissButton.addActionListener(e -> {
            Container parent = banner.getParent();
            parent.remove(banner);
            parent.revalidate();
            parent.repaint();
        });
        
        buttonPanel.add(signUpButton);
        buttonPanel.add(dismissButton);
        
        banner.add(infoLabel, BorderLayout.WEST);
        banner.add(buttonPanel, BorderLayout.EAST);
        
        return banner;
    }
    
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> handleNavClick(text));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(themeManager.getPrimary());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(themeManager.getText());
            }
        });
        
        return button;
    }
    
    private void handleNavClick(String page) {
        // Enable guest mode if not authenticated
        if (!authManager.isAuthenticated()) {
            authManager.loginAsGuest();
            updateUserStatus();
            recreateHeader();
        }
        navigateTo(page);
    }
    
    private void createContent() {
        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
        
        // Start with landing page
        showLandingPage();
    }
    
    private void showLandingPage() {
        contentPanel.removeAll();
        LandingPage landingPage = new LandingPage(this, themeManager, authManager);
        contentPanel.add(landingPage, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showPlaceholderPage(String pageName) {
        contentPanel.removeAll();
        
        JPanel placeholder = new JPanel(new BorderLayout());
        placeholder.setBackground(themeManager.getBackground());
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));
        
        JLabel titleLabel = new JLabel(pageName);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(themeManager.getText());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel(pageName + " functionality will be implemented here.");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        descLabel.setForeground(themeManager.getSecondaryText());
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add some sample content based on page
        JPanel sampleContent = createSampleContent(pageName);
        
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(descLabel);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(sampleContent);
        
        placeholder.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(placeholder, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createSampleContent(String pageName) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(800, 400));
        
        switch (pageName) {
            case "Strategies":
                panel.add(createSampleCard("📈 Momentum Strategy", "Buy when RSI > 70, Sell when RSI < 30"));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createSampleCard("📉 Mean Reversion", "Buy oversold assets, sell overbought"));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createSampleCard("🎯 Trend Following", "Follow long-term market trends"));
                break;
                
            case "Portfolios":
                panel.add(createSampleCard("💼 Growth Portfolio", "Tech-focused high-growth stocks"));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createSampleCard("🛡️ Conservative Portfolio", "Dividend-paying stable stocks"));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createSampleCard("⚖️ Balanced Portfolio", "Mix of growth and value stocks"));
                break;
                
            case "Indicators":
                panel.add(createSampleCard("📊 RSI", "Relative Strength Index - Momentum oscillator"));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createSampleCard("📈 MACD", "Moving Average Convergence Divergence"));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createSampleCard("📉 Bollinger Bands", "Volatility and trend indicator"));
                break;
                
            case "Charts":
                panel.add(createSampleCard("📊 Candlestick Charts", "OHLC price visualization"));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createSampleCard("📈 Line Charts", "Simple price trend visualization"));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createSampleCard("📉 Volume Charts", "Trading volume analysis"));
                break;
                
            case "Alerts":
                panel.add(createSampleCard("🔔 Price Alerts", "Get notified when price hits target"));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createSampleCard("📧 Email Notifications", "Receive alerts via email"));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createSampleCard("📱 Mobile Push", "Real-time mobile notifications"));
                break;
                
            default:
                panel.add(createSampleCard("🚀 Coming Soon", "This feature is under development"));
        }
        
        return panel;
    }
    
    private JPanel createSampleCard(String title, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(themeManager.getCardBackground());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeManager.getBorder()),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(themeManager.getText());
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(themeManager.getSecondaryText());
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descLabel);
        
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void updateUserStatus() {
        if (authManager.isAuthenticated()) {
            User user = authManager.getCurrentUser();
            if (user.isGuest()) {
                userStatusLabel.setText("Guest Mode");
                userStatusLabel.setForeground(themeManager.getWarning());
            } else {
                String status = user.isPro() ? "Pro" : "Basic";
                userStatusLabel.setText(user.getName() + " (" + status + ")");
                userStatusLabel.setForeground(themeManager.getText());
            }
        } else {
            userStatusLabel.setText("");
        }
    }
    
    private void recreateHeader() {
        // Remove old header
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp == headerPanel || (comp instanceof JPanel && 
                ((JPanel) comp).getComponentCount() > 0 && 
                ((JPanel) comp).getComponent(0) == headerPanel)) {
                getContentPane().remove(comp);
                break;
            }
        }
        
        // Create new header
        createHeader();
        applyTheme();
        revalidate();
        repaint();
    }
    
    private void toggleTheme() {
        themeManager.toggleTheme();
        themeToggleButton.setText(themeManager.isDarkMode() ? "☀" : "🌙");
        applyTheme();
    }
    
    private void applyTheme() {
        // Apply theme to frame
        getContentPane().setBackground(themeManager.getBackground());
        
        // Apply theme to header
        if (headerPanel != null) {
            headerPanel.setBackground(themeManager.getCardBackground());
            applyThemeToComponent(headerPanel);
        }
        
        // Apply theme to content
        if (contentPanel != null) {
            applyThemeToComponent(contentPanel);
        }
        
        repaint();
    }
    
    private void applyThemeToComponent(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel) {
                component.setForeground(themeManager.getText());
            } else if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button == themeToggleButton) {
                    button.setForeground(themeManager.getText());
                } else if (button.getBackground() != null && 
                          !button.getBackground().equals(themeManager.getPrimary())) {
                    button.setForeground(themeManager.getText());
                }
            } else if (component instanceof Container) {
                component.setBackground(themeManager.getBackground());
                applyThemeToComponent((Container) component);
            }
        }
    }
    
    private void showRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog(this, themeManager);
        registerDialog.setVisible(true);
        
        if (registerDialog.isRegistrationSuccessful()) {
            authManager.register(
                registerDialog.getUserName(),
                registerDialog.getUserEmail(),
                "password",
                registerDialog.getUserRole()
            );
            updateUserStatus();
            recreateHeader();
            if (currentPage.equals("Home")) {
                showLandingPage();
            }
        }
    }
    
    private void showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog(this, themeManager);
        loginDialog.setVisible(true);
        
        if (loginDialog.isLoginSuccessful()) {
            authManager.login(loginDialog.getUserEmail(), "password");
            updateUserStatus();
            recreateHeader();
            if (currentPage.equals("Home")) {
                showLandingPage();
            }
        }
    }
    
    public void navigateTo(String page) {
        currentPage = page;
        if (page.equals("Home")) {
            showLandingPage();
        } else {
            showPlaceholderPage(page);
        }
    }
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Enable anti-aliasing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        SwingUtilities.invokeLater(() -> new TradingPlatformMain());
    }
}