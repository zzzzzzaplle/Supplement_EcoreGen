import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private List<UserRole> roles;

    public User() {
        this.roles = new ArrayList<UserRole>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserRole> getRoles() {
        if (roles == null) {
            roles = new ArrayList<UserRole>();
        }
        return roles;
    }

    public void addRole(UserRole role) {
        getRoles().add(role);
    }
}
