import java.util.ArrayList;
import java.util.List;

public class Department {
    private String iD;
    private String email;
    private List<Employee> employees;
    private List<Project> projects;

    public Department() {
        this.employees = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public String getID() {
        return iD;
    }

    public void setID(String id) {
        this.iD = id;
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
        if (projects == null || projects.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Project project : projects) {
            total += project.getBudget();
        }
        return total / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> result = new ArrayList<>();
        if (projects == null) {
            return result;
        }
        for (Project project : projects) {
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
