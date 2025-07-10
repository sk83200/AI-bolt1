import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ForecastsPage extends JPanel {
    private ThemeManager themeManager;
    private AuthManager authManager;
    private JFrame parentFrame;
    private JTextField symbolField;
    private JSpinner lookbackSpinner;
    private JSpinner horizonSpinner;
    private JComboBox<String> modelComboBox;
    private JCheckBox aiAssistCheckBox;
    private JButton runForecastButton;
    private JTextArea resultsArea;
    private JPanel chartPanel;
    
    public ForecastsPage(JFrame parentFrame, ThemeManager themeManager, AuthManager authManager) {
        this.parentFrame = parentFrame;
        this.themeManager = themeManager;
        this.authManager = authManager;
        
        setLayout(new BorderLayout());
        initializeComponents();
        setupLayout();
        applyTheme();
    }
    
    private void initializeComponents() {
        symbolField = new JTextField("AAPL", 10);
        lookbackSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        horizonSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
        
        String[] models = {"ARIMA", "LSTM", "Prophet", "Ensemble"};
        modelComboBox = new JComboBox<>(models);
        modelComboBox.setSelectedItem("Ensemble");
        
        aiAssistCheckBox = new JCheckBox("Let AI recommend best model configuration");
        
        runForecastButton = new JButton("Run Forecast");
        runForecastButton.addActionListener(e -> runForecast());
        
        resultsArea = new JTextArea(10, 40);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospace", Font.PLAIN, 12));
        
        chartPanel = createChartPanel();
        
        // Disable for guest mode
        if (authManager.isGuestMode()) {
            runForecastButton.setText("Run Forecast (Sign up required)");
            runForecastButton.setEnabled(false);
            runForecastButton.setBackground(Color.GRAY);
            
            symbolField.setEditable(false);
            lookbackSpinner.setEnabled(false);
            horizonSpinner.setEnabled(false);
            modelComboBox.setEnabled(false);
            aiAssistCheckBox.setEnabled(false);
            
            // Show sample results
            showSampleResults();
        }
    }
    
    private void setupLayout() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Market Forecast");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel subtitleLabel = new JLabel("AI-powered price prediction and market analysis");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Input form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form card
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(BorderFactory.createTitledBorder("Forecast Configuration"));
        formCard.setBackground(themeManager.getCardBackground());
        
        // Input grid
        JPanel inputGrid = new JPanel(new GridBagLayout());
        inputGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Symbol
        gbc.gridx = 0; gbc.gridy = 0;
        inputGrid.add(new JLabel("Symbol:"), gbc);
        gbc.gridx = 1;
        inputGrid.add(symbolField, gbc);
        
        // Lookback Period
        gbc.gridx = 2; gbc.gridy = 0;
        inputGrid.add(new JLabel("Lookback Period (days):"), gbc);
        gbc.gridx = 3;
        inputGrid.add(lookbackSpinner, gbc);
        
        // Forecast Horizon
        gbc.gridx = 0; gbc.gridy = 1;
        inputGrid.add(new JLabel("Forecast Horizon (days):"), gbc);
        gbc.gridx = 1;
        inputGrid.add(horizonSpinner, gbc);
        
        // Model Type
        gbc.gridx = 2; gbc.gridy = 1;
        inputGrid.add(new JLabel("Model Type:"), gbc);
        gbc.gridx = 3;
        inputGrid.add(modelComboBox, gbc);
        
        // AI Assistance
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        inputGrid.add(aiAssistCheckBox, gbc);
        
        // Run button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(runForecastButton);
        inputGrid.add(buttonPanel, gbc);
        
        formCard.add(inputGrid);
        formPanel.add(formCard);
        
        // Results section
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Chart and metrics
        JPanel chartAndMetrics = new JPanel(new BorderLayout());
        
        // Chart
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBorder(BorderFactory.createTitledBorder("Forecast Chart"));
        chartContainer.setBackground(themeManager.getCardBackground());
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        
        // Metrics
        JPanel metricsPanel = createMetricsPanel();
        
        chartAndMetrics.add(chartContainer, BorderLayout.CENTER);
        chartAndMetrics.add(metricsPanel, BorderLayout.EAST);
        
        // Results text
        JPanel resultsTextPanel = new JPanel(new BorderLayout());
        resultsTextPanel.setBorder(BorderFactory.createTitledBorder("Forecast Summary"));
        resultsTextPanel.setBackground(themeManager.getCardBackground());
        
        JScrollPane resultsScrollPane = new JScrollPane(resultsArea);
        resultsTextPanel.add(resultsScrollPane, BorderLayout.CENTER);
        
        resultsPanel.add(chartAndMetrics, BorderLayout.CENTER);
        resultsPanel.add(resultsTextPanel, BorderLayout.SOUTH);
        
        // Guest mode banner
        if (authManager.isGuestMode()) {
            JPanel guestBanner = createGuestBanner();
            add(guestBanner, BorderLayout.NORTH);
            add(formPanel, BorderLayout.CENTER);
            add(resultsPanel, BorderLayout.SOUTH);
        } else {
            add(headerPanel, BorderLayout.NORTH);
            add(formPanel, BorderLayout.CENTER);
            add(resultsPanel, BorderLayout.SOUTH);
        }
    }
    
    private JPanel createGuestBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(254, 243, 199));
        banner.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel infoLabel = new JLabel("ℹ Guest Mode: Viewing sample forecast. Sign up to run custom forecasts with real data.");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(146, 64, 14));
        
        JButton signUpButton = new JButton("Sign Up Free");
        signUpButton.setBackground(themeManager.getPrimary());
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setBorderPainted(false);
        signUpButton.addActionListener(e -> showRegisterDialog());
        
        banner.add(infoLabel, BorderLayout.WEST);
        banner.add(signUpButton, BorderLayout.EAST);
        
        return banner;
    }
    
    private JPanel createChartPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw sample forecast chart
                int width = getWidth() - 40;
                int height = getHeight() - 40;
                int x = 20;
                int y = 20;
                
                // Background
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x, y, width, height);
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawRect(x, y, width, height);
                
                // Historical data (solid line)
                g2d.setColor(themeManager.getPrimary());
                g2d.setStroke(new BasicStroke(2));
                
                int[] historicalX = new int[15];
                int[] historicalY = new int[15];
                
                for (int i = 0; i < 15; i++) {
                    historicalX[i] = x + 20 + (i * (width - 40) / 20);
                    historicalY[i] = y + 20 + (int)(Math.sin(i * 0.3) * 30 + Math.random() * 20 + height/2 - 40);
                }
                
                for (int i = 0; i < 14; i++) {
                    g2d.drawLine(historicalX[i], historicalY[i], historicalX[i + 1], historicalY[i + 1]);
                }
                
                // Forecast data (dashed line)
                g2d.setColor(themeManager.getSuccess());
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
                
                int[] forecastX = new int[6];
                int[] forecastY = new int[6];
                
                forecastX[0] = historicalX[14];
                forecastY[0] = historicalY[14];
                
                for (int i = 1; i < 6; i++) {
                    forecastX[i] = x + 20 + ((14 + i) * (width - 40) / 20);
                    forecastY[i] = forecastY[i-1] + (int)(Math.random() * 20 - 10);
                }
                
                for (int i = 0; i < 5; i++) {
                    g2d.drawLine(forecastX[i], forecastY[i], forecastX[i + 1], forecastY[i + 1]);
                }
                
                // Labels
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.drawString("Historical", x + 20, y + height - 10);
                g2d.setColor(themeManager.getSuccess());
                g2d.drawString("Forecast", x + width - 80, y + height - 10);
            }
        };
        panel.setPreferredSize(new Dimension(400, 200));
        panel.setBackground(Color.WHITE);
        return panel;
    }
    
    private JPanel createMetricsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Forecast Metrics"));
        panel.setBackground(themeManager.getCardBackground());
        panel.setPreferredSize(new Dimension(200, 0));
        
        // Sample metrics
        String[][] metrics = {
            {"Mean Absolute Error", "2.45"},
            {"RMSE", "3.12"},
            {"Hit Rate", "68.5%"},
            {"Confidence", "85%"}
        };
        
        for (String[] metric : metrics) {
            JPanel metricPanel = new JPanel(new BorderLayout());
            metricPanel.setOpaque(false);
            metricPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel nameLabel = new JLabel(metric[0]);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            
            JLabel valueLabel = new JLabel(metric[1]);
            valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
            valueLabel.setForeground(themeManager.getPrimary());
            
            metricPanel.add(nameLabel, BorderLayout.NORTH);
            metricPanel.add(valueLabel, BorderLayout.CENTER);
            
            panel.add(metricPanel);
        }
        
        return panel;
    }
    
    private void runForecast() {
        if (authManager.isGuestMode()) {
            showUpgradeDialog();
            return;
        }
        
        runForecastButton.setEnabled(false);
        runForecastButton.setText("Running Forecast...");
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                Thread.sleep(3000); // Simulate forecast computation
                return generateForecastResults();
            }
            
            @Override
            protected void done() {
                try {
                    String results = get();
                    resultsArea.setText(results);
                    chartPanel.repaint(); // Update chart with new data
                } catch (Exception e) {
                    resultsArea.setText("Forecast failed: " + e.getMessage());
                } finally {
                    runForecastButton.setEnabled(true);
                    runForecastButton.setText("Run Forecast");
                }
            }
        };
        worker.execute();
    }
    
    private void showSampleResults() {
        String sampleResults = "SAMPLE FORECAST RESULTS (Guest Mode)\n" +
                "=====================================\n\n" +
                "Symbol: AAPL\n" +
                "Model: Ensemble (ARIMA + LSTM + Prophet)\n" +
                "Forecast Period: 5 days\n" +
                "Confidence Level: 85%\n\n" +
                "PRICE PREDICTIONS:\n" +
                "Day 1: $198.45 (±$2.15)\n" +
                "Day 2: $199.20 (±$2.30)\n" +
                "Day 3: $200.10 (±$2.45)\n" +
                "Day 4: $199.85 (±$2.60)\n" +
                "Day 5: $201.25 (±$2.75)\n\n" +
                "TREND ANALYSIS:\n" +
                "• Bullish momentum expected\n" +
                "• Support level: $196.50\n" +
                "• Resistance level: $203.00\n" +
                "• Volatility: Moderate\n\n" +
                "RISK FACTORS:\n" +
                "• Earnings announcement in 2 weeks\n" +
                "• Market correlation: 0.75 with SPY\n" +
                "• Technical indicators: Mixed signals\n\n" +
                "Sign up for real-time forecasts with live data!";
        
        resultsArea.setText(sampleResults);
    }
    
    private String generateForecastResults() {
        String symbol = symbolField.getText().toUpperCase();
        int lookback = (Integer) lookbackSpinner.getValue();
        int horizon = (Integer) horizonSpinner.getValue();
        String model = (String) modelComboBox.getSelectedItem();
        
        return String.format(
            "FORECAST RESULTS\n" +
            "================\n\n" +
            "Symbol: %s\n" +
            "Model: %s\n" +
            "Lookback Period: %d days\n" +
            "Forecast Horizon: %d days\n" +
            "Generated: %s\n\n" +
            "PREDICTIONS:\n" +
            "Expected trend: Bullish\n" +
            "Price target: $%.2f\n" +
            "Confidence: %.1f%%\n\n" +
            "METRICS:\n" +
            "MAE: %.2f\n" +
            "RMSE: %.2f\n" +
            "Hit Rate: %.1f%%\n\n" +
            "RECOMMENDATION:\n" +
            "Based on the forecast model, %s shows positive momentum\n" +
            "with moderate volatility expected over the next %d days.",
            symbol, model, lookback, horizon, 
            new java.util.Date().toString(),
            195.50 + Math.random() * 10,
            80 + Math.random() * 15,
            2.1 + Math.random(),
            3.2 + Math.random(),
            65 + Math.random() * 20,
            symbol, horizon
        );
    }
    
    private void showUpgradeDialog() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Forecasting requires a full account. Would you like to sign up?",
            "Upgrade Required",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            showRegisterDialog();
        }
    }
    
    private void showRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog(parentFrame, themeManager);
        registerDialog.setVisible(true);
    }
    
    public void applyTheme() {
        setBackground(themeManager.getBackground());
        resultsArea.setBackground(themeManager.getCardBackground());
        resultsArea.setForeground(themeManager.getText());
        
        if (!authManager.isGuestMode()) {
            runForecastButton.setBackground(themeManager.getPrimary());
            runForecastButton.setForeground(Color.WHITE);
        }
        
        applyThemeToComponent(this);
        repaint();
    }
    
    private void applyThemeToComponent(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel) {
                component.setForeground(themeManager.getText());
            } else if (component instanceof JTextField) {
                component.setBackground(themeManager.getCardBackground());
                component.setForeground(themeManager.getText());
            } else if (component instanceof Container) {
                component.setBackground(themeManager.getBackground());
                applyThemeToComponent((Container) component);
            }
        }
    }
}