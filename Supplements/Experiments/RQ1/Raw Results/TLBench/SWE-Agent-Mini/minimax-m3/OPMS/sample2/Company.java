import java.util.ArrayList;
import java.util.List;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<Department>();
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        for (Department department : departments) {
            for (Project project : department.getProjects()) {
                total += project.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        java.util.Set<String> employeeIds = new java.util.HashSet<String>();
        for (Department department : departments) {
            for (Project project : department.getProjects()) {
                if (project instanceof ProductionProject) {
                    for (Employee employee : project.getWorkingEmployees()) {
                        employeeIds.add(employee.getID());
                    }
                }
            }
        }
        return employeeIds.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            System.out.println("Cannot add department: company already has the maximum of 8 departments.");
            return;
        }
        for (Department existing : departments) {
            if (existing.getID() != null && existing.getID().equals(department.getID())) {
                System.out.println("Cannot add department: department with the same ID already exists.");
                return;
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            System.out.println("Cannot remove department: company must have at least 2 departments.");
            return;
        }
        boolean removed = false;
        for (int i = 0; i < departments.size(); i++) {
            if (departments.get(i).getID() != null && departments.get(i).getID().equals(name)) {
                departments.remove(i);
                removed = true;
                break;
            }
        }
        if (!removed) {
            System.out.println("Cannot remove department: department with the given name not found.");
        }
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
