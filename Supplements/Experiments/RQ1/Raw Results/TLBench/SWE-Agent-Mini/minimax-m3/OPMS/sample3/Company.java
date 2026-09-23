import java.util.ArrayList;
import java.util.List;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<Department>();
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        if (departments == null) return total;
        for (Department d : departments) {
            if (d == null || d.getProjects() == null) continue;
            for (Project p : d.getProjects()) {
                if (p == null) continue;
                total += p.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        List<Employee> counted = new ArrayList<Employee>();
        if (departments == null) return 0;
        for (Department d : departments) {
            if (d == null || d.getProjects() == null) continue;
            for (Project p : d.getProjects()) {
                if (p instanceof ProductionProject) {
                    if (p.getWorkingEmployees() == null) continue;
                    for (Employee e : p.getWorkingEmployees()) {
                        if (e == null) continue;
                        boolean exists = false;
                        for (Employee c : counted) {
                            if (c == e) { exists = true; break; }
                        }
                        if (!exists) counted.add(e);
                    }
                }
            }
        }
        return counted.size();
    }

    public void addDepartment(Department department) {
        if (department == null) return;
        if (departments.size() >= 8) {
            System.out.println("Cannot add department: company already has 8 departments (max).");
            return;
        }
        boolean duplicate = false;
        for (Department d : departments) {
            if (d != null && d.getID() != null && d.getID().equals(department.getID())) {
                duplicate = true;
                break;
            }
        }
        if (duplicate) {
            System.out.println("Cannot add department: a department with the same ID already exists.");
            return;
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (name == null) return;
        if (departments.size() <= 2) {
            System.out.println("Cannot remove department: company must have at least 2 departments.");
            return;
        }
        Department toRemove = null;
        for (Department d : departments) {
            if (d != null && d.getID() != null && d.getID().equals(name)) {
                toRemove = d;
                break;
            }
        }
        if (toRemove != null) {
            departments.remove(toRemove);
        } else {
            System.out.println("Cannot remove department: no department found with the given name (ID).");
        }
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
