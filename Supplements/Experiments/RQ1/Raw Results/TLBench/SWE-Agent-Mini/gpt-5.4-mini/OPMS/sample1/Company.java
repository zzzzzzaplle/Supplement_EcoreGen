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
                if (department != null && department.getProjects() != null) {
                    for (Project project : department.getProjects()) {
                        if (project != null) {
                            sum += project.getBudget();
                        }
                    }
                }
            }
        }
        return sum;
    }

    public int countEmployeesInProductionProjects() {
        Set<String> employeeIds = new HashSet<String>();
        if (departments != null) {
            for (Department department : departments) {
                if (department != null && department.getProjects() != null) {
                    for (Project project : department.getProjects()) {
                        if (project instanceof ProductionProject && project.getWorkingEmployees() != null) {
                            for (Employee employee : project.getWorkingEmployees()) {
                                if (employee != null) {
                                    String key = employee.getID();
                                    if (key == null) {
                                        key = employee.getNumber();
                                    }
                                    if (key != null) {
                                        employeeIds.add(key);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return employeeIds.size();
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
        String name = department.getID();
        for (Department existing : departments) {
            if (existing != null && existing.getID() != null && existing.getID().equals(name)) {
                return;
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments == null || departments.size() <= 2) {
            return;
        }
        for (int i = 0; i < departments.size(); i++) {
            Department department = departments.get(i);
            if (department != null && name != null && name.equals(department.getID())) {
                departments.remove(i);
                return;
            }
        }
    }

    public List<Department> getDepartments() {
        return departments;
    }
}
