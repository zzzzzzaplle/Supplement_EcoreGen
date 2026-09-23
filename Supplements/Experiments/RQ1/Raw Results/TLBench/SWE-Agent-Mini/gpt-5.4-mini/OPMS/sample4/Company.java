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
        double total = 0.0;
        if (departments != null) {
            for (Department department : departments) {
                if (department != null && department.getProjects() != null) {
                    for (Project project : department.getProjects()) {
                        total += project.getBudget();
                    }
                }
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<String> uniqueEmployees = new HashSet<String>();
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
                                    if (key == null) {
                                        key = employee.getName();
                                    }
                                    if (key != null) {
                                        uniqueEmployees.add(key);
                                    }
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
        if (department == null || departments == null) {
            return;
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
        Department target = null;
        for (Department department : departments) {
            if (department != null && department.getID() != null && department.getID().equals(name)) {
                target = department;
                break;
            }
        }
        if (target != null && departments.size() > 2) {
            departments.remove(target);
        }
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        if (departments == null) {
            this.departments = new ArrayList<Department>();
        } else {
            this.departments = departments;
        }
    }
}
