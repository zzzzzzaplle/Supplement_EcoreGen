import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<Department>();
    }

    public double calculateTotalBudget() {
        if (departments == null) {
            return 0.0;
        }
        double total = 0.0;
        for (Department d : departments) {
            if (d == null || d.getProjects() == null) {
                continue;
            }
            for (Project p : d.getProjects()) {
                if (p != null) {
                    total += p.getBudget();
                }
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        if (departments == null) {
            return 0;
        }
        Set<Employee> distinct = new HashSet<Employee>();
        for (Department d : departments) {
            if (d == null || d.getProjects() == null) {
                continue;
            }
            for (Project p : d.getProjects()) {
                if (p instanceof ProductionProject && p.getWorkingEmployees() != null) {
                    for (Employee e : p.getWorkingEmployees()) {
                        if (e != null) {
                            distinct.add(e);
                        }
                    }
                }
            }
        }
        return distinct.size();
    }

    public void addDepartment(Department department) {
        if (departments == null) {
            departments = new ArrayList<Department>();
        }
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null.");
        }
        if (departments.size() >= 8) {
            throw new IllegalStateException("Company already has the maximum of 8 departments.");
        }
        String newId = department.getID();
        for (Department d : departments) {
            if (d != null && newId != null && newId.equals(d.getID())) {
                throw new IllegalStateException("A department with the same ID already exists.");
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments == null) {
            throw new IllegalStateException("No departments to remove.");
        }
        if (departments.size() <= 2) {
            throw new IllegalStateException("Company must have at least 2 departments.");
        }
        Department target = null;
        for (Department d : departments) {
            if (d != null && name != null && name.equals(d.getID())) {
                target = d;
                break;
            }
        }
        if (target == null) {
            throw new IllegalStateException("Department with the given name was not found.");
        }
        departments.remove(target);
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
