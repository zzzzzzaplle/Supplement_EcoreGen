import java.util.ArrayList;
import java.util.List;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<Department>();
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        for (Department dept : departments) {
            for (Project p : dept.getProjects()) {
                total += p.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        List<Employee> uniqueEmployees = new ArrayList<Employee>();
        for (Department dept : departments) {
            for (Project p : dept.getProjects()) {
                if (p instanceof ProductionProject) {
                    for (Employee e : p.getWorkingEmployees()) {
                        if (!uniqueEmployees.contains(e)) {
                            uniqueEmployees.add(e);
                        }
                    }
                }
            }
        }
        return uniqueEmployees.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            throw new IllegalStateException("Company cannot have more than 8 departments.");
        }
        for (Department d : departments) {
            if (d.getID().equals(department.getID())) {
                throw new IllegalArgumentException("Department with ID already exists.");
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            throw new IllegalStateException("Company must have at least 2 departments.");
        }
        boolean removed = false;
        for (int i = 0; i < departments.size(); i++) {
            if (departments.get(i).getID().equals(name)) {
                departments.remove(i);
                removed = true;
                break;
            }
        }
        if (!removed) {
            throw new IllegalArgumentException("Department with the given name not found.");
        }
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
