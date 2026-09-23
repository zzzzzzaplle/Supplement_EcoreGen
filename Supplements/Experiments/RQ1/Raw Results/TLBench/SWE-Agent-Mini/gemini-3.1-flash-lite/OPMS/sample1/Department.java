import java.util.ArrayList;
import java.util.List;

public class Department {
    private String ID;
    private String email;
    private List<Employee> employees = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();

    public Department() {}

    public double calculateAverageBudget() {
        if (projects.isEmpty()) return 0.0;
        double sum = 0.0;
        for (Project p : projects) {
            sum += p.getBudget();
        }
        return sum / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> types = new ArrayList<>();
        for (Project p : projects) {
            if (p instanceof CommunityProject) {
                FundingGroup fg = ((CommunityProject) p).getFundingGroup();
                if (fg != null) {
                    types.add(fg.getType());
                }
            }
        }
        return types;
    }

    public String getID() { return ID; }
    public void setID(String id) { this.ID = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Employee> getEmployees() { return employees; }
    public void addEmployee(Employee employee) { this.employees.add(employee); }
    public List<Project> getProjects() { return projects; }
    public void addProject(Project project) { this.projects.add(project); }
}
