public abstract class Project {
    private String title;
    private String description;
    private double budget;
    private java.util.Date deadline;
    private java.util.List<Employee> workingEmployees;

    public Project() {
        workingEmployees = new java.util.ArrayList<>();
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

    public java.util.Date getDeadline() {
        return deadline;
    }

    public void setDeadline(java.util.Date deadline) {
        this.deadline = deadline;
    }

    public java.util.List<Employee> getWorkingEmployees() {
        return workingEmployees;
    }

    public void addWorkingEmployee(Employee employee) {
        if (workingEmployees == null) {
            workingEmployees = new java.util.ArrayList<>();
        }
        workingEmployees.add(employee);
    }
}
