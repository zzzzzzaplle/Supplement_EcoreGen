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
        if (projects == null || projects.size() == 0) {
            return 0.0;
        }
        double total = 0.0;
        for (Project project : projects) {
            if (project != null) {
                total += project.getBudget();
            }
        }
        return total / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> result = new ArrayList<FundingGroupType>();
        if (projects != null) {
            for (Project project : projects) {
                if (project instanceof CommunityProject) {
                    CommunityProject communityProject = (CommunityProject) project;
                    if (communityProject.getFundingGroup() != null) {
                        result.add(communityProject.getFundingGroup().getType());
                    }
                }
            }
        }
        return result;
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
        if (employees == null) {
            employees = new ArrayList<Employee>();
        }
        if (employee != null) {
            employees.add(employee);
        }
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void addProject(Project project) {
        if (projects == null) {
            projects = new ArrayList<Project>();
        }
        if (project != null) {
            projects.add(project);
        }
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
