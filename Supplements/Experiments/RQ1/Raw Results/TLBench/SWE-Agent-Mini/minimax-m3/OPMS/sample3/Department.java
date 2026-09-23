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
        if (projects == null || projects.isEmpty()) return 0.0;
        double sum = 0.0;
        int count = 0;
        for (Project p : projects) {
            if (p == null) continue;
            sum += p.getBudget();
            count++;
        }
        if (count == 0) return 0.0;
        return sum / count;
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> types = new ArrayList<FundingGroupType>();
        if (projects == null) return types;
        for (Project p : projects) {
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

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public void addEmployee(Employee employee) {
        if (employee == null) return;
        if (this.employees == null) this.employees = new ArrayList<Employee>();
        this.employees.add(employee);
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public void addProject(Project project) {
        if (project == null) return;
        if (this.projects == null) this.projects = new ArrayList<Project>();
        this.projects.add(project);
    }
}
