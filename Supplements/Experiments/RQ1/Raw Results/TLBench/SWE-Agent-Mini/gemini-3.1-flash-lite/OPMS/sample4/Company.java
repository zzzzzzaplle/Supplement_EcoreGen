import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Company {
    private List<Department> departments = new ArrayList<>();

    public Company() {}

    public List<Department> getDepartments() { return departments; }

    public double calculateTotalBudget() {
        double total = 0;
        for (Department d : departments) {
            for (Project p : d.getProjects()) {
                total += p.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<String> employeeIds = new HashSet<>();
        for (Department d : departments) {
            for (Project p : d.getProjects()) {
                if (p instanceof ProductionProject) {
                    for (Employee e : p.getWorkingEmployees()) {
                        employeeIds.add(e.getID());
                    }
                }
            }
        }
        return employeeIds.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() < 8) {
            for (Department d : departments) {
                if (d.getName() != null && d.getName().equals(department.getName())) return;
            }
            departments.add(department);
        }
    }

    public void removeDepartment(String name) {
        if (departments.size() > 2) {
            for (int i = 0; i < departments.size(); i++) {
                if (departments.get(i).getName() != null && departments.get(i).getName().equals(name)) {
                    departments.remove(i);
                    return;
                }
            }
        }
    }
}
