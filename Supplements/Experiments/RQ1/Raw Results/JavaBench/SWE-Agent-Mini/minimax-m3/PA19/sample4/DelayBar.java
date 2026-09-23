/**
 * Delay bar that decrements every round and represents the water-flow distance.
 */
public class DelayBar {

    private int initialValue;
    private int currentValue;

    public DelayBar(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    public DelayBar() {
        this.initialValue = 0;
        this.currentValue = 0;
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

    public void countdown() {
        this.currentValue = this.currentValue - 1;
    }

    public int distance() {
        return -this.currentValue;
    }
}
