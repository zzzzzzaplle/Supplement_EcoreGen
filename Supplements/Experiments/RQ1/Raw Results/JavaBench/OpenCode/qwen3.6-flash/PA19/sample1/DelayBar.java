/**
 * Countdown timer for water flow delay.
 */
public class DelayBar {

    private  int initialValue;
    private  int currentValue;

    public DelayBar() {
    }

    public DelayBar(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
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

    /**
     * Decrements the current value by 1 each round.
     */
    public void countdown() {
        this.currentValue--;
    }

    /**
     * Returns -currentValue, representing how far the water should flow
     * (negative during countdown, positive after delay ends).
     */
    public int distance() {
        return -currentValue;
    }
}
