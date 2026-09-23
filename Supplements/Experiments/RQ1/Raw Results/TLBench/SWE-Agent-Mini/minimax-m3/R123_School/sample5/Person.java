public abstract class Person {
    private String id;

    public Person(String id) {
        this.id = id;
    }

    public Person() {
        this.id = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
