import java.util.ArrayList;
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
        for (Department department : departments) {
            List<Project> projects = department.getProjects();
            for (Project project : projects) {
                total += project.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<Employee> uniqueEmployees = new HashSet<Employee>();
        for (Department department : departments) {
            List<Project> projects = department.getProjects();
            for (Project project : projects) {
                if (project instanceof ProductionProject) {
                    List<Employee> workers = project.getWorkingEmployees();
                    for (Employee e : workers) {
                        uniqueEmployees.add(e);
                    }
                }
            }
        }
        return uniqueEmployees.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            return;
        }
        for (Department d : departments) {
            if (d.getID() != null && d.getID().equals(department.getID())) {
                return;
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            return;
        }
        Department toRemove = null;
        for (Department d : departments) {
            if (d.getID() != null && d.getID().equals(name)) {
                toRemove = d;
                break;
            }
        }
        if (toRemove != null) {
            departments.remove(toRemove);
        }
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
