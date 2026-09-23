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

    // Key operations
    public double calculateTotalBudget() {
        double total = 0.0;
        for (Department dept : departments) {
            if (dept != null && dept.getProjects() != null) {
                for (Project project : dept.getProjects()) {
                    if (project != null) {
                        total += project.getBudget();
                    }
                }
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<Employee> uniqueEmployees = new HashSet<>();
        for (Department dept : departments) {
            if (dept != null && dept.getProjects() != null) {
                for (Project project : dept.getProjects()) {
                    if (project instanceof ProductionProject) {
                        List<Employee> workingEmployees = project.getWorkingEmployees();
                        if (workingEmployees != null) {
                            for (Employee emp : workingEmployees) {
                                if (emp != null) {
                                    uniqueEmployees.add(emp);
                                }
                            }
                        }
                    }
                }
            }
        }
        return uniqueEmployees.size();
    }

    public void addDepartment(Department department) {
        if (departments != null && departments.size() < 8 && department != null) {
            departments.add(department);
        }
    }

    public void removeDepartment(String name) {
        if (departments != null && departments.size() > 2 && name != null) {
            List<Department> removed = new ArrayList<>();
            for (Department dept : departments) {
                if (dept != null && name.equals(dept.getName())) {
                    removed.add(dept);
                }
            }
            departments.removeAll(removed);
        }
    }
}
