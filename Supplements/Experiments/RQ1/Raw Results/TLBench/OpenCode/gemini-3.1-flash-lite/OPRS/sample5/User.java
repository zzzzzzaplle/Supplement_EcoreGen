public class User {
    private String name;
    private java.util.List<UserRole> roles = new java.util.ArrayList<>();

    public User() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public java.util.List<UserRole> getRoles() { return roles; }
    public void addRole(UserRole role) { this.roles.add(role); }
}
