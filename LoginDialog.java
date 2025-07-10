import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox rememberMeCheckBox;
    private boolean loginSuccessful = false;
    private ThemeManager themeManager;
    private String userEmail;
    private String userName;

    public LoginDialog(JFrame parent, ThemeManager themeManager) {
        super(parent, "Sign In - AI Trader", true);
        this.themeManager = themeManager;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        applyTheme();
        
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        rememberMeCheckBox = new JCheckBox("Remember me");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Sign in to your AI Trader account");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;

        // Email
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Email Address:"), gbc);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(passwordField, gbc);

        // Remember me and forgot password
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.add(rememberMeCheckBox, BorderLayout.WEST);
        
        JButton forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        optionsPanel.add(forgotPasswordButton, BorderLayout.EAST);
        
        formPanel.add(optionsPanel, gbc);

        // Demo credentials info
        gbc.gridy = 5;
        gbc.insets = new Insets(15, 0, 8, 0);
        JPanel demoPanel = new JPanel();
        demoPanel.setLayout(new BoxLayout(demoPanel, BoxLayout.Y_AXIS));
        demoPanel.setBorder(BorderFactory.createTitledBorder("Demo Credentials"));
        
        JLabel demoLabel1 = new JLabel("Email: demo@aitrader.com");
        JLabel demoLabel2 = new JLabel("Password: demo123");
        demoLabel1.setFont(new Font("Monospace", Font.PLAIN, 12));
        demoLabel2.setFont(new Font("Monospace", Font.PLAIN, 12));
        
        demoPanel.add(demoLabel1);
        demoPanel.add(demoLabel2);
        formPanel.add(demoPanel, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton loginButton = new JButton("Sign In");
        loginButton.setPreferredSize(new Dimension(120, 35));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
        
        JLabel registerLabel = new JLabel("Don't have an account?");
        registerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        registerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton registerLinkButton = new JButton("Create Account");
        registerLinkButton.setBorderPainted(false);
        registerLinkButton.setContentAreaFilled(false);
        registerLinkButton.setFont(new Font("Arial", Font.BOLD, 12));
        registerLinkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLinkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        footerPanel.add(registerLabel);
        footerPanel.add(registerLinkButton);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(footerPanel, BorderLayout.PAGE_END);

        // Event handlers
        loginButton.addActionListener(e -> handleLogin());
        cancelButton.addActionListener(e -> dispose());
        registerLinkButton.addActionListener(e -> {
            dispose();
            new RegisterDialog((JFrame) getParent(), themeManager).setVisible(true);
        });
        forgotPasswordButton.addActionListener(e -> handleForgotPassword());
    }

    private void setupEventHandlers() {
        // Enter key handling
        KeyStroke enterKey = KeyStroke.getKeyStroke("ENTER");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, "login");
        getRootPane().getActionMap().put("login", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return;
        }

        // Simulate login process
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Signing in...");
        progressBar.setStringPainted(true);

        JDialog progressDialog = new JDialog(this, "Signing In", true);
        progressDialog.add(progressBar);
        progressDialog.setSize(300, 80);
        progressDialog.setLocationRelativeTo(this);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Thread.sleep(1500); // Simulate network delay
                
                // Demo login validation
                if (email.equals("demo@aitrader.com") && password.equals("demo123")) {
                    userEmail = email;
                    userName = "Demo User";
                    return true;
                } else if (email.contains("@") && password.length() >= 6) {
                    // Accept any valid-looking email/password for demo
                    userEmail = email;
                    userName = email.substring(0, email.indexOf("@"));
                    return true;
                }
                return false;
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    if (get()) {
                        loginSuccessful = true;
                        showSuccess("Welcome back! Login successful.");
                        dispose();
                    } else {
                        showError("Invalid email or password. Please try again.");
                    }
                } catch (Exception e) {
                    showError("Login failed. Please check your connection and try again.");
                }
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }

    private void handleForgotPassword() {
        String email = JOptionPane.showInputDialog(
            this,
            "Enter your email address to reset your password:",
            "Forgot Password",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (email != null && !email.trim().isEmpty()) {
            if (email.contains("@") && email.contains(".")) {
                JOptionPane.showMessageDialog(
                    this,
                    "Password reset instructions have been sent to " + email,
                    "Password Reset",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                showError("Please enter a valid email address.");
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void applyTheme() {
        // Apply theme colors
        getContentPane().setBackground(themeManager.getBackground());
        
        // Apply theme to all components
        applyThemeToComponent(getContentPane());
    }

    private void applyThemeToComponent(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel) {
                component.setForeground(themeManager.getText());
            } else if (component instanceof JTextField || component instanceof JPasswordField) {
                component.setBackground(themeManager.getCardBackground());
                component.setForeground(themeManager.getText());
                ((JComponent) component).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(themeManager.getBorder()),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            } else if (component instanceof JCheckBox) {
                component.setBackground(themeManager.getBackground());
                component.setForeground(themeManager.getText());
            } else if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getText().equals("Sign In")) {
                    button.setBackground(themeManager.getPrimary());
                    button.setForeground(Color.WHITE);
                } else if (button.getText().equals("Create Account") || button.getText().equals("Forgot Password?")) {
                    button.setForeground(themeManager.getPrimary());
                } else {
                    button.setBackground(themeManager.getCardBackground());
                    button.setForeground(themeManager.getText());
                }
                button.setBorderPainted(false);
                button.setFocusPainted(false);
            } else if (component instanceof Container) {
                component.setBackground(themeManager.getBackground());
                applyThemeToComponent((Container) component);
            }
        }
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isRememberMeSelected() {
        return rememberMeCheckBox.isSelected();
    }
}