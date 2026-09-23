public class DelayBar {
    private int initialValue;
    private int currentValue;

    public DelayBar() {
        this(0, 0);
    }

    public DelayBar(int initialValue, int currentValue) {
        this.initialValue = initialValue;
        this.currentValue = currentValue;
    }

    public int initialValue() {
        return initialValue;
    }

    public void setInitialValue(int initialValue) {
        this.initialValue = initialValue;
    }

    public int currentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public void countdown() {
        this.currentValue += 1;
    }

    public int distance() {
        return -currentValue;
    }
}
