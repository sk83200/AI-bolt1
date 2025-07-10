import java.util.prefs.Preferences;

public class AuthManager {
    private User currentUser;
    private Preferences prefs;
    private boolean isAuthenticated;
    
    public AuthManager() {
        prefs = Preferences.userNodeForPackage(AuthManager.class);
        loadStoredUser();
    }
    
    private void loadStoredUser() {
        String storedEmail = prefs.get("userEmail", null);
        String storedName = prefs.get("userName", null);
        String storedRole = prefs.get("userRole", null);
        boolean storedPro = prefs.getBoolean("userPro", false);
        
        if (storedEmail != null && storedName != null) {
            currentUser = new User("1", storedName, storedEmail, storedRole, storedPro);
            isAuthenticated = true;
        }
    }
    
    public boolean login(String email, String password) {
        // Simulate login validation
        if (email.equals("demo@aitrader.com") && password.equals("demo123")) {
            currentUser = new User("1", "Demo User", email, "Trader", false);
            isAuthenticated = true;
            storeUser();
            return true;
        } else if (email.contains("@") && password.length() >= 6) {
            // Accept any valid-looking email/password for demo
            String name = email.substring(0, email.indexOf("@"));
            currentUser = new User("1", name, email, "Trader", false);
            isAuthenticated = true;
            storeUser();
            return true;
        }
        return false;
    }
    
    public boolean register(String name, String email, String password, String role) {
        // Simulate registration
        if (name.length() > 0 && email.contains("@") && password.length() >= 8) {
            currentUser = new User("1", name, email, role, false);
            isAuthenticated = true;
            storeUser();
            return true;
        }
        return false;
    }
    
    public void loginAsGuest() {
        currentUser = User.createGuestUser();
        isAuthenticated = true;
        // Don't store guest user
    }
    
    public void logout() {
        currentUser = null;
        isAuthenticated = false;
        clearStoredUser();
    }
    
    public void upgradeToPro() {
        if (currentUser != null) {
            currentUser.setPro(true);
            storeUser();
        }
    }
    
    private void storeUser() {
        if (currentUser != null && !currentUser.isGuest()) {
            prefs.put("userEmail", currentUser.getEmail());
            prefs.put("userName", currentUser.getName());
            prefs.put("userRole", currentUser.getRole());
            prefs.putBoolean("userPro", currentUser.isPro());
        }
    }
    
    private void clearStoredUser() {
        prefs.remove("userEmail");
        prefs.remove("userName");
        prefs.remove("userRole");
        prefs.remove("userPro");
    }
    
    public boolean isAuthenticated() {
        return isAuthenticated && currentUser != null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isProUser() {
        return currentUser != null && currentUser.isPro();
    }
    
    public boolean isGuestMode() {
        return currentUser != null && currentUser.isGuest();
    }
}