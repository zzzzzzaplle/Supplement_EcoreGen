public class Place implements Cloneable {
    private int x;
    private int y;

    public Place() {
    }

    public Place(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int getX() {
        return x;
    }

    public void x(int x) {
        this.x = x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int y() {
        return y;
    }

    public int getY() {
        return y;
    }

    public void y(int y) {
        this.y = y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public Place clone() {
        try {
            Place cloned = (Place) super.clone();
            cloned.x = this.x;
            cloned.y = this.y;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
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
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("(").append(x).append(",").append(y).append(")")
                .toString();
    }
}
