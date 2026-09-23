import java.util.List;

public class User {
    private String name;
    private List<UserRole> roles;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void addRole(UserRole role) {
        if (this.roles == null) {
            this.roles = new java.util.ArrayList<>();
        }
        this.roles.add(role);
    }
}
