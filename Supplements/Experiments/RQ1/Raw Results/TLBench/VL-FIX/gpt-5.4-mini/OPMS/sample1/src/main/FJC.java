import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Date;

/**
 * Represents a company comprised of between 2 and 8 departments.
 */
class Company {
    private List<Department> departments;

    /**
     * Creates an empty company.
     */
    public Company() {
        this.departments = new ArrayList<>();
    }

    /**
     * Creates a company with the specified departments.
     *
     * @param departments departments to assign
     */
    public Company(List<Department> departments) {
        this.departments = departments != null ? departments : new ArrayList<>();
        validateDepartmentCount(this.departments);
    }

    /**
     * Calculates the total budget of all projects in all departments of the company.
     *
     * @return total budget
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
     * Counts distinct employees who work on at least one production project in the company.
     *
     * @return number of distinct employees
     */
    public int countEmployeesInProductionProjects() {
        Set<String> uniqueEmployeeKeys = new HashSet<>();
        if (departments != null) {
            for (Department department : departments) {
                if (department != null && department.getProjects() != null) {
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
        }
        return uniqueEmployeeKeys.size();
    }

    /**
     * Adds a department only if the company contains fewer than 8 departments and
     * the department name/ID is unique.
     *
     * @param department department to add
     * @throws IllegalArgumentException if operation violates constraints
     */
    public void addDepartment(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null.");
        }
        if (departments == null) {
            departments = new ArrayList<>();
        }
        if (departments.size() >= 8) {
            throw new IllegalArgumentException("Cannot add department: maximum of 8 departments allowed.");
        }
        String newName = department.getID();
        for (Department existing : departments) {
            if (existing != null && Objects.equals(existing.getID(), newName)) {
                throw new IllegalArgumentException("Cannot add department: department with the same name already exists.");
            }
        }
        departments.add(department);
    }

    /**
     * Removes an existing department by name only if the company contains more than 2 departments.
     *
     * @param name department name/ID to remove
     * @throws IllegalArgumentException if operation violates constraints
     */
    public void removeDepartment(String name) {
        if (departments == null) {
            throw new IllegalArgumentException("No departments available.");
        }
        if (departments.size() <= 2) {
            throw new IllegalArgumentException("Cannot remove department: minimum of 2 departments required.");
        }
        Department target = null;
        for (Department department : departments) {
            if (department != null && Objects.equals(department.getID(), name)) {
                target = department;
                break;
            }
        }
        if (target == null) {
            throw new IllegalArgumentException("Department not found: " + name);
        }
        departments.remove(target);
    }

    /**
     * Returns the list of departments.
     *
     * @return departments
     */
    public List<Department> getDepartments() {
        if (departments == null) {
            departments = new ArrayList<>();
        }
        return departments;
    }

    /**
     * Sets the list of departments. Must contain between 2 and 8 departments inclusive.
     *
     * @param departments departments to set
     */
    public void setDepartments(List<Department> departments) {
        validateDepartmentCount(departments);
        this.departments = departments;
    }

    private void validateDepartmentCount(List<Department> departments) {
        if (departments == null) {
            throw new IllegalArgumentException("Departments cannot be null.");
        }
        if (departments.size() < 2 || departments.size() > 8) {
            throw new IllegalArgumentException("Company must have between 2 and 8 departments inclusive.");
        }
    }

    private String employeeUniqueKey(Employee employee) {
        return String.valueOf(employee.getID()) + "|" + String.valueOf(employee.getNumber()) + "|" + String.valueOf(employee.getEmail()) + "|" + String.valueOf(employee.getName());
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
     * Creates a department with the given ID and email.
     *
     * @param ID department ID/name
     * @param email department email
     */
    public Department(String ID, String email) {
        this();
        this.ID = ID;
        this.email = email;
    }

    /**
     * Calculates the average budget of all projects in this department.
     *
     * @return average budget, or 0 if there are no projects
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
     * Retrieves the funding group types of all community projects in this department.
     *
     * @return list of funding group types
     */
    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> result = new ArrayList<>();
        if (projects != null) {
            for (Project project : projects) {
                if (project instanceof CommunityProject) {
                    CommunityProject communityProject = (CommunityProject) project;
                    FundingGroup group = communityProject.getFundingGroup();
                    if (group != null) {
                        result.add(group.getType());
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns department ID.
     *
     * @return ID
     */
    public String getID() {
        return ID;
    }

    /**
     * Sets department ID.
     *
     * @param id ID value
     */
    public void setID(String id) {
        this.ID = id;
    }

    /**
     * Returns department email.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets department email.
     *
     * @param email email value
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns employees list.
     *
     * @return employees
     */
    public List<Employee> getEmployees() {
        if (employees == null) {
            employees = new ArrayList<>();
        }
        return employees;
    }

    /**
     * Adds an employee to the department.
     *
     * @param employee employee to add
     */
    public void addEmployee(Employee employee) {
        if (employee == null) {
            return;
        }
        getEmployees().add(employee);
    }

    /**
     * Returns projects list.
     *
     * @return projects
     */
    public List<Project> getProjects() {
        if (projects == null) {
            projects = new ArrayList<>();
        }
        return projects;
    }

    /**
     * Adds a project to the department.
     *
     * @param project project to add
     */
    public void addProject(Project project) {
        if (project == null) {
            return;
        }
        getProjects().add(project);
    }

    /**
     * Sets employees.
     *
     * @param employees employees list
     */
    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    /**
     * Sets projects.
     *
     * @param projects projects list
     */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}

/**
 * Types of employees.
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

    /**
     * Creates an employee with all properties.
     *
     * @param type employee type
     * @param name employee name
     * @param email employee email
     * @param ID employee ID
     * @param number employee number
     */
    public Employee(EmployeeType type, String name, String email, String ID, String number) {
        this.type = type;
        this.name = name;
        this.email = email;
        this.ID = ID;
        this.number = number;
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
 * Abstract project with common properties.
 */
abstract class Project {
    private String title;
    private String description;
    private double budget;
    private Date deadline;
    private List<Employee> workingEmployees;

    /**
     * Creates a project with empty working employees.
     */
    public Project() {
        this.workingEmployees = new ArrayList<>();
    }

    /**
     * Creates a project with the given common attributes.
     *
     * @param title title
     * @param description description
     * @param budget budget
     * @param deadline deadline
     */
    public Project(String title, String description, double budget, Date deadline) {
        this();
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.deadline = deadline;
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
        if (workingEmployees == null) {
            workingEmployees = new ArrayList<>();
        }
        return workingEmployees;
    }

    public void addWorkingEmployee(Employee employee) {
        if (employee == null) {
            return;
        }
        getWorkingEmployees().add(employee);
    }

    public void setWorkingEmployees(List<Employee> workingEmployees) {
        this.workingEmployees = workingEmployees;
    }
}

/**
 * Production project characterized by a site code.
 */
class ProductionProject extends Project {
    private String siteCode;

    /**
     * Creates an empty production project.
     */
    public ProductionProject() {
        super();
    }

    /**
     * Creates a production project with common attributes and site code.
     *
     * @param title title
     * @param description description
     * @param budget budget
     * @param deadline deadline
     * @param siteCode site code
     */
    public ProductionProject(String title, String description, double budget, Date deadline, String siteCode) {
        super(title, description, budget, deadline);
        this.siteCode = siteCode;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }
}

/**
 * Research project with common project attributes.
 */
class ResearchProject extends Project {
    /**
     * Creates an empty research project.
     */
    public ResearchProject() {
        super();
    }

    /**
     * Creates a research project with common attributes.
     *
     * @param title title
     * @param description description
     * @param budget budget
     * @param deadline deadline
     */
    public ResearchProject(String title, String description, double budget, Date deadline) {
        super(title, description, budget, deadline);
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

    /**
     * Creates an education project with common attributes and funding group.
     *
     * @param title title
     * @param description description
     * @param budget budget
     * @param deadline deadline
     * @param fundingGroup funding group
     */
    public EducationProject(String title, String description, double budget, Date deadline, FundingGroup fundingGroup) {
        super(title, description, budget, deadline);
        this.fundingGroup = fundingGroup;
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

    /**
     * Creates a community project with common attributes and funding group.
     *
     * @param title title
     * @param description description
     * @param budget budget
     * @param deadline deadline
     * @param fundingGroup funding group
     */
    public CommunityProject(String title, String description, double budget, Date deadline, FundingGroup fundingGroup) {
        super(title, description, budget, deadline);
        this.fundingGroup = fundingGroup;
    }

    public FundingGroup getFundingGroup() {
        return fundingGroup;
    }

    public void setFundingGroup(FundingGroup group) {
        this.fundingGroup = group;
    }
}

/**
 * Types of funding groups.
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

    /**
     * Creates a funding group with name and type.
     *
     * @param name name
     * @param type type
     */
    public FundingGroup(String name, FundingGroupType type) {
        this.name = name;
        this.type = type;
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