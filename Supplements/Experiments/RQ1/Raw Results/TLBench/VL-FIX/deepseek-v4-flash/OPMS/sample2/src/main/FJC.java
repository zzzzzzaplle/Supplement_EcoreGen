import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

enum EmployeeType {
    TEMPORARY, PERMANENT
}

enum FundingGroupType {
    PRIVATE, GOVERNMENT, MIXED
}

class Employee {
    private EmployeeType type;
    private String name;
    private String email;
    private String ID;
    private String number;

    public Employee() {}

    public EmployeeType getType() { return type; }
    public void setType(EmployeeType type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
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
    public void setWorkingEmployees(List<Employee> workingEmployees) { this.workingEmployees = workingEmployees; }
    public void addWorkingEmployee(Employee employee) {
        if (!workingEmployees.contains(employee)) {
            workingEmployees.add(employee);
        }
    }
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
    private String ID;
    private String email;
    private List<Employee> employees = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();

    public Department() {}

    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
    public void addEmployee(Employee employee) {
        if (!employees.contains(employee)) {
            employees.add(employee);
        }
    }
    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }
    public void addProject(Project project) {
        if (!projects.contains(project)) {
            projects.add(project);
        }
    }

    public double calculateAverageBudget() {
        if (projects.isEmpty()) return 0.0;
        double sum = 0.0;
        for (Project p : projects) {
            sum += p.getBudget();
        }
        return sum / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> result = new ArrayList<>();
        for (Project p : projects) {
            if (p instanceof CommunityProject) {
                CommunityProject cp = (CommunityProject) p;
                if (cp.getFundingGroup() != null) {
                    result.add(cp.getFundingGroup().getType());
                }
            }
        }
        return result;
    }
}

class Company {
    private List<Department> departments = new ArrayList<>();

    public Company() {}

    public List<Department> getDepartments() { return departments; }
    public void setDepartments(List<Department> departments) {
        if (departments.size() >= 2 && departments.size() <= 8) {
            this.departments = departments;
        } else {
            throw new IllegalArgumentException("Company must have between 2 and 8 departments.");
        }
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            throw new IllegalStateException("Cannot add department: maximum 8 departments reached.");
        }
        // Check unique name (ID used as name)
        for (Department d : departments) {
            if (d.getID().equals(department.getID())) {
                throw new IllegalArgumentException("Department with ID " + department.getID() + " already exists.");
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            throw new IllegalStateException("Cannot remove department: minimum 2 departments required.");
        }
        boolean removed = departments.removeIf(d -> d.getID().equals(name));
        if (!removed) {
            throw new IllegalArgumentException("Department with ID " + name + " not found.");
        }
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        for (Department dept : departments) {
            for (Project p : dept.getProjects()) {
                total += p.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<Employee> productionEmployees = new HashSet<>();
        for (Department dept : departments) {
            for (Project p : dept.getProjects()) {
                if (p instanceof ProductionProject) {
                    productionEmployees.addAll(p.getWorkingEmployees());
                }
            }
        }
        return productionEmployees.size();
    }
}