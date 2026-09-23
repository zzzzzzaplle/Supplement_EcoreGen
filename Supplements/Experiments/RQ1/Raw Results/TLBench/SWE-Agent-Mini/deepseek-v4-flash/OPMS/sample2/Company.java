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
                    productionEmployees.addAll(p.getWorkingEmployees());
                }
            }
        }
        return productionEmployees.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            throw new IllegalArgumentException("Company already has 8 departments; cannot add more.");
        }
        for (Department d : departments) {
            if (d.getID().equals(department.getID())) {
                throw new IllegalArgumentException("Department with ID " + department.getID() + " already exists.");
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            throw new IllegalArgumentException("Company has only " + departments.size() + " departments; cannot remove.");
        }
        boolean found = false;
        for (int i = 0; i < departments.size(); i++) {
            if (departments.get(i).getID().equals(name)) {
                departments.remove(i);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Department with ID " + name + " not found.");
        }
    }
}
