public class Manager extends Employee {
    private double salary;
    private String position;
    private java.util.List<Employee> subordinates;

    public Manager() {
        this.subordinates = new java.util.ArrayList<>();
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public java.util.List<Employee> getSubordinates() {
        return subordinates;
    }

    public int getDirectSubordinateEmployeesCount() {
        return subordinates.size();
    }
}
