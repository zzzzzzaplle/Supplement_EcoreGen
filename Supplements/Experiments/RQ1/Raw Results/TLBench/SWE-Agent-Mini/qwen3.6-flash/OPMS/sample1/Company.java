import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<>();
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        for (Department department : departments) {
            for (Project project : department.getProjects()) {
                total += project.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<String> uniqueEmployeeIDs = new HashSet<>();
        for (Department department : departments) {
            for (Project project : department.getProjects()) {
                if (project instanceof ProductionProject) {
                    for (Employee employee : project.getWorkingEmployees()) {
                        uniqueEmployeeIDs.add(employee.getID());
                    }
                }
            }
        }
        return uniqueEmployeeIDs.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            return;
        }
        // Ensure unique name
        for (Department dept : departments) {
            if (dept.getID() != null && dept.getID().equals(department.getID())) {
                return;
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            return;
        }
        Department toRemove = null;
        for (Department dept : departments) {
            if (dept.getID() != null && dept.getID().equals(name)) {
                toRemove = dept;
                break;
            }
        }
        if (toRemove != null) {
            departments.remove(toRemove);
        }
    }
}
