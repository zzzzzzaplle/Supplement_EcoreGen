import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Company {
    private List<Department> departments = new ArrayList<>();

    public Company() {}

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
            // Need to check uniqueness - assuming ID is the name/unique identifier
            for(Department d : departments) {
                if(d.getID().equals(department.getID())) return;
            }
            departments.add(department);
        }
    }

    public void removeDepartment(String id) {
        if (departments.size() > 2) {
            departments.removeIf(d -> d.getID().equals(id));
        }
    }

    public List<Department> getDepartments() { return departments; }
    public void setDepartments(List<Department> departments) { this.departments = departments; }
}
