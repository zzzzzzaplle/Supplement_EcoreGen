import java.util.List;
import java.util.ArrayList;

public class Department {
    private String ID;
    private String email;
    private List<Employee> employees;
    private List<Project> projects;

    public Department() {
        this.employees = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public Department(String ID, String email) {
        this.ID = ID;
        this.email = email;
        this.employees = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public double calculateAverageBudget() {
        if (projects.isEmpty()) {
            return 0.0;
        }
        double totalBudget = 0.0;
        for (Project project : projects) {
            totalBudget += project.getBudget();
        }
        return totalBudget / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> fundingGroupTypes = new ArrayList<>();
        for (Project project : projects) {
            if (project instanceof CommunityProject) {
                CommunityProject communityProject = (CommunityProject) project;
                FundingGroup fundingGroup = communityProject.getFundingGroup();
                if (fundingGroup != null) {
                    fundingGroupTypes.add(fundingGroup.getType());
                }
            }
        }
        return fundingGroupTypes;
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
}
