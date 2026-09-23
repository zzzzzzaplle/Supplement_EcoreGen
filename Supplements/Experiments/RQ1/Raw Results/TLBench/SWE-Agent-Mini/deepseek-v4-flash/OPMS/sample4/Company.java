import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<Department>();
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        for (Department d : departments) {
            for (Project p : d.getProjects()) {
                total += p.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<Employee> productionEmployees = new HashSet<Employee>();
        for (Department d : departments) {
            for (Project p : d.getProjects()) {
                if (p instanceof ProductionProject) {
                    for (Employee e : p.getWorkingEmployees()) {
                        productionEmployees.add(e);
                    }
                }
            }
        }
        return productionEmployees.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            throw new IllegalStateException("Cannot add department: company already has 8 departments (maximum).");
        }
        for (Department d : departments) {
            if (d.getID() != null && d.getID().equals(department.getID())) {
                throw new IllegalArgumentException("A department with ID " + department.getID() + " already exists.");
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
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
            throw new IllegalArgumentException("Department with ID " + name + " not found.");
        }
        departments.remove(toRemove);
    }
}
