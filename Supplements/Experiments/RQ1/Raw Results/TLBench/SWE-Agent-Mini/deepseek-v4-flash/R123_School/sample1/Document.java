import java.util.Objects;

class Document {
    private String name;
    
    public Document(String name) {
        this.name = name;
    }
    
    public Document() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(name, document.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
