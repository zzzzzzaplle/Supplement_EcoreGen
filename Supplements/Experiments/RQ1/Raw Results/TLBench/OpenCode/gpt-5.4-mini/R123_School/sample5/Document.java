public class Document {
    private String name;

    public Document() {
        this.name = null;
    }

    public Document(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Document document = (Document) o;
        if (name == null) {
            return document.name == null;
        }
        return name.equals(document.name);
    }

    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }
}
