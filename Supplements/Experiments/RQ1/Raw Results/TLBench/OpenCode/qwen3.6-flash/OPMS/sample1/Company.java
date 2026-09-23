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
        this.departments = new ArrayList<>(departments);
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        if (departments == null) {
            return total;
        }
        for (Department department : departments) {
            List<Project> projects = department.getProjects();
            if (projects != null) {
                for (Project project : projects) {
                    total += project.getBudget();
                }
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<Employee> uniqueEmployees = new HashSet<>();
        if (departments == null) {
            return 0;
        }
        for (Department department : departments) {
            List<Project> projects = department.getProjects();
            if (projects != null) {
                for (Project project : projects) {
                    if (project instanceof ProductionProject) {
                        List<Employee> workingEmployees = project.getWorkingEmployees();
                        if (workingEmployees != null) {
                            for (Employee employee : workingEmployees) {
                                uniqueEmployees.add(employee);
                            }
                        }
                    }
                }
            }
        }
        return uniqueEmployees.size();
    }

    public void addDepartment(Department department) {
        if (departments == null) {
            departments = new ArrayList<>();
        }
        if (departments.size() < 8) {
            departments.add(department);
        }
    }

    public void removeDepartment(String name) {
        if (departments == null || departments.isEmpty()) {
            return;
        }
        if (departments.size() > 2) {
            for (int i = 0; i < departments.size(); i++) {
                Department dept = departments.get(i);
                if (dept != null && dept.getID() != null && dept.getID().equals(name)) {
                    departments.remove(i);
                    return;
                }
            }
        }
    }
}
