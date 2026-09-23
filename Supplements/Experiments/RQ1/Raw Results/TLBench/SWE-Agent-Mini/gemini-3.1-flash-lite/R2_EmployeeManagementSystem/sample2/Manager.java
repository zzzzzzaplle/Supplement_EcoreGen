import java.util.ArrayList;
import java.util.List;

public class Manager extends Employee {
    private double salary;
    private String position;
    private List<Employee> subordinates;

    public Manager() {
        this.subordinates = new ArrayList<>();
    }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public List<Employee> getSubordinates() { return subordinates; }
    public void setSubordinates(List<Employee> subordinates) { this.subordinates = subordinates; }
    
    public int getDirectSubordinateEmployeesCount() {
        return subordinates.size();
    }
}
