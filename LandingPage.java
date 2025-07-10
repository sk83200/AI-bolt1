import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class LandingPage extends JPanel {
    private ThemeManager themeManager;
    private AuthManager authManager;
    private JFrame parentFrame;
    private JTextArea aiInputArea;
    
    public LandingPage(JFrame parentFrame, ThemeManager themeManager, AuthManager authManager) {
        this.parentFrame = parentFrame;
        this.themeManager = themeManager;
        this.authManager = authManager;
        
        setLayout(new BorderLayout());
        initializeComponents();
        applyTheme();
    }
    
    private void initializeComponents() {
        // Create main scroll pane
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        // Add sections
        mainPanel.add(createHeroSection());
        
        if (!authManager.isAuthenticated()) {
            mainPanel.add(createAiInputSection());
        }
        
        mainPanel.add(createFeaturesSection());
        mainPanel.add(createPricingSection());
        
        scrollPane.setViewportView(mainPanel);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeroSection() {
        JPanel heroPanel = new JPanel(new BorderLayout());
        heroPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        heroPanel.setPreferredSize(new Dimension(0, 500));
        
        // Create gradient background
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                Color startColor = themeManager.isDarkMode() ? 
                    new Color(31, 41, 55) : new Color(79, 70, 229);
                Color endColor = themeManager.isDarkMode() ? 
                    new Color(17, 24, 39) : new Color(67, 56, 202);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, startColor,
                    getWidth(), getHeight(), endColor
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(new GridLayout(1, 2, 40, 0));
        
        // Left side - Text content
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("<html><div style='text-align: center;'>" +
            "Trade Smarter with<br>" +
            "<span style='color: #F59E0B;'>AI-Powered</span><br>" +
            "Insights</div></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("<html><div style='text-align: center; width: 400px;'>" +
            "Harness the power of artificial intelligence to optimize your trading strategies " +
            "and maximize returns in today's complex markets.</div></html>");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        if (!authManager.isAuthenticated()) {
            JButton getStartedButton = createStyledButton("Get Started", themeManager.getAccent());
            JButton loginButton = createStyledButton("Log In", Color.WHITE, themeManager.getPrimary());
            
            getStartedButton.addActionListener(e -> showRegisterDialog());
            loginButton.addActionListener(e -> showLoginDialog());
            
            buttonPanel.add(getStartedButton);
            buttonPanel.add(loginButton);
        } else {
            JLabel welcomeLabel = new JLabel("Welcome back, " + authManager.getCurrentUser().getName());
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
            welcomeLabel.setForeground(themeManager.getAccent());
            welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JButton dashboardButton = createStyledButton("Go to Dashboard", themeManager.getAccent());
            JButton strategyButton = createStyledButton("Strategy Builder", Color.WHITE, themeManager.getPrimary());
            
            dashboardButton.addActionListener(e -> navigateTo("Dashboard"));
            strategyButton.addActionListener(e -> navigateTo("Trading"));
            
            textPanel.add(welcomeLabel);
            textPanel.add(Box.createVerticalStrut(20));
            buttonPanel.add(dashboardButton);
            buttonPanel.add(strategyButton);
        }
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(20));
        textPanel.add(subtitleLabel);
        textPanel.add(Box.createVerticalStrut(30));
        textPanel.add(buttonPanel);
        
        // Right side - Chart preview
        JPanel chartPanel = createChartPreview();
        
        gradientPanel.add(textPanel);
        gradientPanel.add(chartPanel);
        
        heroPanel.add(gradientPanel, BorderLayout.CENTER);
        return heroPanel;
    }
    
    private JPanel createAiInputSection() {
        JPanel aiSection = new JPanel();
        aiSection.setLayout(new BoxLayout(aiSection, BoxLayout.Y_AXIS));
        aiSection.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Describe Your Trading Strategy");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Tell our AI what you're looking for and we'll help you build it");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // AI Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        inputPanel.setMaximumSize(new Dimension(800, 200));
        
        JPanel inputContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g2d.setColor(themeManager.getCardBackground());
                g2d.fill(roundedRectangle);
                g2d.setColor(themeManager.getBorder());
                g2d.draw(roundedRectangle);
            }
        };
        inputContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        aiInputArea = new JTextArea(4, 50);
        aiInputArea.setFont(new Font("Arial", Font.PLAIN, 14));
        aiInputArea.setLineWrap(true);
        aiInputArea.setWrapStyleWord(true);
        aiInputArea.setText("Describe your trading strategy... (e.g., 'Create a momentum strategy for tech stocks with 20% annual returns')");
        aiInputArea.setForeground(themeManager.getSecondaryText());
        
        // Add focus listeners for placeholder behavior
        aiInputArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (aiInputArea.getText().startsWith("Describe your trading strategy...")) {
                    aiInputArea.setText("");
                    aiInputArea.setForeground(themeManager.getText());
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (aiInputArea.getText().isEmpty()) {
                    aiInputArea.setText("Describe your trading strategy... (e.g., 'Create a momentum strategy for tech stocks with 20% annual returns')");
                    aiInputArea.setForeground(themeManager.getSecondaryText());
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(aiInputArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        JButton sendButton = new JButton("→");
        sendButton.setFont(new Font("Arial", Font.BOLD, 20));
        sendButton.setPreferredSize(new Dimension(50, 50));
        sendButton.setBackground(themeManager.getPrimary());
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.addActionListener(e -> handleAiInput());
        
        JPanel inputRow = new JPanel(new BorderLayout());
        inputRow.add(scrollPane, BorderLayout.CENTER);
        inputRow.add(sendButton, BorderLayout.EAST);
        
        inputContainer.add(inputRow, BorderLayout.CENTER);
        
        // Quick prompts
        JPanel promptsPanel = new JPanel(new FlowLayout());
        String[] prompts = {
            "Momentum strategy for tech stocks",
            "Low-risk dividend portfolio", 
            "Crypto swing trading strategy",
            "Market volatility hedge"
        };
        
        for (String prompt : prompts) {
            JButton promptButton = new JButton(prompt);
            promptButton.setFont(new Font("Arial", Font.PLAIN, 12));
            promptButton.setBackground(themeManager.getCardBackground());
            promptButton.setForeground(themeManager.getSecondaryText());
            promptButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBorder()),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            promptButton.addActionListener(e -> {
                aiInputArea.setText(prompt);
                aiInputArea.setForeground(themeManager.getText());
                handleAiInput();
            });
            promptsPanel.add(promptButton);
        }
        
        inputContainer.add(promptsPanel, BorderLayout.SOUTH);
        inputPanel.add(inputContainer, BorderLayout.CENTER);
        
        // Feature preview
        JLabel disclaimerLabel = new JLabel("AI assistant provides general information only, not financial advice.");
        disclaimerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        disclaimerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel featuresLabel = new JLabel("✨ AI Strategy Builder    📊 Backtesting    📈 Performance Analytics    🔄 Portfolio Optimization");
        featuresLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        featuresLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        aiSection.add(titleLabel);
        aiSection.add(Box.createVerticalStrut(10));
        aiSection.add(subtitleLabel);
        aiSection.add(inputPanel);
        aiSection.add(Box.createVerticalStrut(20));
        aiSection.add(disclaimerLabel);
        aiSection.add(Box.createVerticalStrut(10));
        aiSection.add(featuresLabel);
        
        return aiSection;
    }
    
    private JPanel createFeaturesSection() {
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
        
        JLabel titleLabel = new JLabel("Professional-Grade Trading Tools");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        
        // Feature cards
        String[][] features = {
            {"📊", "Advanced Analytics", "Gain insights with our powerful analytics tools. Monitor market trends and visualize your portfolio performance in real-time."},
            {"💰", "Smart Trading", "Execute trades with confidence using our intelligent trading platform enhanced by AI-driven recommendations."},
            {"💡", "AI Assistant", "Get personalized trading suggestions from our AI assistant, helping you make informed decisions based on market data."}
        };
        
        for (String[] feature : features) {
            cardsPanel.add(createFeatureCard(feature[0], feature[1], feature[2]));
        }
        
        featuresPanel.add(titleLabel);
        featuresPanel.add(cardsPanel);
        
        return featuresPanel;
    }
    
    private JPanel createFeatureCard(String icon, String title, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        card.setBackground(themeManager.getCardBackground());
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(descArea);
        
        return card;
    }
    
    private JPanel createPricingSection() {
        JPanel pricingPanel = new JPanel();
        pricingPanel.setLayout(new BoxLayout(pricingPanel, BoxLayout.Y_AXIS));
        pricingPanel.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
        
        JLabel titleLabel = new JLabel("Choose Your Plan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        
        // Pricing cards
        cardsPanel.add(createPricingCard("Free", "$0", new String[]{
            "✓ Basic charting tools",
            "✓ Limited market data",
            "✗ Advanced AI insights",
            "✗ Custom strategies"
        }, false, "Get Started"));
        
        cardsPanel.add(createPricingCard("Pro", "$49", new String[]{
            "✓ Advanced AI insights", 
            "✓ Real-time market data",
            "✓ Custom strategies",
            "✓ Portfolio optimization"
        }, true, "Upgrade to Pro"));
        
        cardsPanel.add(createPricingCard("Enterprise", "Custom", new String[]{
            "✓ Custom solutions",
            "✓ Dedicated support", 
            "✓ API access",
            "✓ White-label options"
        }, false, "Contact Sales"));
        
        pricingPanel.add(titleLabel);
        pricingPanel.add(cardsPanel);
        
        return pricingPanel;
    }
    
    private JPanel createPricingCard(String planName, String price, String[] features, boolean highlighted, String buttonText) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        card.setBackground(themeManager.getCardBackground());
        
        if (highlighted) {
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getPrimary(), 2),
                BorderFactory.createEmptyBorder(28, 18, 28, 18)
            ));
        }
        
        JLabel planLabel = new JLabel(planName);
        planLabel.setFont(new Font("Arial", Font.BOLD, 24));
        planLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel priceLabel = new JLabel(price);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 36));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        for (String feature : features) {
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            featureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            featuresPanel.add(featureLabel);
            featuresPanel.add(Box.createVerticalStrut(8));
        }
        
        JButton actionButton = createStyledButton(buttonText, 
            highlighted ? themeManager.getPrimary() : themeManager.getCardBackground(),
            highlighted ? Color.WHITE : themeManager.getText());
        actionButton.addActionListener(e -> {
            if (buttonText.equals("Get Started") || buttonText.equals("Upgrade to Pro")) {
                showRegisterDialog();
            } else if (buttonText.equals("Contact Sales")) {
                JOptionPane.showMessageDialog(this, "Contact sales at sales@aitrader.com", "Contact Sales", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        card.add(planLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(priceLabel);
        card.add(featuresPanel);
        card.add(actionButton);
        
        return card;
    }
    
    private JPanel createChartPreview() {
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw a simple chart preview
                int width = getWidth() - 40;
                int height = getHeight() - 40;
                int x = 20;
                int y = 20;
                
                // Background
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(x, y, width, height, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.fill(roundedRectangle);
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.draw(roundedRectangle);
                
                // Chart lines
                g2d.setColor(themeManager.getPrimary());
                g2d.setStroke(new BasicStroke(3));
                
                int[] xPoints = new int[10];
                int[] yPoints = new int[10];
                
                for (int i = 0; i < 10; i++) {
                    xPoints[i] = x + 20 + (i * (width - 40) / 9);
                    yPoints[i] = y + 20 + (int)(Math.random() * (height - 40));
                }
                
                for (int i = 0; i < 9; i++) {
                    g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
                }
                
                // Chart title
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString("AAPL - Live Chart", x + 20, y + 40);
            }
        };
        chartPanel.setOpaque(false);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        
        return chartPanel;
    }
    
    private JButton createStyledButton(String text, Color background) {
        return createStyledButton(text, background, Color.WHITE);
    }
    
    private JButton createStyledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (background.equals(themeManager.getPrimary())) {
                    button.setBackground(themeManager.getPrimaryHover());
                } else if (background.equals(themeManager.getAccent())) {
                    button.setBackground(themeManager.getAccent().darker());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(background);
            }
        });
        
        return button;
    }
    
    private void handleAiInput() {
        String input = aiInputArea.getText().trim();
        if (!input.isEmpty() && !input.startsWith("Describe your trading strategy...")) {
            // Enable guest mode and navigate to trading
            authManager.loginAsGuest();
            navigateTo("Trading");
        }
    }
    
    private void showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog(parentFrame, themeManager);
        loginDialog.setVisible(true);
        
        if (loginDialog.isLoginSuccessful()) {
            authManager.login(loginDialog.getUserEmail(), "password");
            refreshPage();
        }
    }
    
    private void showRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog(parentFrame, themeManager);
        registerDialog.setVisible(true);
        
        if (registerDialog.isRegistrationSuccessful()) {
            authManager.register(
                registerDialog.getUserName(),
                registerDialog.getUserEmail(),
                "password",
                registerDialog.getUserRole()
            );
            refreshPage();
        }
    }
    
    private void navigateTo(String page) {
        // This would be implemented by the main frame
        if (parentFrame instanceof TradingPlatformMain) {
            ((TradingPlatformMain) parentFrame).navigateTo(page);
        }
    }
    
    private void refreshPage() {
        removeAll();
        initializeComponents();
        applyTheme();
        revalidate();
        repaint();
    }
    
    public void applyTheme() {
        setBackground(themeManager.getBackground());
        applyThemeToComponent(this);
        repaint();
    }
    
    private void applyThemeToComponent(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel) {
                component.setForeground(themeManager.getText());
            } else if (component instanceof JTextArea) {
                JTextArea textArea = (JTextArea) component;
                if (!textArea.getText().startsWith("Describe your trading strategy...")) {
                    textArea.setForeground(themeManager.getText());
                }
                textArea.setBackground(themeManager.getCardBackground());
            } else if (component instanceof Container) {
                component.setBackground(themeManager.getBackground());
                applyThemeToComponent((Container) component);
            }
        }
    }
}