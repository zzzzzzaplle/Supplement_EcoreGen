import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Date;

/**
 * Represents a company comprised of 2 to 8 departments.
 */
class Company {
    private List<Department> departments;

    /**
     * Creates a company with no departments initialized.
     */
    public Company() {
        this.departments = new ArrayList<>();
    }

    /**
     * Calculates the total budget of all projects in all departments.
     *
     * @return total budget across all departments
     */
    public double calculateTotalBudget() {
        double total = 0.0;
        if (departments != null) {
            for (Department department : departments) {
                if (department != null && department.getProjects() != null) {
                    for (Project project : department.getProjects()) {
                        if (project != null) {
                            total += project.getBudget();
                        }
                    }
                }
            }
        }
        return total;
    }

    /**
     * Counts the distinct employees who work on at least one production project in the company.
     *
     * @return number of distinct employees working on production projects
     */
    public int countEmployeesInProductionProjects() {
        Set<String> uniqueEmployeeKeys = new HashSet<>();
        if (departments != null) {
            for (Department department : departments) {
                if (department == null || department.getProjects() == null) {
                    continue;
                }
                for (Project project : department.getProjects()) {
                    if (project instanceof ProductionProject && project.getWorkingEmployees() != null) {
                        for (Employee employee : project.getWorkingEmployees()) {
                            if (employee != null) {
                                uniqueEmployeeKeys.add(employeeUniqueKey(employee));
                            }
                        }
                    }
                }
            }
        }
        return uniqueEmployeeKeys.size();
    }

    /**
     * Adds a department if the company has fewer than 8 departments and the department name is unique.
     * A department is considered unique by its ID.
     *
     * @param department department to add
     * @throws IllegalStateException    if adding would exceed the maximum department limit
     * @throws IllegalArgumentException if the department is null, duplicates an existing ID, or violates constraints
     */
    public void addDepartment(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null.");
        }
        if (departments == null) {
            departments = new ArrayList<>();
        }
        if (departments.size() >= 8) {
            throw new IllegalStateException("Company cannot have more than 8 departments.");
        }
        String newId = department.getID();
        for (Department existing : departments) {
            if (existing != null && Objects.equals(existing.getID(), newId)) {
                throw new IllegalArgumentException("Department ID must be unique.");
            }
        }
        departments.add(department);
    }

    /**
     * Removes an existing department by name (ID) only if the company still has at least 2 departments after removal.
     *
     * @param name department ID/name to remove
     * @throws IllegalStateException    if removing would violate the minimum department limit
     * @throws IllegalArgumentException if no matching department is found
     */
    public void removeDepartment(String name) {
        if (departments == null) {
            departments = new ArrayList<>();
        }
        if (departments.size() <= 2) {
            throw new IllegalStateException("Company must have at least 2 departments.");
        }
        for (int i = 0; i < departments.size(); i++) {
            Department department = departments.get(i);
            if (department != null && Objects.equals(department.getID(), name)) {
                departments.remove(i);
                return;
            }
        }
        throw new IllegalArgumentException("Department not found.");
    }

    /**
     * Gets the list of departments.
     *
     * @return departments list
     */
    public List<Department> getDepartments() {
        return departments;
    }

    /**
     * Sets the list of departments.
     *
     * @param departments departments list
     */
    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    private String employeeUniqueKey(Employee employee) {
        return String.valueOf(employee.getID()) + "|" + String.valueOf(employee.getNumber());
    }
}

/**
 * Represents a department with employees and projects.
 */
class Department {
    private String ID;
    private String email;
    private List<Employee> employees;
    private List<Project> projects;

    /**
     * Creates an empty department.
     */
    public Department() {
        this.employees = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    /**
     * Calculates the average budget of all projects in the department.
     *
     * @return average budget, or 0.0 if there are no projects
     */
    public double calculateAverageBudget() {
        if (projects == null || projects.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        int count = 0;
        for (Project project : projects) {
            if (project != null) {
                total += project.getBudget();
                count++;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }

    /**
     * Retrieves the funding group types of all community projects within the department.
     *
     * @return list of funding group types for community projects
     */
    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> types = new ArrayList<>();
        if (projects != null) {
            for (Project project : projects) {
                if (project instanceof CommunityProject) {
                    CommunityProject communityProject = (CommunityProject) project;
                    FundingGroup fundingGroup = communityProject.getFundingGroup();
                    if (fundingGroup != null) {
                        types.add(fundingGroup.getType());
                    }
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

    public void addEmployee(Employee employee) {
        if (employees == null) {
            employees = new ArrayList<>();
        }
        employees.add(employee);
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void addProject(Project project) {
        if (projects == null) {
            projects = new ArrayList<>();
        }
        projects.add(project);
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}

/**
 * Type of employee.
 */
enum EmployeeType {
    TEMPORARY,
    PERMANENT
}

/**
 * Represents an employee.
 */
class Employee {
    private EmployeeType type;
    private String name;
    private String email;
    private String ID;
    private String number;

    /**
     * Creates an empty employee.
     */
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

/**
 * Abstract base class for projects.
 */
abstract class Project {
    private String title;
    private String description;
    private double budget;
    private Date deadline;
    private List<Employee> workingEmployees;

    /**
     * Creates a project with no initialized working employees list.
     */
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

    public void addWorkingEmployee(Employee employee) {
        if (workingEmployees == null) {
            workingEmployees = new ArrayList<>();
        }
        workingEmployees.add(employee);
    }

    public void setWorkingEmployees(List<Employee> workingEmployees) {
        this.workingEmployees = workingEmployees;
    }
}

/**
 * Production project with a site code.
 */
class ProductionProject extends Project {
    private String siteCode;

    /**
     * Creates an empty production project.
     */
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

/**
 * Research project.
 */
class ResearchProject extends Project {
    /**
     * Creates an empty research project.
     */
    public ResearchProject() {
        super();
    }
}

/**
 * Education project associated with a funding group.
 */
class EducationProject extends Project {
    private FundingGroup fundingGroup;

    /**
     * Creates an empty education project.
     */
    public EducationProject() {
        super();
    }

    public FundingGroup getFundingGroup() {
        return fundingGroup;
    }

    public void setFundingGroup(FundingGroup group) {
        this.fundingGroup = group;
    }
}

/**
 * Community project associated with a funding group.
 */
class CommunityProject extends Project {
    private FundingGroup fundingGroup;

    /**
     * Creates an empty community project.
     */
    public CommunityProject() {
        super();
    }

    public FundingGroup getFundingGroup() {
        return fundingGroup;
    }

    public void setFundingGroup(FundingGroup group) {
        this.fundingGroup = group;
    }
}

/**
 * Funding group type.
 */
enum FundingGroupType {
    PRIVATE,
    GOVERNMENT,
    MIXED
}

/**
 * Represents a funding group.
 */
class FundingGroup {
    private String name;
    private FundingGroupType type;

    /**
     * Creates an empty funding group.
     */
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