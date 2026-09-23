import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

enum EmployeeType {
    TEMPORARY,
    PERMANENT
}

enum FundingGroupType {
    PRIVATE,
    GOVERNMENT,
    MIXED
}

class FundingGroup {
    private String name;
    private FundingGroupType type;

    public FundingGroup() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public FundingGroupType getType() { return type; }
    public void setType(FundingGroupType type) { this.type = type; }
}

class Employee {
    private EmployeeType type;
    private String name;
    private String email;
    private String ID;
    private String number;

    public Employee() {}

    // Getters and Setters
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

    // Getters and Setters
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

    public ProductionProject() {}

    // Getters and Setters
    public String getSiteCode() { return siteCode; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }
}

class ResearchProject extends Project {
    public ResearchProject() {}
}

class EducationProject extends Project {
    private FundingGroup fundingGroup;

    public EducationProject() {}

    // Getters and Setters
    public FundingGroup getFundingGroup() { return fundingGroup; }
    public void setFundingGroup(FundingGroup fundingGroup) { this.fundingGroup = fundingGroup; }
}

class CommunityProject extends Project {
    private FundingGroup fundingGroup;

    public CommunityProject() {}

    // Getters and Setters
    public FundingGroup getFundingGroup() { return fundingGroup; }
    public void setFundingGroup(FundingGroup fundingGroup) { this.fundingGroup = fundingGroup; }
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

    // Key operations
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

    // Getters and Setters
    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
    public void addEmployee(Employee employee) {
        if (this.employees == null) {
            this.employees = new ArrayList<>();
        }
        this.employees.add(employee);
    }

    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }
    public void addProject(Project project) {
        if (this.projects == null) {
            this.projects = new ArrayList<>();
        }
        this.projects.add(project);
    }
}

class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<>();
    }

    // Key operations
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
        Set<String> uniqueEmployeeIDs = new HashSet<>();
        if (departments == null) return 0;
        for (Department dept : departments) {
            if (dept.getProjects() != null) {
                for (Project p : dept.getProjects()) {
                    if (p instanceof ProductionProject) {
                        if (p.getWorkingEmployees() != null) {
                            for (Employee emp : p.getWorkingEmployees()) {
                                if (emp != null && emp.getID() != null) {
                                    uniqueEmployeeIDs.add(emp.getID());
                                }
                            }
                        }
                    }
                }
            }
        }
        return uniqueEmployeeIDs.size();
    }

    public void addDepartment(Department department) {
        if (department == null) return;
        // Ensure unique name (ID) constraint
        if (departments != null) {
            for (Department d : departments) {
                if (d.getID() != null && d.getID().equals(department.getID())) {
                    return; // duplicate name, reject
                }
            }
        }
        if (departments == null) {
            departments = new ArrayList<>();
        }
        if (departments.size() < 8) {
            departments.add(department);
        }
    }

    public void removeDepartment(String name) {
        if (name == null || departments == null) return;
        if (departments.size() > 2) {
            departments.removeIf(d -> name.equals(d.getID()));
        }
    }

    // Getters and Setters
    public List<Department> getDepartments() { return departments; }
    public void setDepartments(List<Department> departments) { this.departments = departments; }
}