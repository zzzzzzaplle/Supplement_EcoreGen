import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a company comprised of 2 to 8 departments.
 */
 class Company {
    private List<Department> departments;

    /**
     * Creates an empty company with an initialized departments list.
     */
    public Company() {
        this.departments = new ArrayList<>();
    }

    /**
     * Calculates the sum of the budget amounts of all projects in all departments.
     *
     * @return total budget of all projects in the company
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
     * Counts the distinct number of employees who work on at least one production project in the company.
     *
     * @return count of distinct employees assigned to production projects
     */
    public int countEmployeesInProductionProjects() {
        Set<String> distinctEmployeeKeys = new HashSet<>();
        if (departments != null) {
            for (Department department : departments) {
                if (department != null && department.getProjects() != null) {
                    for (Project project : department.getProjects()) {
                        if (project instanceof ProductionProject && project.getWorkingEmployees() != null) {
                            for (Employee employee : project.getWorkingEmployees()) {
                                if (employee != null) {
                                    distinctEmployeeKeys.add(employeeUniqueKey(employee));
                                }
                            }
                        }
                    }
                }
            }
        }
        return distinctEmployeeKeys.size();
    }

    /**
     * Adds a new department by unique name only if the current number of departments is less than 8.
     * Rejects any operation that would violate the company department constraints.
     *
     * @param department department to add
     * @throws IllegalArgumentException if the constraints are violated
     */
    public void addDepartment(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null.");
        }
        if (departments == null) {
            departments = new ArrayList<>();
        }
        if (departments.size() >= 8) {
            throw new IllegalArgumentException("Company cannot have more than 8 departments.");
        }

        String newId = department.getID();
        for (Department existing : departments) {
            if (existing != null && Objects.equals(existing.getID(), newId)) {
                throw new IllegalArgumentException("Department ID must be unique.");
            }
        }

        departments.add(department);
        if (departments.size() < 2 || departments.size() > 8) {
            // rollback to preserve invariant
            departments.remove(department);
            throw new IllegalArgumentException("Company must contain between 2 and 8 departments.");
        }
    }

    /**
     * Removes an existing department by name only if the current number of departments is greater than 2.
     * Rejects any operation that would violate the company department constraints.
     *
     * @param name department name/ID to remove
     * @throws IllegalArgumentException if the constraints are violated or not found
     */
    public void removeDepartment(String name) {
        if (departments == null) {
            throw new IllegalArgumentException("No departments available.");
        }
        if (departments.size() <= 2) {
            throw new IllegalArgumentException("Company must contain at least 2 departments.");
        }

        Department target = null;
        for (Department department : departments) {
            if (department != null && Objects.equals(department.getID(), name)) {
                target = department;
                break;
            }
        }

        if (target == null) {
            throw new IllegalArgumentException("Department not found.");
        }

        departments.remove(target);
        if (departments.size() < 2 || departments.size() > 8) {
            // rollback to preserve invariant
            departments.add(target);
            throw new IllegalArgumentException("Company must contain between 2 and 8 departments.");
        }
    }

    /**
     * Gets the departments in this company.
     *
     * @return list of departments
     */
    public List<Department> getDepartments() {
        return departments;
    }

    /**
     * Sets the departments in this company.
     *
     * @param departments list of departments
     */
    public void setDepartments(List<Department> departments) {
        this.departments = departments;
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
     * Creates a department with initialized collections.
     */
    public Department() {
        this.employees = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    /**
     * Calculates the average budget amount of all projects in this department.
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
     * Retrieves the funding group type of all community projects within this department.
     *
     * @return list of funding group types for community projects
     */
    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> result = new ArrayList<>();
        if (projects != null) {
            for (Project project : projects) {
                if (project instanceof CommunityProject) {
                    CommunityProject communityProject = (CommunityProject) project;
                    if (communityProject.getFundingGroup() != null) {
                        result.add(communityProject.getFundingGroup().getType());
                    } else {
                        result.add(null);
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
}

/**
 * Employee type enumeration.
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
     * Creates an employee with no initial values.
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
 * Abstract base class for all project types.
 */
abstract class Project {
    private String title;
    private String description;
    private double budget;
    private Date deadline;
    private List<Employee> workingEmployees;

    /**
     * Creates a project with initialized working employees list.
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

/**
 * Represents a production project characterized by a site code.
 */
class ProductionProject extends Project {
    private String siteCode;

    /**
     * Creates a production project.
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
 * Represents a research project.
 */
class ResearchProject extends Project {
    /**
     * Creates a research project.
     */
    public ResearchProject() {
        super();
    }
}

/**
 * Represents an education project associated with one funding group.
 */
class EducationProject extends Project {
    private FundingGroup fundingGroup;

    /**
     * Creates an education project.
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
 * Represents a community project associated with one funding group.
 */
class CommunityProject extends Project {
    private FundingGroup fundingGroup;

    /**
     * Creates a community project.
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
 * Funding group type enumeration.
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
     * Creates a funding group with no initial values.
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