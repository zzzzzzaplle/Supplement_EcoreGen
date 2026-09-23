import java.util.ArrayList;
import java.util.Date;
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
                        if (project != null) {
                            total += project.getBudget();
                        }
                    }
                }
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<String> employeeIds = new HashSet<String>();
        if (departments != null) {
            for (Department department : departments) {
                if (department != null && department.getProjects() != null) {
                    for (Project project : department.getProjects()) {
                        if (project instanceof ProductionProject) {
                            List<Employee> workers = project.getWorkingEmployees();
                            if (workers != null) {
                                for (Employee employee : workers) {
                                    if (employee != null && employee.getID() != null) {
                                        employeeIds.add(employee.getID());
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
        if (department.getID() != null) {
            for (Department existing : departments) {
                if (existing != null && department.getID().equals(existing.getID())) {
                    return;
                }
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments == null || departments.size() <= 2 || name == null) {
            return;
        }
        Department target = null;
        for (Department department : departments) {
            if (department != null && name.equals(department.getID())) {
                target = department;
                break;
            }
        }
        if (target != null) {
            departments.remove(target);
        }
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
