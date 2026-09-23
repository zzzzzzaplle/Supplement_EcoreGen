import java.util.ArrayList;
import java.util.List;

public class Department {
    private String ID;
    private String email;
    private List<Employee> employees;
    private List<Project> projects;
    private String name;

    public Department() {
        this.employees = new ArrayList<Employee>();
        this.projects = new ArrayList<Project>();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double calculateAverageBudget() {
        if (projects == null || projects.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Project p : projects) {
            total += p.getBudget();
        }
        return total / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> result = new ArrayList<FundingGroupType>();
        if (projects != null) {
            for (Project p : projects) {
                if (p instanceof CommunityProject) {
                    CommunityProject cp = (CommunityProject) p;
                    FundingGroup fg = cp.getFundingGroup();
                    if (fg != null && fg.getType() != null) {
                        result.add(fg.getType());
                    }
                }
            }
        }
        return result;
    }
}
