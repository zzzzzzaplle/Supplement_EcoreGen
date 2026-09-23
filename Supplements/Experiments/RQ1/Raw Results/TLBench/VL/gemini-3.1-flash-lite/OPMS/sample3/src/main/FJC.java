import java.util.*;
import java.util.stream.Collectors;

enum EmployeeType {
    TEMPORARY, PERMANENT
}

enum FundingGroupType {
    PRIVATE, GOVERNMENT, MIXED
}

class FundingGroup {
    private String name;
    private FundingGroupType type;

    public FundingGroup() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public FundingGroupType getType() { return type; }
    public void setType(FundingGroupType type) { this.type = type; }
}

class Employee {
    private EmployeeType type;
    private String name;
    private String email;
    private String id;
    private String number;

    public Employee() {}

    public EmployeeType getType() { return type; }
    public void setType(EmployeeType type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getID() { return id; }
    public void setID(String id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
}

abstract class Project {
    private String title;
    private String description;
    private double budget;
    private Date deadline;
    private List<Employee> workingEmployees = new ArrayList<>();

    public Project() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
    public List<Employee> getWorkingEmployees() { return workingEmployees; }
    public void addWorkingEmployee(Employee employee) { this.workingEmployees.add(employee); }
}

class ProductionProject extends Project {
    private String siteCode;

    public ProductionProject() {}

    public String getSiteCode() { return siteCode; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }
}

class ResearchProject extends Project {
    public ResearchProject() {}
}

class EducationProject extends Project {
    private FundingGroup fundingGroup;

    public EducationProject() {}

    public FundingGroup getFundingGroup() { return fundingGroup; }
    public void setFundingGroup(FundingGroup fundingGroup) { this.fundingGroup = fundingGroup; }
}

class CommunityProject extends Project {
    private FundingGroup fundingGroup;

    public CommunityProject() {}

    public FundingGroup getFundingGroup() { return fundingGroup; }
    public void setFundingGroup(FundingGroup fundingGroup) { this.fundingGroup = fundingGroup; }
}

class Department {
    private String id;
    private String email;
    private List<Employee> employees = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();

    public Department() {}

    public String getID() { return id; }
    public void setID(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Employee> getEmployees() { return employees; }
    public void addEmployee(Employee employee) { this.employees.add(employee); }
    public List<Project> getProjects() { return projects; }
    public void addProject(Project project) { this.projects.add(project); }

    public double calculateAverageBudget() {
        return projects.stream().mapToDouble(Project::getBudget).average().orElse(0.0);
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        return projects.stream()
                .filter(p -> p instanceof CommunityProject)
                .map(p -> ((CommunityProject) p).getFundingGroup().getType())
                .collect(Collectors.toList());
    }
}

class Company {
    private List<Department> departments = new ArrayList<>();

    public Company() {}

    public List<Department> getDepartments() { return departments; }
    public void setDepartments(List<Department> departments) { this.departments = departments; }

    public double calculateTotalBudget() {
        return departments.stream()
                .flatMap(d -> d.getProjects().stream())
                .mapToDouble(Project::getBudget)
                .sum();
    }

    public int countEmployeesInProductionProjects() {
        return (int) departments.stream()
                .flatMap(d -> d.getProjects().stream())
                .filter(p -> p instanceof ProductionProject)
                .flatMap(p -> p.getWorkingEmployees().stream())
                .distinct()
                .count();
    }

    public void addDepartment(Department department) {
        if (departments.size() < 8) {
            departments.add(department);
        }
    }

    public void removeDepartment(String id) {
        if (departments.size() > 2) {
            departments.removeIf(d -> d.getID().equals(id));
        }
    }
}