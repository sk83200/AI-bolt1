import java.awt.Color;
import java.awt.Font;
import java.util.prefs.Preferences;

public class ThemeManager {
    private boolean isDarkMode;
    private Preferences prefs;
    
    // Light theme colors
    private static final Color LIGHT_BACKGROUND = new Color(249, 250, 251);
    private static final Color LIGHT_CARD_BACKGROUND = Color.WHITE;
    private static final Color LIGHT_TEXT = new Color(17, 24, 39);
    private static final Color LIGHT_SECONDARY_TEXT = new Color(107, 114, 128);
    private static final Color LIGHT_BORDER = new Color(229, 231, 235);
    
    // Dark theme colors
    private static final Color DARK_BACKGROUND = new Color(17, 24, 39);
    private static final Color DARK_CARD_BACKGROUND = new Color(31, 41, 55);
    private static final Color DARK_TEXT = Color.WHITE;
    private static final Color DARK_SECONDARY_TEXT = new Color(156, 163, 175);
    private static final Color DARK_BORDER = new Color(75, 85, 99);
    
    // Common colors
    private static final Color PRIMARY = new Color(79, 70, 229);
    private static final Color PRIMARY_HOVER = new Color(67, 56, 202);
    private static final Color ACCENT = new Color(245, 158, 11);
    private static final Color SUCCESS = new Color(16, 185, 129);
    private static final Color WARNING = new Color(245, 158, 11);
    private static final Color ERROR = new Color(239, 68, 68);
    
    public ThemeManager() {
        prefs = Preferences.userNodeForPackage(ThemeManager.class);
        isDarkMode = prefs.getBoolean("darkMode", false);
    }
    
    public void toggleTheme() {
        isDarkMode = !isDarkMode;
        prefs.putBoolean("darkMode", isDarkMode);
    }
    
    public boolean isDarkMode() {
        return isDarkMode;
    }
    
    public Color getBackground() {
        return isDarkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }
    
    public Color getCardBackground() {
        return isDarkMode ? DARK_CARD_BACKGROUND : LIGHT_CARD_BACKGROUND;
    }
    
    public Color getText() {
        return isDarkMode ? DARK_TEXT : LIGHT_TEXT;
    }
    
    public Color getSecondaryText() {
        return isDarkMode ? DARK_SECONDARY_TEXT : LIGHT_SECONDARY_TEXT;
    }
    
    public Color getBorder() {
        return isDarkMode ? DARK_BORDER : LIGHT_BORDER;
    }
    
    public Color getPrimary() {
        return PRIMARY;
    }
    
    public Color getPrimaryHover() {
        return PRIMARY_HOVER;
    }
    
    public Color getAccent() {
        return ACCENT;
    }
    
    public Color getSuccess() {
        return SUCCESS;
    }
    
    public Color getWarning() {
        return WARNING;
    }
    
    public Color getError() {
        return ERROR;
    }
    
    public Font getHeaderFont() {
        return new Font("Arial", Font.BOLD, 24);
    }
    
    public Font getSubHeaderFont() {
        return new Font("Arial", Font.BOLD, 18);
    }
    
    public Font getBodyFont() {
        return new Font("Arial", Font.PLAIN, 14);
    }
    
    public Font getSmallFont() {
        return new Font("Arial", Font.PLAIN, 12);
    }
}