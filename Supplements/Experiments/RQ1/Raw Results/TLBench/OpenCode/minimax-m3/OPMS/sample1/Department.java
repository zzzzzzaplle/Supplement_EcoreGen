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
        return this.ID;
    }

    public void setID(String id) {
        this.ID = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Project> getProjects() {
        return this.projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public void addEmployee(Employee employee) {
        if (this.employees == null) {
            this.employees = new ArrayList<Employee>();
        }
        this.employees.add(employee);
    }

    public void addProject(Project project) {
        if (this.projects == null) {
            this.projects = new ArrayList<Project>();
        }
        this.projects.add(project);
    }

    public double calculateAverageBudget() {
        if (this.projects == null || this.projects.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Project p : this.projects) {
            sum += p.getBudget();
        }
        return sum / this.projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> types = new ArrayList<FundingGroupType>();
        if (this.projects == null) {
            return types;
        }
        for (Project p : this.projects) {
            if (p instanceof CommunityProject) {
                CommunityProject cp = (CommunityProject) p;
                FundingGroup fg = cp.getFundingGroup();
                if (fg != null) {
                    types.add(fg.getType());
                }
            }
        }
        return types;
    }
}
