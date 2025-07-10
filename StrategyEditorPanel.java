import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StrategyEditorPanel extends JPanel {
    private ThemeManager themeManager;
    private AuthManager authManager;
    private StrategyData strategyData;
    private Consumer<String> codeGenerationListener;
    private Consumer<String> messageListener;
    
    // Form components
    private JTextField nameField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> assetClassComboBox;
    private JComboBox<String> timeframeComboBox;
    private JList<String> indicatorsList;
    private JTextArea entryConditionsArea;
    private JSpinner profitTargetSpinner;
    private JSpinner stopLossSpinner;
    private JCheckBox trailingStopCheckBox;
    private JSpinner maxRiskSpinner;
    private JSpinner maxPositionsSpinner;
    private JButton saveButton;
    private JButton backtestButton;
    private JButton resetButton;
    
    public StrategyEditorPanel(ThemeManager themeManager, AuthManager authManager) {
        this.themeManager = themeManager;
        this.authManager = authManager;
        this.strategyData = new StrategyData();
        
        setLayout(new BorderLayout());
        initializeComponents();
        setupLayout();
        loadSampleData();
    }
    
    private void initializeComponents() {
        nameField = new JTextField(20);
        
        String[] types = {"Momentum", "Mean Reversion", "Trend Following", "Breakout"};
        typeComboBox = new JComboBox<>(types);
        
        String[] assetClasses = {"Stocks", "Cryptocurrency", "Forex", "Commodities"};
        assetClassComboBox = new JComboBox<>(assetClasses);
        
        String[] timeframes = {"1 Minute", "5 Minutes", "15 Minutes", "1 Hour", "4 Hours", "1 Day"};
        timeframeComboBox = new JComboBox<>(timeframes);
        
        String[] indicators = {"RSI", "MACD", "Simple MA", "Exponential MA", "Bollinger Bands", "ADX"};
        indicatorsList = new JList<>(indicators);
        indicatorsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        entryConditionsArea = new JTextArea(4, 30);
        entryConditionsArea.setLineWrap(true);
        entryConditionsArea.setWrapStyleWord(true);
        
        profitTargetSpinner = new JSpinner(new SpinnerNumberModel(5.0, 0.0, 100.0, 0.1));
        stopLossSpinner = new JSpinner(new SpinnerNumberModel(2.0, 0.0, 50.0, 0.1));
        trailingStopCheckBox = new JCheckBox("Use trailing stop");
        
        maxRiskSpinner = new JSpinner(new SpinnerNumberModel(2.0, 0.0, 10.0, 0.1));
        maxPositionsSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));
        
        saveButton = new JButton("Save Strategy");
        backtestButton = new JButton("Run Backtest");
        resetButton = new JButton("Reset");
        
        // Add action listeners
        saveButton.addActionListener(e -> saveStrategy());
        backtestButton.addActionListener(e -> runBacktest());
        resetButton.addActionListener(e -> resetForm());
        
        // Add change listeners for code generation
        nameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { generateCode(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { generateCode(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { generateCode(); }
        });
        
        typeComboBox.addActionListener(e -> generateCode());
        profitTargetSpinner.addChangeListener(e -> generateCode());
        stopLossSpinner.addChangeListener(e -> generateCode());
    }
    
    private void setupLayout() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Strategy Builder");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel statusLabel = new JLabel(authManager.isGuestMode() ? "Read-only (Guest)" : "");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(themeManager.getWarning());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        // Form content
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Basic Information
        formPanel.add(createSectionPanel("Basic Information", createBasicInfoPanel()));
        formPanel.add(Box.createVerticalStrut(15));
        
        // Entry Rules
        formPanel.add(createSectionPanel("Entry Rules", createEntryRulesPanel()));
        formPanel.add(Box.createVerticalStrut(15));
        
        // Exit Rules
        formPanel.add(createSectionPanel("Exit Rules", createExitRulesPanel()));
        formPanel.add(Box.createVerticalStrut(15));
        
        // Risk Management
        formPanel.add(createSectionPanel("Risk Management", createRiskManagementPanel()));
        formPanel.add(Box.createVerticalStrut(15));
        
        // Action Buttons
        formPanel.add(createButtonPanel());
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createSectionPanel(String title, JPanel content) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(BorderFactory.createTitledBorder(title));
        section.setBackground(themeManager.getCardBackground());
        section.add(content, BorderLayout.CENTER);
        return section;
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Strategy Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Strategy Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);
        
        // Strategy Type
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Strategy Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(typeComboBox, gbc);
        
        // Asset Class
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Asset Class:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(assetClassComboBox, gbc);
        
        // Timeframe
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Timeframe:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(timeframeComboBox, gbc);
        
        return panel;
    }
    
    private JPanel createEntryRulesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Indicators
        JPanel indicatorsPanel = new JPanel(new BorderLayout());
        indicatorsPanel.add(new JLabel("Technical Indicators:"), BorderLayout.NORTH);
        JScrollPane indicatorsScrollPane = new JScrollPane(indicatorsList);
        indicatorsScrollPane.setPreferredSize(new Dimension(200, 80));
        indicatorsPanel.add(indicatorsScrollPane, BorderLayout.CENTER);
        
        // Entry Conditions
        JPanel conditionsPanel = new JPanel(new BorderLayout());
        conditionsPanel.add(new JLabel("Entry Conditions:"), BorderLayout.NORTH);
        JScrollPane conditionsScrollPane = new JScrollPane(entryConditionsArea);
        conditionsPanel.add(conditionsScrollPane, BorderLayout.CENTER);
        
        panel.add(indicatorsPanel, BorderLayout.WEST);
        panel.add(conditionsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createExitRulesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Profit Target
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Profit Target (%):"), gbc);
        gbc.gridx = 1;
        panel.add(profitTargetSpinner, gbc);
        
        // Stop Loss
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Stop Loss (%):"), gbc);
        gbc.gridx = 1;
        panel.add(stopLossSpinner, gbc);
        
        // Trailing Stop
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(trailingStopCheckBox, gbc);
        
        return panel;
    }
    
    private JPanel createRiskManagementPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Max Risk Per Trade
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Max Risk Per Trade (%):"), gbc);
        gbc.gridx = 1;
        panel.add(maxRiskSpinner, gbc);
        
        // Max Positions
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Max Positions:"), gbc);
        gbc.gridx = 1;
        panel.add(maxPositionsSpinner, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setOpaque(false);
        
        // Style buttons based on guest mode
        if (authManager.isGuestMode()) {
            saveButton.setText("Save (Sign up required)");
            backtestButton.setText("Backtest (Sign up required)");
            
            saveButton.setEnabled(false);
            backtestButton.setEnabled(false);
            resetButton.setEnabled(false);
            
            saveButton.setBackground(Color.GRAY);
            backtestButton.setBackground(Color.GRAY);
            resetButton.setBackground(Color.GRAY);
        } else {
            saveButton.setBackground(themeManager.getPrimary());
            saveButton.setForeground(Color.WHITE);
            backtestButton.setBackground(themeManager.getCardBackground());
            resetButton.setBackground(themeManager.getCardBackground());
        }
        
        panel.add(saveButton);
        panel.add(backtestButton);
        panel.add(resetButton);
        
        return panel;
    }
    
    private void loadSampleData() {
        if (authManager.isGuestMode()) {
            nameField.setText("Sample Momentum Strategy");
            typeComboBox.setSelectedItem("Momentum");
            assetClassComboBox.setSelectedItem("Stocks");
            timeframeComboBox.setSelectedItem("1 Day");
            
            indicatorsList.setSelectedIndices(new int[]{0, 2, 4}); // RSI, Simple MA, Bollinger Bands
            entryConditionsArea.setText("RSI > 70\nPrice > SMA20\nVolume > 1.5x average");
            
            profitTargetSpinner.setValue(5.0);
            stopLossSpinner.setValue(2.0);
            maxRiskSpinner.setValue(2.0);
            maxPositionsSpinner.setValue(5);
            
            // Disable editing for guest mode
            setComponentsEnabled(false);
        }
        
        generateCode();
    }
    
    private void setComponentsEnabled(boolean enabled) {
        nameField.setEditable(enabled);
        typeComboBox.setEnabled(enabled);
        assetClassComboBox.setEnabled(enabled);
        timeframeComboBox.setEnabled(enabled);
        indicatorsList.setEnabled(enabled);
        entryConditionsArea.setEditable(enabled);
        profitTargetSpinner.setEnabled(enabled);
        stopLossSpinner.setEnabled(enabled);
        trailingStopCheckBox.setEnabled(enabled);
        maxRiskSpinner.setEnabled(enabled);
        maxPositionsSpinner.setEnabled(enabled);
        
        if (!enabled) {
            // Add gray overlay effect for guest mode
            nameField.setBackground(new Color(240, 240, 240));
            entryConditionsArea.setBackground(new Color(240, 240, 240));
        }
    }
    
    private void saveStrategy() {
        if (authManager.isGuestMode()) {
            showUpgradeDialog();
            return;
        }
        
        updateStrategyData();
        if (messageListener != null) {
            messageListener.accept("Strategy saved successfully: " + strategyData.getName());
        }
        JOptionPane.showMessageDialog(this, "Strategy saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void runBacktest() {
        if (authManager.isGuestMode()) {
            showUpgradeDialog();
            return;
        }
        
        updateStrategyData();
        if (messageListener != null) {
            messageListener.accept("Backtest started for: " + strategyData.getName());
        }
        
        // Simulate backtest
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                Thread.sleep(3000); // Simulate backtest time
                return "Backtest completed. Annual return: 15.2%, Sharpe ratio: 1.4, Max drawdown: -8.5%";
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    if (messageListener != null) {
                        messageListener.accept(result);
                    }
                    JOptionPane.showMessageDialog(StrategyEditorPanel.this, result, "Backtest Results", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    if (messageListener != null) {
                        messageListener.accept("Backtest failed: " + e.getMessage());
                    }
                }
            }
        };
        worker.execute();
    }
    
    private void resetForm() {
        if (authManager.isGuestMode()) {
            showUpgradeDialog();
            return;
        }
        
        nameField.setText("");
        typeComboBox.setSelectedIndex(0);
        assetClassComboBox.setSelectedIndex(0);
        timeframeComboBox.setSelectedIndex(0);
        indicatorsList.clearSelection();
        entryConditionsArea.setText("");
        profitTargetSpinner.setValue(5.0);
        stopLossSpinner.setValue(2.0);
        trailingStopCheckBox.setSelected(false);
        maxRiskSpinner.setValue(2.0);
        maxPositionsSpinner.setValue(5);
        
        generateCode();
    }
    
    private void updateStrategyData() {
        strategyData.setName(nameField.getText());
        strategyData.setType((String) typeComboBox.getSelectedItem());
        strategyData.setAssetClass((String) assetClassComboBox.getSelectedItem());
        strategyData.setTimeframe((String) timeframeComboBox.getSelectedItem());
        strategyData.setProfitTarget((Double) profitTargetSpinner.getValue());
        strategyData.setStopLoss((Double) stopLossSpinner.getValue());
        strategyData.setMaxRisk((Double) maxRiskSpinner.getValue());
        strategyData.setMaxPositions((Integer) maxPositionsSpinner.getValue());
    }
    
    private void generateCode() {
        if (codeGenerationListener != null) {
            updateStrategyData();
            String code = generateStrategyCode();
            codeGenerationListener.accept(code);
        }
    }
    
    private String generateStrategyCode() {
        if (authManager.isGuestMode()) {
            return "// Guest Mode - Sign up to generate custom code\n" +
                   "// Sample strategy code would appear here for authenticated users\n\n" +
                   "public class SampleMomentumStrategy {\n" +
                   "    // Strategy implementation\n" +
                   "    // Entry: RSI > 70, Price > SMA20\n" +
                   "    // Exit: 5% profit target, 2% stop loss\n" +
                   "    // Risk: 2% per trade, max 5 positions\n" +
                   "}";
        }
        
        StringBuilder code = new StringBuilder();
        code.append("// Generated Trading Strategy: ").append(strategyData.getName()).append("\n\n");
        code.append("import java.util.*;\n");
        code.append("import trading.framework.*;\n\n");
        
        String className = strategyData.getName().replaceAll("\\s+", "");
        if (className.isEmpty()) className = "CustomStrategy";
        
        code.append("public class ").append(className).append(" extends Strategy {\n\n");
        
        code.append("    public ").append(className).append("() {\n");
        code.append("        super();\n");
        code.append("        this.assetClass = \"").append(strategyData.getAssetClass()).append("\";\n");
        code.append("        this.timeframe = \"").append(strategyData.getTimeframe()).append("\";\n");
        code.append("    }\n\n");
        
        code.append("    @Override\n");
        code.append("    public boolean entryConditions(MarketData data) {\n");
        code.append("        // Entry logic based on ").append(strategyData.getType()).append(" strategy\n");
        code.append("        return data.getRsi() > 70 && data.getPrice() > data.getSma20();\n");
        code.append("    }\n\n");
        
        code.append("    @Override\n");
        code.append("    public boolean exitConditions(MarketData data, Position position) {\n");
        code.append("        double profitTarget = ").append(strategyData.getProfitTarget()).append(";\n");
        code.append("        double stopLoss = ").append(strategyData.getStopLoss()).append(";\n");
        code.append("        \n");
        code.append("        return position.getUnrealizedPnlPct() >= profitTarget ||\n");
        code.append("               position.getUnrealizedPnlPct() <= -stopLoss;\n");
        code.append("    }\n\n");
        
        code.append("    @Override\n");
        code.append("    public double positionSize(MarketData data) {\n");
        code.append("        double maxRisk = ").append(strategyData.getMaxRisk()).append(";\n");
        code.append("        return calculatePositionSize(maxRisk);\n");
        code.append("    }\n");
        code.append("}");
        
        return code.toString();
    }
    
    private void showUpgradeDialog() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "This feature requires a full account. Would you like to sign up?",
            "Upgrade Required",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Show registration dialog
            RegisterDialog registerDialog = new RegisterDialog((JFrame) SwingUtilities.getWindowAncestor(this), themeManager);
            registerDialog.setVisible(true);
        }
    }
    
    public void updateStrategy(StrategyData newStrategy) {
        if (authManager.isGuestMode()) return;
        
        this.strategyData = newStrategy;
        nameField.setText(newStrategy.getName());
        typeComboBox.setSelectedItem(newStrategy.getType());
        generateCode();
        
        if (messageListener != null) {
            messageListener.accept("AI suggestion applied to strategy");
        }
    }
    
    public void setCodeGenerationListener(Consumer<String> listener) {
        this.codeGenerationListener = listener;
    }
    
    public void setMessageListener(Consumer<String> listener) {
        this.messageListener = listener;
    }
    
    public void applyTheme() {
        setBackground(themeManager.getCardBackground());
        
        // Apply theme to form components
        nameField.setBackground(themeManager.getCardBackground());
        nameField.setForeground(themeManager.getText());
        entryConditionsArea.setBackground(themeManager.getCardBackground());
        entryConditionsArea.setForeground(themeManager.getText());
        
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