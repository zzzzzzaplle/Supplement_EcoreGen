import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<>();
    }

    public double calculateTotalBudget() {
        double total = 0.0;
        for (Department department : departments) {
            if (department != null && department.getProjects() != null) {
                for (Project project : department.getProjects()) {
                    total += project.getBudget();
                }
            }
        }
        return total;
    }

    public int countEmployeesInProductionProjects() {
        Set<Employee> employeeSet = new java.util.HashSet<>();
        for (Department department : departments) {
            if (department != null && department.getProjects() != null) {
                for (Project project : department.getProjects()) {
                    if (project instanceof ProductionProject) {
                        List<Employee> workingEmployees = project.getWorkingEmployees();
                        if (workingEmployees != null) {
                            for (Employee employee : workingEmployees) {
                                employeeSet.add(employee);
                            }
                        }
                    }
                }
            }
        }
        return employeeSet.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            return;
        }
        for (Department d : departments) {
            if (d != null && d.getID().equals(department.getID())) {
                return;
            }
        }
        departments.add(department);
    }

    public void removeDepartment(String name) {
        if (departments.size() <= 2) {
            return;
        }
        for (int i = 0; i < departments.size(); i++) {
            if (departments.get(i) != null && departments.get(i).getID().equals(name)) {
                departments.remove(i);
                break;
            }
        }
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
