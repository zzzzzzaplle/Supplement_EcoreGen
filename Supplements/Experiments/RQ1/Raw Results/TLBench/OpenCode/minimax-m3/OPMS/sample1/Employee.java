public class Employee {
    private EmployeeType type;
    private String name;
    private String email;
    private String ID;
    private String number;

    public Employee() {
    }

    public EmployeeType getType() {
        return this.type;
    }

    public void setType(EmployeeType type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getID() {
        return this.ID;
    }

    public void setID(String id) {
        this.ID = id;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
