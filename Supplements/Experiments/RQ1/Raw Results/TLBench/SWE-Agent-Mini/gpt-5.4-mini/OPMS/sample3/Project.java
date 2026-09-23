import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Project {
    private String title;
    private String description;
    private double budget;
    private Date deadline;
    private List<Employee> workingEmployees;

    public Project() {
        this.workingEmployees = new ArrayList<Employee>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public List<Employee> getWorkingEmployees() {
        return workingEmployees;
    }

    public void addWorkingEmployee(Employee employee) {
        if (this.workingEmployees == null) {
            this.workingEmployees = new ArrayList<Employee>();
        }
        this.workingEmployees.add(employee);
    }
}
