import java.util.ArrayList;
import java.util.List;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<>();
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public double calculateTotalBudget() {
        double total = 0;
        for (Department d : departments) {
            for (Project p : d.getProjects()) {
                total += p.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        List<Employee> distinctEmployees = new ArrayList<>();
        for (Department d : departments) {
            for (Project p : d.getProjects()) {
                if (p instanceof ProductionProject) {
                    for (Employee e : p.getWorkingEmployees()) {
                        if (!distinctEmployees.contains(e)) {
                            distinctEmployees.add(e);
                        }
                    }
                }
            }
        }
        return distinctEmployees.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() < 8) {
            for (Department d : departments) {
                if (d.getID().equals(department.getID())) {
                    return;
                }
            }
            departments.add(department);
        }
    }

    public void removeDepartment(String name) {
        if (departments.size() > 2) {
            departments.removeIf(d -> d.getID().equals(name));
        }
    }
}
