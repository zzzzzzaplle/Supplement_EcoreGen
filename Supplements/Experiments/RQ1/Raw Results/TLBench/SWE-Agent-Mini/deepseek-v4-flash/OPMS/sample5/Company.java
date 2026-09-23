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

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
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
        Set<Employee> productionEmployees = new HashSet<Employee>();
        for (Department dept : departments) {
            for (Project proj : dept.getProjects()) {
                if (proj instanceof ProductionProject) {
                    productionEmployees.addAll(proj.getWorkingEmployees());
                }
            }
        }
        return productionEmployees.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            throw new IllegalArgumentException("Company cannot have more than 8 departments");
        }
        for (Department d : departments) {
            if (d.getID().equals(department.getID())) {
                throw new IllegalArgumentException("Department with ID " + department.getID() + " already exists");
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            throw new IllegalArgumentException("Company must have at least 2 departments");
        }
        Department toRemove = null;
        for (Department d : departments) {
            if (d.getID().equals(name)) {
                toRemove = d;
                break;
            }
        }
        if (toRemove == null) {
            throw new IllegalArgumentException("Department with ID " + name + " not found");
        }
        departments.remove(toRemove);
    }
}
