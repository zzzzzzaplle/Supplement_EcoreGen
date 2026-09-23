public class Place implements Cloneable {
    private int x;
    private int y;

    public Place() {
        this.x = 0;
        this.y = 0;
    }

    public Place(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
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
        return java.util.Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Place[" + x + ", " + y + "]";
    }

    @Override
    public Place clone() throws CloneNotSupportedException {
        return (Place) super.clone();
    }
}
