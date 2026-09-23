import java.util.List;
import java.util.ArrayList;

public class Company {
    private List<Department> departments = new ArrayList<Department>();

    public Company() {
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        for (Department d : departments) {
            for (Project p : d.getProjects()) {
                total += p.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        List<Employee> countedEmployees = new ArrayList<Employee>();
        for (Department d : departments) {
            for (Project p : d.getProjects()) {
                if (p instanceof ProductionProject) {
                    for (Employee e : p.getWorkingEmployees()) {
                        if (!countedEmployees.contains(e)) {
                            countedEmployees.add(e);
                        }
                    }
                }
            }
        }
        return countedEmployees.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            throw new IllegalStateException("Cannot add department: maximum of 8 departments reached.");
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
            throw new IllegalStateException("Cannot remove department: minimum of 2 departments required.");
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

    public List<Department> getDepartments() {
        return departments;
    }
}
