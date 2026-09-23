import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        Set<Employee> employees = new HashSet<Employee>();
        for (Department dept : departments) {
            for (Project project : dept.getProjects()) {
                if (project instanceof ProductionProject) {
                    for (Employee emp : ((ProductionProject) project).getWorkingEmployees()) {
                        employees.add(emp);
                    }
                }
            }
        }
        return employees.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() < 8) {
            departments.add(department);
        }
        // reject if already 8
    }

    public void removeDepartment(String name) {
        if (departments.size() > 2) {
            Iterator<Department> it = departments.iterator();
            while (it.hasNext()) {
                Department dept = it.next();
                if (dept.getID().equals(name)) {
                    it.remove();
                    break;
                }
            }
        }
        // reject if already 2 or less
    }
}
