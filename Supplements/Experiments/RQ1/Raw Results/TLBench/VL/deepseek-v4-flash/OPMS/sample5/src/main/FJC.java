import java.util.ArrayList;
import java.util.Date;
import java.util.List;

enum EmployeeType {
    TEMPORARY,
    PERMANENT
}

enum FundingGroupType {
    PRIVATE,
    GOVERNMENT,
    MIXED
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

abstract class Project {
    private String title;
    private String description;
    private double budget;
    private Date deadline;
    private List<Employee> workingEmployees;

    public Project() {
        this.workingEmployees = new ArrayList<>();
    }

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
        if (this.workingEmployees == null) {
            this.workingEmployees = new ArrayList<>();
        }
        this.workingEmployees.add(employee);
    }
}

class ProductionProject extends Project {
    private String siteCode;

    public ProductionProject() {
        super();
    }

    public String getSiteCode() { return siteCode; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }
}

class ResearchProject extends Project {
    public ResearchProject() {
        super();
    }
}

class EducationProject extends Project {
    private FundingGroup fundingGroup;

    public EducationProject() {
        super();
    }

    public FundingGroup getFundingGroup() { return fundingGroup; }
    public void setFundingGroup(FundingGroup fundingGroup) { this.fundingGroup = fundingGroup; }
}

class CommunityProject extends Project {
    private FundingGroup fundingGroup;

    public CommunityProject() {
        super();
    }

    public FundingGroup getFundingGroup() { return fundingGroup; }
    public void setFundingGroup(FundingGroup fundingGroup) { this.fundingGroup = fundingGroup; }
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

class Department {
    private String ID;
    private String email;
    private List<Employee> employees;
    private List<Project> projects;

    public Department() {
        this.employees = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }

    public void addEmployee(Employee employee) {
        if (this.employees == null) {
            this.employees = new ArrayList<>();
        }
        this.employees.add(employee);
    }

    public void addProject(Project project) {
        if (this.projects == null) {
            this.projects = new ArrayList<>();
        }
        this.projects.add(project);
    }

    public double calculateAverageBudget() {
        if (projects == null || projects.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Project p : projects) {
            sum += p.getBudget();
        }
        return sum / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> result = new ArrayList<>();
        if (projects == null) return result;
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
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<>();
    }

    public List<Department> getDepartments() { return departments; }
    public void setDepartments(List<Department> departments) { this.departments = departments; }

    public double calculateTotalBudget() {
        double total = 0.0;
        if (departments == null) return total;
        for (Department dept : departments) {
            if (dept.getProjects() != null) {
                for (Project p : dept.getProjects()) {
                    total += p.getBudget();
                }
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        if (departments == null) return 0;
        List<Employee> uniqueEmployees = new ArrayList<>();
        for (Department dept : departments) {
            if (dept.getProjects() != null) {
                for (Project p : dept.getProjects()) {
                    if (p instanceof ProductionProject) {
                        if (p.getWorkingEmployees() != null) {
                            for (Employee e : p.getWorkingEmployees()) {
                                if (!uniqueEmployees.contains(e)) {
                                    uniqueEmployees.add(e);
                                }
                            }
                        }
                    }
                }
            }
        }
        return uniqueEmployees.size();
    }

    public void addDepartment(Department department) {
        if (departments == null) {
            departments = new ArrayList<>();
        }
        if (departments.size() >= 8) {
            throw new IllegalStateException("Cannot add department: company already has 8 departments (maximum).");
        }
        // Check unique name (using ID as name identifier)
        if (department.getID() != null) {
            for (Department d : departments) {
                if (department.getID().equals(d.getID())) {
                    throw new IllegalArgumentException("Department with ID '" + department.getID() + "' already exists.");
                }
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments == null || departments.isEmpty()) {
            throw new IllegalStateException("Cannot remove department: company has no departments.");
        }
        if (departments.size() <= 2) {
            throw new IllegalStateException("Cannot remove department: company must have at least 2 departments.");
        }
        Department toRemove = null;
        for (Department d : departments) {
            if (d.getID() != null && d.getID().equals(name)) {
                toRemove = d;
                break;
            }
        }
        if (toRemove == null) {
            throw new IllegalArgumentException("Department with ID '" + name + "' not found.");
        }
        departments.remove(toRemove);
    }
}