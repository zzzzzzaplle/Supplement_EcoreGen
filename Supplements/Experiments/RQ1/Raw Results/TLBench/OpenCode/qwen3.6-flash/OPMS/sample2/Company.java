import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Company {
    private List<Department> departments;

    public Company() {
        departments = new ArrayList<>();
    }

    public double calculateTotalBudget() {
        double total = 0;
        for (Department department : departments) {
            if (department.getProjects() != null) {
                for (Project project : department.getProjects()) {
                    total += project.getBudget();
                }
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<Employee> employees = new HashSet<>();
        for (Department department : departments) {
            if (department.getProjects() != null) {
                for (Project project : department.getProjects()) {
                    if (project instanceof ProductionProject) {
                        if (project.getWorkingEmployees() != null) {
                            for (Employee employee : project.getWorkingEmployees()) {
                                employees.add(employee);
                            }
                        }
                    }
                }
            }
        }
        return employees.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() < 8) {
            departments.add(department);
        }
    }

    public void removeDepartment(String name) {
        if (departments.size() > 2) {
            departments.removeIf(d -> d.getID().equals(name));
        }
    }

    public List<Department> getDepartments() {
        return departments;
    }
}
