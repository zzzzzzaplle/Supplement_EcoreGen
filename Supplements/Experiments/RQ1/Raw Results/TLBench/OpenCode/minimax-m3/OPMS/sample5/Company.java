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
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        if (departments != null) {
            for (Department d : departments) {
                List<Project> projects = d.getProjects();
                if (projects != null) {
                    for (Project p : projects) {
                        total += p.getBudget();
                    }
                }
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<String> distinctIds = new HashSet<String>();
        if (departments != null) {
            for (Department d : departments) {
                List<Project> projects = d.getProjects();
                if (projects != null) {
                    for (Project p : projects) {
                        if (p instanceof ProductionProject) {
                            List<Employee> workers = p.getWorkingEmployees();
                            if (workers != null) {
                                for (Employee e : workers) {
                                    if (e != null && e.getID() != null) {
                                        distinctIds.add(e.getID());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return distinctIds.size();
    }

    public void addDepartment(Department department) {
        if (departments == null) {
            departments = new ArrayList<Department>();
        }
        if (departments.size() >= 8) {
            throw new IllegalStateException("Company already has the maximum number of departments (8).");
        }
        if (department == null || department.getName() == null) {
            throw new IllegalArgumentException("Department or department name cannot be null.");
        }
        for (Department d : departments) {
            if (d.getName() != null && d.getName().equals(department.getName())) {
                throw new IllegalArgumentException("A department with the same name already exists.");
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments == null) {
            throw new IllegalStateException("Department list is not initialized.");
        }
        if (departments.size() <= 2) {
            throw new IllegalStateException("Company must have at least 2 departments.");
        }
        if (name == null) {
            throw new IllegalArgumentException("Department name cannot be null.");
        }
        boolean removed = false;
        for (int i = 0; i < departments.size(); i++) {
            Department d = departments.get(i);
            if (d.getName() != null && d.getName().equals(name)) {
                departments.remove(i);
                removed = true;
                break;
            }
        }
        if (!removed) {
            throw new IllegalArgumentException("No department found with the given name.");
        }
    }
}
