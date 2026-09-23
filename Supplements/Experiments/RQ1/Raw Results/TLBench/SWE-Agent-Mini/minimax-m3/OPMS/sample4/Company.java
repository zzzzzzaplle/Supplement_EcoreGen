import java.util.List;
import java.util.ArrayList;

public class Company {
    private List<Department> departments;

    public Company() {
        this.departments = new ArrayList<>();
    }

    public double calculateTotalBudget() {
        double totalBudget = 0.0;
        for (Department department : departments) {
            for (int i = 0; i < department.getProjects().size(); i++) {
                totalBudget += department.getProjects().get(i).getBudget();
            }
        }
        return totalBudget;
    }

    public int countEmployeesInProductionProjects() {
        List<String> uniqueEmployeeIds = new ArrayList<>();
        for (Department department : departments) {
            for (int i = 0; i < department.getProjects().size(); i++) {
                Project project = department.getProjects().get(i);
                if (project instanceof ProductionProject) {
                    for (int j = 0; j < project.getWorkingEmployees().size(); j++) {
                        String empId = project.getWorkingEmployees().get(j).getID();
                        if (!uniqueEmployeeIds.contains(empId)) {
                            uniqueEmployeeIds.add(empId);
                        }
                    }
                }
            }
        }
        return uniqueEmployeeIds.size();
    }

    public void addDepartment(Department department) {
        if (departments.size() >= 8) {
            return;
        }
        for (Department d : departments) {
            if (d.getID().equals(department.getID())) {
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
            if (departments.get(i).getID().equals(name)) {
                departments.remove(i);
                return;
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
