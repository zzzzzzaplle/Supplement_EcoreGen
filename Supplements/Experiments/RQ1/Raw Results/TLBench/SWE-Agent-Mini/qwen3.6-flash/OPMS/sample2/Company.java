import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<>();
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        for (Department dept : this.departments) {
            for (Project project : dept.getProjects()) {
                total += project.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<Employee> uniqueEmployees = new HashSet<>();
        for (Department dept : this.departments) {
            for (Project project : dept.getProjects()) {
                if (project instanceof ProductionProject) {
                    for (Employee emp : project.getWorkingEmployees()) {
                        uniqueEmployees.add(emp);
                    }
                }
            }
        }
        return uniqueEmployees.size();
    }

    public void addDepartment(Department department) {
        if (this.departments.size() < 8) {
            this.departments.add(department);
        }
    }

    public void removeDepartment(String name) {
        if (this.departments.size() > 2) {
            for (int i = 0; i < this.departments.size(); i++) {
                if (this.departments.get(i).getID().equals(name)) {
                    this.departments.remove(i);
                    break;
                }
            }
        }
    }

    public List<Department> getDepartments() {
        return departments;
    }
}
