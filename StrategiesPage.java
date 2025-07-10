import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StrategiesPage extends JPanel {
    private ThemeManager themeManager;
    private AuthManager authManager;
    private JFrame parentFrame;
    private JSplitPane mainSplitPane;
    private JSplitPane rightSplitPane;
    private AiAssistantPanel aiAssistantPanel;
    private StrategyEditorPanel strategyEditorPanel;
    private MessagePanel messagePanel;
    private OutputPanel outputPanel;
    
    public StrategiesPage(JFrame parentFrame, ThemeManager themeManager, AuthManager authManager) {
        this.parentFrame = parentFrame;
        this.themeManager = themeManager;
        this.authManager = authManager;
        
        setLayout(new BorderLayout());
        initializeComponents();
        setupLayout();
        applyTheme();
    }
    
    private void initializeComponents() {
        // Create panels
        aiAssistantPanel = new AiAssistantPanel(themeManager, authManager);
        strategyEditorPanel = new StrategyEditorPanel(themeManager, authManager);
        messagePanel = new MessagePanel(themeManager, authManager);
        outputPanel = new OutputPanel(themeManager, authManager);
        
        // Set up communication between panels
        aiAssistantPanel.setStrategyUpdateListener(strategyEditorPanel::updateStrategy);
        strategyEditorPanel.setCodeGenerationListener(outputPanel::updateCode);
        strategyEditorPanel.setMessageListener(messagePanel::addMessage);
    }
    
    private void setupLayout() {
        if (authManager.isAuthenticated() && !authManager.isGuestMode()) {
            // Full authenticated user layout: GUI Editor (60%) | Messages/Output (40%)
            rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            rightSplitPane.setTopComponent(messagePanel);
            rightSplitPane.setBottomComponent(outputPanel);
            rightSplitPane.setDividerLocation(200);
            rightSplitPane.setResizeWeight(0.4);
            
            mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            mainSplitPane.setLeftComponent(strategyEditorPanel);
            mainSplitPane.setRightComponent(rightSplitPane);
            mainSplitPane.setDividerLocation(800);
            mainSplitPane.setResizeWeight(0.6);
            
            add(mainSplitPane, BorderLayout.CENTER);
        } else {
            // Guest mode layout: AI Assistant (30%) | GUI Editor (45%) | Messages/Output (25%)
            rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            rightSplitPane.setTopComponent(messagePanel);
            rightSplitPane.setBottomComponent(outputPanel);
            rightSplitPane.setDividerLocation(150);
            rightSplitPane.setResizeWeight(0.5);
            
            JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            centerSplitPane.setLeftComponent(strategyEditorPanel);
            centerSplitPane.setRightComponent(rightSplitPane);
            centerSplitPane.setDividerLocation(600);
            centerSplitPane.setResizeWeight(0.65);
            
            mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            mainSplitPane.setLeftComponent(aiAssistantPanel);
            mainSplitPane.setRightComponent(centerSplitPane);
            mainSplitPane.setDividerLocation(350);
            mainSplitPane.setResizeWeight(0.3);
            
            add(mainSplitPane, BorderLayout.CENTER);
        }
        
        // Style split panes
        styleSplitPane(mainSplitPane);
        styleSplitPane(rightSplitPane);
    }
    
    private void styleSplitPane(JSplitPane splitPane) {
        splitPane.setBackground(themeManager.getBackground());
        splitPane.setBorder(null);
        splitPane.setDividerSize(4);
        splitPane.setContinuousLayout(true);
    }
    
    public void applyTheme() {
        setBackground(themeManager.getBackground());
        
        if (aiAssistantPanel != null) aiAssistantPanel.applyTheme();
        if (strategyEditorPanel != null) strategyEditorPanel.applyTheme();
        if (messagePanel != null) messagePanel.applyTheme();
        if (outputPanel != null) outputPanel.applyTheme();
        
        repaint();
    }
}