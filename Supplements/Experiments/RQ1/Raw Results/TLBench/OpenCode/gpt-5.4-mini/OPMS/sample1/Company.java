import java.util.ArrayList;
import java.util.List;

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
        java.util.HashSet<String> employeeIds = new java.util.HashSet<String>();
        if (departments != null) {
            for (Department department : departments) {
                if (department != null && department.getProjects() != null) {
                    for (Project project : department.getProjects()) {
                        if (project instanceof ProductionProject && project.getWorkingEmployees() != null) {
                            for (Employee employee : project.getWorkingEmployees()) {
                                if (employee != null && employee.getID() != null) {
                                    employeeIds.add(employee.getID());
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
            throw new IllegalArgumentException("Department cannot be null");
        }
        if (departments == null) {
            departments = new ArrayList<Department>();
        }
        if (departments.size() >= 8) {
            throw new IllegalStateException("Company cannot have more than 8 departments");
        }
        String name = department.getID();
        for (Department existing : departments) {
            if (existing != null && existing.getID() != null && existing.getID().equals(name)) {
                throw new IllegalArgumentException("Department name must be unique");
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments == null) {
            departments = new ArrayList<Department>();
        }
        if (departments.size() <= 2) {
            throw new IllegalStateException("Company must have at least 2 departments");
        }
        Department target = null;
        for (Department department : departments) {
            if (department != null && department.getID() != null && department.getID().equals(name)) {
                target = department;
                break;
            }
        }
        if (target == null) {
            throw new IllegalArgumentException("Department not found");
        }
        departments.remove(target);
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
