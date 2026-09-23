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
     * Unparameterized constructor.
     */
    public Company() {
        this.departments = new ArrayList<>();
    }

    /**
     * Returns the departments of the company.
     *
     * @return list of departments
     */
    public List<Department> getDepartments() {
        return departments;
    }

    /**
     * Sets the departments of the company.
     * This method enforces the 2..8 constraint.
     *
     * @param departments list of departments
     * @throws IllegalArgumentException if constraints are violated
     */
    public void setDepartments(List<Department> departments) {
        if (departments == null) {
            throw new IllegalArgumentException("Departments cannot be null.");
        }
        if (departments.size() < 2 || departments.size() > 8) {
            throw new IllegalArgumentException("Company must have between 2 and 8 departments.");
        }
        this.departments = new ArrayList<>(departments);
    }

    /**
     * Calculates the total budget of all projects in all departments.
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
     * Adds a department if the company remains within 2..8 departments and the department name is unique.
     *
     * @param department department to add
     * @throws IllegalArgumentException if constraints are violated
     */
    public void addDepartment(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null.");
        }
        if (departments == null) {
            departments = new ArrayList<>();
        }
        if (departments.size() >= 8) {
            throw new IllegalArgumentException("Cannot add more than 8 departments.");
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
     * Removes a department by name only if the company remains within 2..8 departments.
     *
     * @param name department ID/name to remove
     * @throws IllegalArgumentException if constraints are violated
     */
    public void removeDepartment(String name) {
        if (departments == null) {
            throw new IllegalArgumentException("Departments cannot be null.");
        }
        if (departments.size() <= 2) {
            throw new IllegalArgumentException("Cannot remove department when company has only 2 departments.");
        }
        Department toRemove = null;
        for (Department department : departments) {
            if (department != null && Objects.equals(department.getID(), name)) {
                toRemove = department;
                break;
            }
        }
        if (toRemove == null) {
            throw new IllegalArgumentException("Department not found.");
        }
        departments.remove(toRemove);
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
     * Unparameterized constructor.
     */
    public Department() {
        this.employees = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    /**
     * Returns the department ID.
     *
     * @return department ID
     */
    public String getID() {
        return ID;
    }

    /**
     * Sets the department ID.
     *
     * @param id department ID
     */
    public void setID(String id) {
        this.ID = id;
    }

    /**
     * Returns the department email.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the department email.
     *
     * @param email department email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the employees of the department.
     *
     * @return list of employees
     */
    public List<Employee> getEmployees() {
        return employees;
    }

    /**
     * Adds an employee to the department.
     *
     * @param employee employee to add
     */
    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        if (employees == null) {
            employees = new ArrayList<>();
        }
        employees.add(employee);
    }

    /**
     * Returns the projects of the department.
     *
     * @return list of projects
     */
    public List<Project> getProjects() {
        return projects;
    }

    /**
     * Adds a project to the department.
     *
     * @param project project to add
     */
    public void addProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null.");
        }
        if (projects == null) {
            projects = new ArrayList<>();
        }
        projects.add(project);
    }

    /**
     * Calculates the average budget of all projects in the department.
     *
     * @return average budget, or 0.0 if no projects exist
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
     * Retrieves the funding group types of all community projects in the department.
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
}

/**
 * Employee types.
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

    /**
     * Returns the employee type.
     *
     * @return employee type
     */
    public EmployeeType getType() {
        return type;
    }

    /**
     * Sets the employee type.
     *
     * @param type employee type
     */
    public void setType(EmployeeType type) {
        this.type = type;
    }

    /**
     * Returns the employee name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the employee name.
     *
     * @param name employee name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the employee email.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the employee email.
     *
     * @param email employee email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the employee ID.
     *
     * @return employee ID
     */
    public String getID() {
        return ID;
    }

    /**
     * Sets the employee ID.
     *
     * @param id employee ID
     */
    public void setID(String id) {
        this.ID = id;
    }

    /**
     * Returns the employee number.
     *
     * @return employee number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the employee number.
     *
     * @param number employee number
     */
    public void setNumber(String number) {
        this.number = number;
    }
}

/**
 * Abstract base class for all projects.
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

    /**
     * Returns the project title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the project title.
     *
     * @param title project title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the project description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the project description.
     *
     * @param description project description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the project budget.
     *
     * @return budget
     */
    public double getBudget() {
        return budget;
    }

    /**
     * Sets the project budget.
     *
     * @param budget project budget
     */
    public void setBudget(double budget) {
        this.budget = budget;
    }

    /**
     * Returns the project deadline.
     *
     * @return deadline
     */
    public Date getDeadline() {
        return deadline;
    }

    /**
     * Sets the project deadline.
     *
     * @param deadline project deadline
     */
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    /**
     * Returns the employees working on the project.
     *
     * @return list of working employees
     */
    public List<Employee> getWorkingEmployees() {
        return workingEmployees;
    }

    /**
     * Adds a working employee to the project.
     *
     * @param employee employee to add
     */
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

    /**
     * Returns the site code.
     *
     * @return site code
     */
    public String getSiteCode() {
        return siteCode;
    }

    /**
     * Sets the site code.
     *
     * @param siteCode site code
     */
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

    /**
     * Returns the funding group.
     *
     * @return funding group
     */
    public FundingGroup getFundingGroup() {
        return fundingGroup;
    }

    /**
     * Sets the funding group.
     *
     * @param group funding group
     */
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

    /**
     * Returns the funding group.
     *
     * @return funding group
     */
    public FundingGroup getFundingGroup() {
        return fundingGroup;
    }

    /**
     * Sets the funding group.
     *
     * @param group funding group
     */
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

    /**
     * Returns the funding group name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the funding group name.
     *
     * @param name funding group name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the funding group type.
     *
     * @return type
     */
    public FundingGroupType getType() {
        return type;
    }

    /**
     * Sets the funding group type.
     *
     * @param type funding group type
     */
    public void setType(FundingGroupType type) {
        this.type = type;
    }
}