import java.util.ArrayList;
import java.util.List;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<>();
    }

    public double calculateTotalBudget() {
        double total = 0;
        for (Department dept : departments) {
            for (Project project : dept.getProjects()) {
                total += project.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        List<Employee> counted = new ArrayList<>();
        for (Department dept : departments) {
            for (Project project : dept.getProjects()) {
                if (project instanceof ProductionProject) {
                    for (Employee emp : project.getWorkingEmployees()) {
                        if (!counted.contains(emp)) {
                            counted.add(emp);
                        }
                    }
                }
            }
        }
        return counted.size();
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

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
