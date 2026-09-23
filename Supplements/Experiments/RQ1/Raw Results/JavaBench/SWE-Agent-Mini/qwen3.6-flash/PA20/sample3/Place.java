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
    
    public void x(int x) {
        this.x = x;
    }
    
    public int y() {
        return y;
    }
    
    public void y(int y) {
        this.y = y;
    }
    
    @Override
    public Place clone() throws CloneNotSupportedException {
        return (Place) super.clone();
    }
}
