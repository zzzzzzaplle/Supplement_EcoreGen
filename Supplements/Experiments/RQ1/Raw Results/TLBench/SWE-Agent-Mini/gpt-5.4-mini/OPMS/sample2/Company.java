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
        if (departments == null) {
            return sum;
        }
        for (Department department : departments) {
            if (department != null && department.getProjects() != null) {
                for (Project project : department.getProjects()) {
                    sum += project.getBudget();
                }
            }
        }
        return sum;
    }

    public int countEmployeesInProductionProjects() {
        Set<String> distinctEmployees = new HashSet<String>();
        if (departments == null) {
            return 0;
        }
        for (Department department : departments) {
            if (department == null || department.getProjects() == null) {
                continue;
            }
            for (Project project : department.getProjects()) {
                if (project instanceof ProductionProject && project.getWorkingEmployees() != null) {
                    for (Employee employee : project.getWorkingEmployees()) {
                        if (employee != null) {
                            String key = employee.getID();
                            if (key == null) {
                                key = employee.getNumber();
                            }
                            if (key == null) {
                                key = employee.getName() + "|" + employee.getEmail();
                            }
                            distinctEmployees.add(key);
                        }
                    }
                }
            }
        }
        return distinctEmployees.size();
    }

    public void addDepartment(Department department) {
        if (departments == null) {
            departments = new ArrayList<Department>();
        }
        if (department == null || departments.size() >= 8) {
            return;
        }
        for (Department existing : departments) {
            if (existing != null && existing.getID() != null && existing.getID().equals(department.getID())) {
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

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
