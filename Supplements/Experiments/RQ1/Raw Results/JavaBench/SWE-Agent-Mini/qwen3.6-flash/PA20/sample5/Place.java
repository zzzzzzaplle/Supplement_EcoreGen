public class Place {
    protected int x;
    protected int y;

    public Place() {
    }

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
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
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
        return "Place{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public Place clone() throws CloneNotSupportedException {
        Place cloned = (Place) super.clone();
        return cloned;
    }
}
