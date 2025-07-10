import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

public class OutputPanel extends JPanel {
    private ThemeManager themeManager;
    private AuthManager authManager;
    private JTabbedPane tabbedPane;
    private JTextArea jsonArea;
    private JTextArea pythonArea;
    private JTextArea javaArea;
    private JButton copyButton;
    private String currentCode = "";
    
    public OutputPanel(ThemeManager themeManager, AuthManager authManager) {
        this.themeManager = themeManager;
        this.authManager = authManager;
        
        setLayout(new BorderLayout());
        initializeComponents();
        setupLayout();
        loadSampleCode();
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        jsonArea = new JTextArea();
        pythonArea = new JTextArea();
        javaArea = new JTextArea();
        
        // Configure text areas
        configureTextArea(jsonArea);
        configureTextArea(pythonArea);
        configureTextArea(javaArea);
        
        copyButton = new JButton("Copy");
        copyButton.setFont(new Font("Arial", Font.PLAIN, 11));
        copyButton.addActionListener(e -> copyCurrentCode());
        
        if (authManager.isGuestMode()) {
            copyButton.setEnabled(false);
            copyButton.setBackground(Color.GRAY);
        }
    }
    
    private void configureTextArea(JTextArea textArea) {
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospace", Font.PLAIN, 12));
        textArea.setBackground(new Color(40, 44, 52)); // Dark background
        textArea.setForeground(new Color(171, 178, 191)); // Light text
        textArea.setTabSize(2);
    }
    
    private void setupLayout() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        JLabel titleLabel = new JLabel("Strategy Output");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.add(copyButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Tabs
        tabbedPane.addTab("JSON", new JScrollPane(jsonArea));
        tabbedPane.addTab("Python", new JScrollPane(pythonArea));
        tabbedPane.addTab("Java", new JScrollPane(javaArea));
        
        // Guest mode overlay
        if (authManager.isGuestMode()) {
            JPanel overlayPanel = new JPanel();
            overlayPanel.setLayout(new OverlayLayout(overlayPanel));
            
            // Semi-transparent overlay
            JPanel grayOverlay = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(new Color(128, 128, 128, 100));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            grayOverlay.setOpaque(false);
            
            overlayPanel.add(grayOverlay);
            overlayPanel.add(tabbedPane);
            
            // Guest banner
            JPanel guestBanner = new JPanel(new BorderLayout());
            guestBanner.setBackground(new Color(255, 243, 205));
            guestBanner.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            
            JLabel warningLabel = new JLabel("⚠ Guest Mode - Code is read-only");
            warningLabel.setFont(new Font("Arial", Font.BOLD, 12));
            warningLabel.setForeground(new Color(146, 64, 14));
            
            JLabel infoLabel = new JLabel("Sign up to edit and export code");
            infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            infoLabel.setForeground(new Color(146, 64, 14));
            
            JButton signUpButton = new JButton("Sign Up Free");
            signUpButton.setBackground(new Color(146, 64, 14));
            signUpButton.setForeground(Color.WHITE);
            signUpButton.setBorderPainted(false);
            signUpButton.setFont(new Font("Arial", Font.BOLD, 11));
            signUpButton.addActionListener(e -> showRegisterDialog());
            
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            textPanel.add(warningLabel);
            textPanel.add(infoLabel);
            
            guestBanner.add(textPanel, BorderLayout.WEST);
            guestBanner.add(signUpButton, BorderLayout.EAST);
            
            add(headerPanel, BorderLayout.NORTH);
            add(overlayPanel, BorderLayout.CENTER);
            add(guestBanner, BorderLayout.SOUTH);
        } else {
            add(headerPanel, BorderLayout.NORTH);
            add(tabbedPane, BorderLayout.CENTER);
        }
    }
    
    private void loadSampleCode() {
        // Sample JSON
        String sampleJson = "{\n" +
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
                "}";
        
        // Sample Python
        String samplePython = "# Sample Trading Strategy - Python Implementation\n" +
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
                "        self.rsi = Indicator.RSI(period=14)\n" +
                "        self.sma_20 = Indicator.SMA(period=20)\n" +
                "        self.volume_avg = Indicator.SMA(period=20, field='volume')\n" +
                "        \n" +
                "    def entry_conditions(self, data):\n" +
                "        return (\n" +
                "            data['rsi'] > 70 and\n" +
                "            data['close'] > data['sma_20'] and\n" +
                "            data['volume'] > data['volume_avg'] * 1.5\n" +
                "        )\n" +
                "        \n" +
                "    def exit_conditions(self, data, position):\n" +
                "        profit_target = 5.0\n" +
                "        stop_loss = 2.0\n" +
                "        \n" +
                "        return (\n" +
                "            position.unrealized_pnl_pct >= profit_target or\n" +
                "            position.unrealized_pnl_pct <= -stop_loss\n" +
                "        )\n" +
                "        \n" +
                "    def position_size(self, data):\n" +
                "        max_risk = 2.0\n" +
                "        return self.calculate_position_size(max_risk)\n\n" +
                "# Sign up to generate custom strategies!";
        
        // Sample Java
        String sampleJava = "// Sample Trading Strategy - Java Implementation\n" +
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
                "        double profitTarget = 5.0;\n" +
                "        double stopLoss = 2.0;\n" +
                "        \n" +
                "        return position.getUnrealizedPnlPct() >= profitTarget ||\n" +
                "               position.getUnrealizedPnlPct() <= -stopLoss;\n" +
                "    }\n" +
                "    \n" +
                "    @Override\n" +
                "    public double positionSize(MarketData data) {\n" +
                "        double maxRisk = 2.0;\n" +
                "        return calculatePositionSize(maxRisk);\n" +
                "    }\n" +
                "}\n\n" +
                "// Sign up to generate custom strategies!";
        
        jsonArea.setText(sampleJson);
        pythonArea.setText(samplePython);
        javaArea.setText(sampleJava);
        
        currentCode = sampleJava; // Default to Java
    }
    
    public void updateCode(String newCode) {
        if (authManager.isGuestMode()) return;
        
        this.currentCode = newCode;
        
        // Update Java tab with generated code
        javaArea.setText(newCode);
        
        // Update JSON representation
        updateJsonFromCode();
    }
    
    private void updateJsonFromCode() {
        // Simple JSON generation based on current code
        String json = "{\n" +
                "  \"strategy\": {\n" +
                "    \"name\": \"Generated Strategy\",\n" +
                "    \"type\": \"momentum\",\n" +
                "    \"language\": \"java\",\n" +
                "    \"generated\": true\n" +
                "  }\n" +
                "}";
        jsonArea.setText(json);
    }
    
    private void copyCurrentCode() {
        if (authManager.isGuestMode()) return;
        
        String codeToCopy = "";
        int selectedTab = tabbedPane.getSelectedIndex();
        
        switch (selectedTab) {
            case 0: // JSON
                codeToCopy = jsonArea.getText();
                break;
            case 1: // Python
                codeToCopy = pythonArea.getText();
                break;
            case 2: // Java
                codeToCopy = javaArea.getText();
                break;
        }
        
        if (!codeToCopy.isEmpty()) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(codeToCopy);
            clipboard.setContents(selection, null);
            
            JOptionPane.showMessageDialog(this, "Code copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog((JFrame) SwingUtilities.getWindowAncestor(this), themeManager);
        registerDialog.setVisible(true);
    }
    
    public void applyTheme() {
        setBackground(themeManager.getCardBackground());
        tabbedPane.setBackground(themeManager.getCardBackground());
        copyButton.setBackground(themeManager.getCardBackground());
        copyButton.setForeground(themeManager.getText());
        
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
}