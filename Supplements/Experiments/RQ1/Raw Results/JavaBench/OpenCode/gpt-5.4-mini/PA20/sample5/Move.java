public class Move implements Cloneable {
    private Place source;
    private Place destination;

    public Move() {
    }

    public Move(Place source, Place destination) {
        this.source = source;
        this.destination = destination;
    }

    public Place getSource() {
        return this.source;
    }

    public void setSource(Place source) {
        this.source = source;
    }

    public Place getDestination() {
        return this.destination;
    }

    public void setDestination(Place destination) {
        this.destination = destination;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return source.equals(move.source) &&
                destination.equals(move.destination);
    }

    public int hashCode() {
        return java.util.Objects.hash(source, destination);
    }

    public String toString() {
        return new java.util.StringJoiner(", ", Move.class.getSimpleName() + "[", "]")
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
}
