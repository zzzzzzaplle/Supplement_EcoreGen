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
     * Unparameterized constructor.
     */
    public Company() {
        this.departments = new ArrayList<>();
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
     * Counts the distinct number of employees who work on at least one production project.
     *
     * @return distinct employee count
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
                                    distinctEmployeeKeys.add(employee.getID() + "|" + employee.getNumber());
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
     * Adds a new department if the current number is less than 8 and the department name is unique.
     *
     * @param department department to add
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
     * Removes an existing department by name if the current number of departments is greater than 2.
     *
     * @param name department name/ID to remove
     */
    public void removeDepartment(String name) {
        if (departments == null) {
            departments = new ArrayList<>();
        }
        if (departments.size() <= 2) {
            throw new IllegalStateException("Company must have at least 2 departments.");
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
    }

    /**
     * Gets the list of departments.
     *
     * @return list of departments
     */
    public List<Department> getDepartments() {
        return departments;
    }

    /**
     * Sets the list of departments. Must contain between 2 and 8 departments inclusive.
     *
     * @param departments departments list
     */
    public void setDepartments(List<Department> departments) {
        if (departments == null || departments.size() < 2 || departments.size() > 8) {
            throw new IllegalArgumentException("Company must have between 2 and 8 departments.");
        }
        this.departments = departments;
    }
}

/**
 * Represents a department in a company.
 */
class Department {
    private String ID;
    private String email;
    private List<Employee> employees;
    private List<Project> projects;

    /**
     * Unparameterized constructor.
     */
    public Department() {
        this.employees = new ArrayList<>();
        this.projects = new ArrayList<>();
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
        for (Project project : projects) {
            if (project != null) {
                total += project.getBudget();
            }
        }
        return total / projects.size();
    }

    /**
     * Retrieves funding group types of all community projects within this department.
     *
     * @return list of funding group types
     */
    public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
        List<FundingGroupType> result = new ArrayList<>();
        if (projects != null) {
            for (Project project : projects) {
                if (project instanceof CommunityProject) {
                    CommunityProject communityProject = (CommunityProject) project;
                    FundingGroup fundingGroup = communityProject.getFundingGroup();
                    if (fundingGroup != null) {
                        result.add(fundingGroup.getType());
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

    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        if (employees == null) {
            employees = new ArrayList<>();
        }
        employees.add(employee);
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void addProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null.");
        }
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
     * Unparameterized constructor.
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
     * Unparameterized constructor.
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
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        if (workingEmployees == null) {
            workingEmployees = new ArrayList<>();
        }
        workingEmployees.add(employee);
    }
}

/**
 * Represents a production project.
 */
class ProductionProject extends Project {
    private String siteCode;

    /**
     * Unparameterized constructor.
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
     * Unparameterized constructor.
     */
    public ResearchProject() {
        super();
    }
}

/**
 * Represents an education project.
 */
class EducationProject extends Project {
    private FundingGroup fundingGroup;

    /**
     * Unparameterized constructor.
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
 * Represents a community project.
 */
class CommunityProject extends Project {
    private FundingGroup fundingGroup;

    /**
     * Unparameterized constructor.
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
 * Funding group types.
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
     * Unparameterized constructor.
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