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

    public double calculateAverageBudget() {
        if (projects == null || projects.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Project project : projects) {
            sum += project.getBudget();
        }
        return sum / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> types = new ArrayList<FundingGroupType>();
        for (Project project : projects) {
            if (project instanceof CommunityProject) {
                CommunityProject cp = (CommunityProject) project;
                FundingGroup group = cp.getFundingGroup();
                if (group != null && group.getType() != null) {
                    types.add(group.getType());
                }
            }
        }
        return types;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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
        if (this.employees == null) {
            this.employees = new ArrayList<Employee>();
        }
        this.employees.add(employee);
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public void addProject(Project project) {
        if (this.projects == null) {
            this.projects = new ArrayList<Project>();
        }
        this.projects.add(project);
    }
}
