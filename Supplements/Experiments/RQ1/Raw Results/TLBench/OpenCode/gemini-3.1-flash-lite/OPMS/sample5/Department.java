import java.util.ArrayList;
import java.util.List;

public class Department {
    private String ID;
    private String email;
    private List<Employee> employees;
    private List<Project> projects;

    public Department() {
        this.employees = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Employee> getEmployees() { return employees; }
    public void addEmployee(Employee employee) { this.employees.add(employee); }
    public List<Project> getProjects() { return projects; }
    public void addProject(Project project) { this.projects.add(project); }

    public double calculateAverageBudget() {
        if (projects.isEmpty()) return 0;
        double total = 0;
        for (Project p : projects) total += p.getBudget();
        return total / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> types = new ArrayList<>();
        for (Project p : projects) {
            if (p instanceof CommunityProject) {
                types.add(((CommunityProject) p).getFundingGroup().getType());
            }
        }
        return types;
    }
}
