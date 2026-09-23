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
            for (Project project : dept.getProjects()) {
                total += project.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        java.util.Set<String> uniqueEmployeeIds = new java.util.HashSet<String>();
        for (Department dept : departments) {
            for (Project project : dept.getProjects()) {
                if (project instanceof ProductionProject) {
                    for (Employee emp : project.getWorkingEmployees()) {
                        uniqueEmployeeIds.add(emp.getID());
                    }
                }
            }
        }
        return uniqueEmployeeIds.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            System.out.println("Cannot add department: company already has the maximum of 8 departments.");
            return;
        }
        if (department == null) {
            System.out.println("Cannot add a null department.");
            return;
        }
        for (Department d : departments) {
            if (d.getID() != null && d.getID().equals(department.getID())) {
                System.out.println("Cannot add department: a department with the same ID already exists.");
                return;
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            System.out.println("Cannot remove department: company must always have at least 2 departments.");
            return;
        }
        if (name == null) {
            System.out.println("Cannot remove department: name is null.");
            return;
        }
        Department toRemove = null;
        for (Department d : departments) {
            if (name.equals(d.getID())) {
                toRemove = d;
                break;
            }
        }
        if (toRemove == null) {
            System.out.println("Cannot remove department: no department found with ID " + name);
            return;
        }
        departments.remove(toRemove);
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
