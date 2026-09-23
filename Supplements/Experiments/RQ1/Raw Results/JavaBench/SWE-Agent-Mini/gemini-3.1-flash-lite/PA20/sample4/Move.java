import java.util.Objects;
import java.util.StringJoiner;

public class Move implements Cloneable {
    private Place source;
    private Place destination;
    public Move() {}
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return source.equals(move.source) &&
                destination.equals(move.destination);
    }
    public int hashCode() {
        return Objects.hash(source, destination);
    }
    public String toString() {
        return new StringJoiner(", ", Move.class.getSimpleName() + "[", "]")
                .add("source=" + source)
                .add("destination=" + destination)
                .toString();
    }
    public Move clone() throws CloneNotSupportedException {
        Move cloned = (Move) super.clone();
        cloned.source = this.source.clone();
        cloned.destination = this.destination.clone();
        return cloned;
    }
    public Place getSource() { return source; }
    public void setSource(Place source) { this.source = source; }
    public Place getDestination() { return destination; }
    public void setDestination(Place destination) { this.destination = destination; }
}
