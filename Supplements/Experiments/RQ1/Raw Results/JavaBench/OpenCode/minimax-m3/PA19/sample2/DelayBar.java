/**
 * Countdown bar representing the delay before water starts to flow.
 * distance() = -currentValue; negative during the countdown, positive once the delay has ended.
 */
public class DelayBar {

    private int initialValue;
    private int currentValue;

    public DelayBar() {
        this.initialValue = 0;
        this.currentValue = 0;
    }

    public DelayBar(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    public void countdown() {
        this.currentValue--;
    }

    public int distance() {
        return -this.currentValue;
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
