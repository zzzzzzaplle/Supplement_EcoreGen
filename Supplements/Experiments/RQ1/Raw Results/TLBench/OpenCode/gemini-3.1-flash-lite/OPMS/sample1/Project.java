import java.util.*;

public abstract class Project {
    private String title;
    private String description;
    private double budget;
    private Date deadline;
    private List<Employee> workingEmployees = new ArrayList<>();

    public Project() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
    public List<Employee> getWorkingEmployees() { return workingEmployees; }
    public void addWorkingEmployee(Employee employee) { this.workingEmployees.add(employee); }
}
