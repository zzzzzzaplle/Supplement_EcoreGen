import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<>();
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        for (Department department : departments) {
            for (Project project : department.getProjects()) {
                total += project.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<String> employeeIds = new HashSet<>();
        for (Department department : departments) {
            for (Project project : department.getProjects()) {
                if (project instanceof ProductionProject) {
                    for (Employee employee : project.getWorkingEmployees()) {
                        employeeIds.add(employee.getID());
                    }
                }
            }
        }
        return employeeIds.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            return;
        }
        for (Department d : departments) {
            if (d.getID() != null && d.getID().equals(department.getID())) {
                return;
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            return;
        }
        for (int i = 0; i < departments.size(); i++) {
            if (departments.get(i).getID() != null && departments.get(i).getID().equals(name)) {
                departments.remove(i);
                return;
            }
        }
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
            employees = new ArrayList<>();
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
            projects = new ArrayList<>();
        }
        projects.add(project);
    }

    public double calculateAverageBudget() {
        if (projects == null || projects.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Project project : projects) {
            total += project.getBudget();
        }
        return total / projects.size();
    }

    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> types = new ArrayList<>();
        if (projects == null) {
            return types;
        }
        for (Project project : projects) {
            if (project instanceof CommunityProject) {
                CommunityProject cp = (CommunityProject) project;
                if (cp.getFundingGroup() != null) {
                    types.add(cp.getFundingGroup().getType());
                }
            }
        }
        return types;
    }
}

enum EmployeeType {
    TEMPORARY,
    PERMANENT
}

class Employee {
    private EmployeeType type;
    private String name;
    private String email;
    private String ID;
    private String number;

    public Employee() {
    }

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
        if (workingEmployees == null) {
            workingEmployees = new ArrayList<>();
        }
        workingEmployees.add(employee);
    }
}

class ProductionProject extends Project {
    private String siteCode;

    public ProductionProject() {
        super();
    }

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

    public FundingGroup getFundingGroup() {
        return fundingGroup;
    }

    public void setFundingGroup(FundingGroup fundingGroup) {
        this.fundingGroup = fundingGroup;
    }
}

class CommunityProject extends Project {
    private FundingGroup fundingGroup;

    public CommunityProject() {
        super();
    }

    public FundingGroup getFundingGroup() {
        return fundingGroup;
    }

    public void setFundingGroup(FundingGroup fundingGroup) {
        this.fundingGroup = fundingGroup;
    }
}

enum FundingGroupType {
    PRIVATE,
    GOVERNMENT,
    MIXED
}

class FundingGroup {
    private String name;
    private FundingGroupType type;

    public FundingGroup() {
    }

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