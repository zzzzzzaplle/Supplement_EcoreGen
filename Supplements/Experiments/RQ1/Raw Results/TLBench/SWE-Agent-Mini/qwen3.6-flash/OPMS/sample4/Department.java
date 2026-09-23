import java.util.ArrayList;
import java.util.List;

public class Department {
    private String ID;
    private String email;
    private List<Employee> employees;
    private List<Project> projects;

    public Department() {
        this.employees = new ArrayList<Employee>();
        this.projects = new ArrayList<Project>();
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

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public void addEmployee(Employee employee) {
        this.employees.add(employee);
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public void addProject(Project project) {
        this.projects.add(project);
    }

    public double calculateAverageBudget() {
        if (projects.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Project project : projects) {
            total += project.getBudget();
        }
        return total / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> result = new ArrayList<FundingGroupType>();
        for (Project project : projects) {
            if (project instanceof CommunityProject) {
                FundingGroup fg = ((CommunityProject) project).getFundingGroup();
                if (fg != null) {
                    result.add(fg.getType());
                }
            }
        }
        return result;
    }
}
