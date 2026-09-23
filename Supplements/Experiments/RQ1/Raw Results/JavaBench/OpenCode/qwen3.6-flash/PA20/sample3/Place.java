import java.util.Objects;
import java.util.StringJoiner;

class Place implements Cloneable {
    private int x;
    private int y;

    public Place() {}

    public Place(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public void x(int x) {
        this.x = x;
    }

    public int y() {
        return y;
    }

    public void y(int y) {
        this.y = y;
    }

    public Place clone() throws CloneNotSupportedException {
        Place cloned = (Place) super.clone();
        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return x == place.x && y == place.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Place[", "]")
                .add("x=" + x)
                .add("y=" + y)
                .toString();
    }
}
