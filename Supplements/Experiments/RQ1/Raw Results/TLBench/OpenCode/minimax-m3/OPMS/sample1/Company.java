import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<Department>();
    }

    public List<Department> getDepartments() {
        return this.departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        if (this.departments == null) {
            return total;
        }
        for (Department d : this.departments) {
            List<Project> projects = d.getProjects();
            if (projects == null) {
                continue;
            }
            for (Project p : projects) {
                total += p.getBudget();
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<Employee> distinct = new HashSet<Employee>();
        if (this.departments == null) {
            return 0;
        }
        for (Department d : this.departments) {
            List<Project> projects = d.getProjects();
            if (projects == null) {
                continue;
            }
            for (Project p : projects) {
                if (p instanceof ProductionProject) {
                    List<Employee> workers = p.getWorkingEmployees();
                    if (workers != null) {
                        distinct.addAll(workers);
                    }
                }
            }
        }
        return distinct.size();
    }

    public void addDepartment(Department department) {
        if (this.departments == null) {
            this.departments = new ArrayList<Department>();
        }
        if (this.departments.size() >= 8) {
            throw new IllegalStateException("Company cannot have more than 8 departments.");
        }
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null.");
        }
        String newId = department.getID();
        for (Department existing : this.departments) {
            String existingId = existing.getID();
            if (existingId != null && existingId.equals(newId)) {
                throw new IllegalStateException("Department with ID " + newId + " already exists.");
            }
        }
        this.departments.add(department);
    }

    public void removeDepartment(String name) {
        if (this.departments == null) {
            return;
        }
        if (this.departments.size() <= 2) {
            throw new IllegalStateException("Company must have at least 2 departments.");
        }
        Department toRemove = null;
        for (Department d : this.departments) {
            String id = d.getID();
            if (id != null && id.equals(name)) {
                toRemove = d;
                break;
            }
        }
        if (toRemove != null) {
            this.departments.remove(toRemove);
        }
    }
}
