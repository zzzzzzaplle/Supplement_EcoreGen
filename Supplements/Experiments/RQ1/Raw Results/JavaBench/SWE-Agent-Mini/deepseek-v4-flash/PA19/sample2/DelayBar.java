/**
 * Tracks the delay before water starts flowing and the distance water flows.
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
        currentValue--;
    }

    /**
     * Returns the distance water should flow.
     * Negative during countdown, positive after delay ends.
     *
     * @return the distance
     */
    public int distance() {
        return -currentValue;
    }

    public int getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(int initialValue) {
        this.initialValue = initialValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }
}
