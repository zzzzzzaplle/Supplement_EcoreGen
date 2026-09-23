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
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public void addRole(UserRole role) {
        if (roles == null) {
            roles = new ArrayList<UserRole>();
        }
        roles.add(role);
    }
}
