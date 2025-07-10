import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterDialog extends JDialog {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private boolean registrationSuccessful = false;
    private ThemeManager themeManager;

    public RegisterDialog(JFrame parent, ThemeManager themeManager) {
        super(parent, "Create Account - AI Trader", true);
        this.themeManager = themeManager;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        applyTheme();
        
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        
        String[] roles = {"Trader", "Analyst", "Portfolio Manager"};
        roleComboBox = new JComboBox<>(roles);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Join thousands of traders using AI-powered insights");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;

        // Full Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(nameField, gbc);

        // Email
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Email Address:"), gbc);
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(emailField, gbc);

        // Role
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("I am a:"), gbc);
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(roleComboBox, gbc);

        // Password
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(confirmPasswordField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton registerButton = new JButton("Create Account");
        registerButton.setPreferredSize(new Dimension(150, 35));
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
        
        JLabel loginLabel = new JLabel("Already have an account?");
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton loginLinkButton = new JButton("Sign In");
        loginLinkButton.setBorderPainted(false);
        loginLinkButton.setContentAreaFilled(false);
        loginLinkButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginLinkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLinkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        footerPanel.add(loginLabel);
        footerPanel.add(loginLinkButton);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(footerPanel, BorderLayout.PAGE_END);

        // Event handlers
        registerButton.addActionListener(e -> handleRegistration());
        cancelButton.addActionListener(e -> dispose());
        loginLinkButton.addActionListener(e -> {
            dispose();
            new LoginDialog((JFrame) getParent(), themeManager).setVisible(true);
        });
    }

    private void setupEventHandlers() {
        // Enter key handling
        KeyStroke enterKey = KeyStroke.getKeyStroke("ENTER");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, "register");
        getRootPane().getActionMap().put("register", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });
    }

    private void handleRegistration() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return;
        }

        if (password.length() < 8) {
            showError("Password must be at least 8 characters long.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        // Simulate registration process
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Creating account...");
        progressBar.setStringPainted(true);

        JDialog progressDialog = new JDialog(this, "Creating Account", true);
        progressDialog.add(progressBar);
        progressDialog.setSize(300, 80);
        progressDialog.setLocationRelativeTo(this);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Thread.sleep(2000); // Simulate network delay
                return true; // Simulate successful registration
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    if (get()) {
                        registrationSuccessful = true;
                        showSuccess("Account created successfully! Welcome to AI Trader.");
                        dispose();
                    } else {
                        showError("Registration failed. Please try again.");
                    }
                } catch (Exception e) {
                    showError("Registration failed. Please try again.");
                }
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Registration Error", JOptionPane.ERROR_MESSAGE);
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
            } else if (component instanceof JComboBox) {
                component.setBackground(themeManager.getCardBackground());
                component.setForeground(themeManager.getText());
            } else if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getText().equals("Create Account")) {
                    button.setBackground(themeManager.getPrimary());
                    button.setForeground(Color.WHITE);
                } else if (button.getText().equals("Sign In")) {
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

    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }

    public String getUserName() {
        return nameField.getText().trim();
    }

    public String getUserEmail() {
        return emailField.getText().trim();
    }

    public String getUserRole() {
        return (String) roleComboBox.getSelectedItem();
    }
}