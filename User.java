public class User {
    private String id;
    private String name;
    private String email;
    private String role;
    private boolean isPro;
    private boolean isGuest;
    
    public User() {
        this.isGuest = false;
        this.isPro = false;
    }
    
    public User(String id, String name, String email, String role, boolean isPro) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.isPro = isPro;
        this.isGuest = false;
    }
    
    public static User createGuestUser() {
        User guest = new User();
        guest.id = "guest";
        guest.name = "Guest User";
        guest.email = "";
        guest.role = "Guest";
        guest.isPro = false;
        guest.isGuest = true;
        return guest;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public boolean isPro() { return isPro; }
    public void setPro(boolean pro) { isPro = pro; }
    
    public boolean isGuest() { return isGuest; }
    public void setGuest(boolean guest) { isGuest = guest; }
}