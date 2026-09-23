import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<>();
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        if (departments == null) {
            return total;
        }
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
        if (departments == null) {
            return 0;
        }
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
        if (departments == null) {
            departments = new ArrayList<>();
        }
        if (departments.size() >= 8) {
            return;
        }
        for (Department existing : departments) {
            if (existing.getID() != null && existing.getID().equals(department.getID())) {
                return;
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments == null || departments.size() <= 2) {
            return;
        }
        departments.removeIf(d -> d.getID() != null && d.getID().equals(name));
    }
}
