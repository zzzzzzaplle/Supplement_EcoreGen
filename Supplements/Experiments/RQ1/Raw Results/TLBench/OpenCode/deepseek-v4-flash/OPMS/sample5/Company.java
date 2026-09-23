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
        double total = 0.0;
        for (Department d : departments) {
            for (Project p : d.getProjects()) {
                total += p.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<Employee> employeeSet = new HashSet<Employee>();
        for (Department d : departments) {
            for (Project p : d.getProjects()) {
                if (p instanceof ProductionProject) {
                    employeeSet.addAll(p.getWorkingEmployees());
                }
            }
        }
        return employeeSet.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            return;
        }
        for (Department d : departments) {
            if (d.getID().equals(department.getID())) {
                return;
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            return;
        }
        departments.removeIf(d -> d.getID().equals(name));
    }

    public List<Department> getDepartments() {
        return departments;
    }
}
