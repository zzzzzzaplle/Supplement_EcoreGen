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
        double total = 0.0;
        for (Project p : projects) {
            total += p.getBudget();
        }
        return total / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> types = new ArrayList<FundingGroupType>();
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
        if (employees == null) {
            employees = new ArrayList<Employee>();
        }
        employees.add(employee);
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public void addProject(Project project) {
        if (projects == null) {
            projects = new ArrayList<Project>();
        }
        projects.add(project);
    }
}
