import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        ID = id;
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
        if (projects.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Project project : projects) {
            sum += project.getBudget();
        }
        return sum / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> types = new ArrayList<>();
        for (Project project : projects) {
            if (project instanceof CommunityProject) {
                CommunityProject cp = (CommunityProject) project;
                if (cp.getFundingGroup() != null) {
                    types.add(cp.getFundingGroup().getType());
                }
            }
        }
        return types;
    }
}
