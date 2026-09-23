import java.util.ArrayList;
import java.util.List;

public class Department {
    private String id;
    private String email;
    private List<Employee> employees = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();

    public Department() {}

    public double calculateAverageBudget() {
        if (projects.isEmpty()) return 0.0;
        double total = 0.0;
        for (Project p : projects) {
            total += p.getBudget();
        }
        return total / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> types = new ArrayList<>();
        for (Project p : projects) {
            if (p instanceof CommunityProject) {
                CommunityProject cp = (CommunityProject) p;
                if (cp.getFundingGroup() != null) {
                    types.add(cp.getFundingGroup().getType());
                }
            }
        }
        return types;
    }

    public String getID() { return id; }
    public void setID(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Employee> getEmployees() { return employees; }
    public void addEmployee(Employee employee) { this.employees.add(employee); }
    public List<Project> getProjects() { return projects; }
    public void addProject(Project project) { this.projects.add(project); }
}
