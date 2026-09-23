public class Employee {
    private EmployeeType type;
    private String name;
    private String email;
    private String ID;
    private String number;

    public Employee() {
    }

    public Employee(EmployeeType type, String name, String email, String ID, String number) {
        this.type = type;
        this.name = name;
        this.email = email;
        this.ID = ID;
        this.number = number;
    }

    public EmployeeType getType() {
        return type;
    }

    public void setType(EmployeeType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        this.ID = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
