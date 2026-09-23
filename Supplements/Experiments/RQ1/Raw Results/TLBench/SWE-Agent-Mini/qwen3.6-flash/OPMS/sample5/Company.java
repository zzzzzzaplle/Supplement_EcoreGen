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

    public double calculateTotalBudget() {
        double total = 0.0;
        for (Department dept : departments) {
            total += dept.calculateAverageBudget(); // Wait, calculateAverageBudget returns average. Need to sum.
            // Let's re-implement logic in Department or use a getter.
            // The class diagram says Department has calculateAverageBudget.
            // To calculate total, I need the sum of budgets of all projects in all departments.
        }
        // Re-writing to access project budgets directly via Department's projects list
        // However, Department doesn't expose a simple getSumBudget().
        // I will iterate through departments and their projects.
        // But calculateTotalBudget is a method of Company.
        // I can access getDepartments(), then getProjects() from each Department.
        
        double totalBudget = 0.0;
        for (Department dept : departments) {
            for (Project project : dept.getProjects()) {
                totalBudget += project.getBudget();
            }
        }
        return totalBudget;
    }

    public int countEmployeesInProductionProjects() {
        Set<Employee> uniqueEmployees = new HashSet<>();
        for (Department dept : departments) {
            for (Project project : dept.getProjects()) {
                if (project instanceof ProductionProject) {
                    uniqueEmployees.addAll(project.getWorkingEmployees());
                }
            }
        }
        return uniqueEmployees.size();
    }

    public void addDepartment(Department department) {
        if (this.departments.size() < 8) {
            // Ensure unique name (implied by requirement)
            for (Department existing : this.departments) {
                if (existing.getID().equals(department.getID())) {
                    return; // Or throw exception, but requirement says "unique name only"
                }
            }
            this.departments.add(department);
        }
    }

    public void removeDepartment(String name) {
        if (this.departments.size() > 2) {
            this.departments.removeIf(dept -> dept.getID().equals(name));
        }
    }
}
