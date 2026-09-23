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

class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<>();
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        for (Department dept : departments) {
            for (Project proj : dept.getProjects()) {
                total += proj.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<Employee> uniqueEmployees = new HashSet<>();
        for (Department dept : departments) {
            for (Project proj : dept.getProjects()) {
                if (proj instanceof ProductionProject) {
                    uniqueEmployees.addAll(proj.getWorkingEmployees());
                }
            }
        }
        return uniqueEmployees.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() < 8) {
            // Check unique name (using ID as unique name)
            for (Department d : departments) {
                if (d.getID().equals(department.getID())) {
                    throw new IllegalArgumentException("Department with ID " + department.getID() + " already exists.");
                }
            }
            departments.add(department);
        } else {
            throw new IllegalStateException("Cannot add department: maximum 8 departments reached.");
        }
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            throw new IllegalStateException("Cannot remove department: minimum 2 departments required.");
        }
        Department toRemove = null;
        for (Department d : departments) {
            if (d.getID().equals(name)) {
                toRemove = d;
                break;
            }
        }
        if (toRemove == null) {
            throw new IllegalArgumentException("Department with ID " + name + " not found.");
        }
        departments.remove(toRemove);
    }

    // Getter and Setter
    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
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

    public double calculateAverageBudget() {
        if (projects.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Project proj : projects) {
            total += proj.getBudget();
        }
        return total / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> result = new ArrayList<>();
        for (Project proj : projects) {
            if (proj instanceof CommunityProject) {
                CommunityProject cp = (CommunityProject) proj;
                if (cp.getFundingGroup() != null) {
                    result.add(cp.getFundingGroup().getType());
                }
            }
        }
        return result;
    }

    // Getters and Setters
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

class Employee {
    private EmployeeType type;
    private String name;
    private String email;
    private String ID;
    private String number;

    public Employee() {
    }

    // Getters and Setters
    public EmployeeType getType() {
        return type;
    }

    public void setType(EmployeeType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        this.ID = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
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
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public List<Employee> getWorkingEmployees() {
        return workingEmployees;
    }

    public void setWorkingEmployees(List<Employee> workingEmployees) {
        this.workingEmployees = workingEmployees;
    }

    public void addWorkingEmployee(Employee employee) {
        this.workingEmployees.add(employee);
    }
}

class ProductionProject extends Project {
    private String siteCode;

    public ProductionProject() {
        super();
    }

    // Getters and Setters
    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }
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

    // Getters and Setters
    public FundingGroup getFundingGroup() {
        return fundingGroup;
    }

    public void setFundingGroup(FundingGroup group) {
        this.fundingGroup = group;
    }
}

class CommunityProject extends Project {
    private FundingGroup fundingGroup;

    public CommunityProject() {
        super();
    }

    // Getters and Setters
    public FundingGroup getFundingGroup() {
        return fundingGroup;
    }

    public void setFundingGroup(FundingGroup group) {
        this.fundingGroup = group;
    }
}

class FundingGroup {
    private String name;
    private FundingGroupType type;

    public FundingGroup() {
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FundingGroupType getType() {
        return type;
    }

    public void setType(FundingGroupType type) {
        this.type = type;
    }
}