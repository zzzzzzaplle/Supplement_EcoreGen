import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<Department>();
    }

    public double calculateTotalBudget() {
        double sum = 0.0;
        if (departments != null) {
            for (Department department : departments) {
                if (department.getProjects() != null) {
                    for (Project project : department.getProjects()) {
                        sum += project.getBudget();
                    }
                }
            }
        }
        return sum;
    }

    public int countEmployeesInProductionProjects() {
        Set<String> uniqueEmployees = new HashSet<String>();
        if (departments != null) {
            for (Department department : departments) {
                if (department.getProjects() != null) {
                    for (Project project : department.getProjects()) {
                        if (project instanceof ProductionProject && project.getWorkingEmployees() != null) {
                            for (Employee employee : project.getWorkingEmployees()) {
                                if (employee != null) {
                                    String key = employee.getID();
                                    if (key == null) {
                                        key = employee.getNumber();
                                    }
                                    if (key == null) {
                                        key = employee.getName();
                                    }
                                    uniqueEmployees.add(key);
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
        if (department == null) {
            return;
        }
        if (departments == null) {
            departments = new ArrayList<Department>();
        }
        if (departments.size() >= 8) {
            return;
        }
        String newId = department.getID();
        for (Department existing : departments) {
            if (existing.getID() != null && existing.getID().equals(newId)) {
                return;
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments == null || departments.size() <= 2 || name == null) {
            return;
        }
        for (int i = 0; i < departments.size(); i++) {
            Department department = departments.get(i);
            if (name.equals(department.getID())) {
                departments.remove(i);
                return;
            }
        }
    }

    public List<Department> getDepartments() {
        return departments;
    }
}
