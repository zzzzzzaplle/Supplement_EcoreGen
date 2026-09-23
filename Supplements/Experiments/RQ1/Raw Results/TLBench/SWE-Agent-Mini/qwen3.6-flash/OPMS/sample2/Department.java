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

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        this.ID = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void addEmployee(Employee employee) {
        this.employees.add(employee);
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void addProject(Project project) {
        this.projects.add(project);
    }

    public double calculateAverageBudget() {
        if (this.projects == null || this.projects.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Project project : this.projects) {
            sum += project.getBudget();
        }
        return sum / this.projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> result = new ArrayList<>();
        for (Project project : this.projects) {
            if (project instanceof CommunityProject) {
                CommunityProject cp = (CommunityProject) project;
                if (cp.getFundingGroup() != null) {
                    result.add(cp.getFundingGroup().getType());
                }
            }
        }
        return result;
    }
}
