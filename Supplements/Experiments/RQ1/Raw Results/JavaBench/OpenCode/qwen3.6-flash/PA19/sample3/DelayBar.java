/**
 * Represents a countdown delay bar for water flow.
 */
public class DelayBar {

    private int initialValue;
    private int currentValue;

    public DelayBar() {
        this(0);
    }

    public DelayBar(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    public void countdown() {
        this.currentValue -= 1;
    }

    public int distance() {
        return -currentValue;
    }
}
