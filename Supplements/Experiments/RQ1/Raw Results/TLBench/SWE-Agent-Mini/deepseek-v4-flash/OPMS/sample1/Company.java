import java.util.ArrayList;
import java.util.List;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<Department>();
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        for (Department dept : departments) {
            for (Project project : dept.getProjects()) {
                total += project.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        List<Employee> counted = new ArrayList<Employee>();
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
            throw new IllegalArgumentException("Company already has 8 departments, cannot add more.");
        }
        for (Department d : departments) {
            if (d.getID().equals(department.getID())) {
                throw new IllegalArgumentException("Department with ID " + department.getID() + " already exists.");
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            throw new IllegalArgumentException("Company must have at least 2 departments, cannot remove.");
        }
        Department toRemove = null;
        for (Department d : departments) {
            if (d.getID().equals(name)) {
                toRemove = d;
                break;
            }
        }
        if (toRemove == null) {
            throw new IllegalArgumentException("Department with ID " + name + " not found.");
        }
        departments.remove(toRemove);
    }
}
