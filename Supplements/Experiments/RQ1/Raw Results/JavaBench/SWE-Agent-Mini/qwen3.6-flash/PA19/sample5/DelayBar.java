/**
 * Delay bar that counts down and provides water fill distance.
 */
public class DelayBar {
    private int initialValue;
    private int currentValue;

    public DelayBar() {
    }

    public DelayBar(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    /**
     * Decrements the current value by 1 each round.
     */
    public void countdown() {
        this.currentValue--;
    }

    /**
     * Returns -currentValue, representing how far water should flow.
     * Negative during countdown, positive after delay ends.
     */
    public int distance() {
        return -currentValue;
    }
}
