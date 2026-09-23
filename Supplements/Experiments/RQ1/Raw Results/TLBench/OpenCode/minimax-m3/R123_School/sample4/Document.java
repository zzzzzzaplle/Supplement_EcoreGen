import java.util.Objects;

public class Document {
    private String name;

    public Document() {
        this.name = "";
    }

    public Document(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Document)) {
            return false;
        }
        Document d = (Document) o;
        return Objects.equals(name, d.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
