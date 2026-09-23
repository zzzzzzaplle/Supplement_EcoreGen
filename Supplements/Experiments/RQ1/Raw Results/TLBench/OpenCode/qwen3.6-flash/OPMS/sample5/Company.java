import java.util.*;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<>();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            throw new IllegalArgumentException("Company cannot have more than 8 departments");
        }
        for (Department d : departments) {
            if (d.getEmail().equals(department.getEmail())) {
                throw new IllegalArgumentException("Department email already exists");
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            throw new IllegalArgumentException("Company must have at least 2 departments");
        }
        Iterator<Department> iterator = departments.iterator();
        while (iterator.hasNext()) {
            Department d = iterator.next();
            if (d.getEmail().equals(name)) {
                iterator.remove();
                return;
            }
        }
    }

    public double calculateTotalBudget() {
        double total = 0;
        for (Department department : departments) {
            total += department.calculateAverageBudget() * department.getProjects().size();
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<Employee> employees = new HashSet<>();
        for (Department department : departments) {
            for (Project project : department.getProjects()) {
                if (project instanceof ProductionProject) {
                    employees.addAll(project.getWorkingEmployees());
                }
            }
        }
        return employees.size();
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
