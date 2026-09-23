public class DelayBar {
    private int initialValue;
    private int currentValue;

    public DelayBar() {}

    public void countdown() {
        if (currentValue > 0) currentValue--;
    }

    public int distance() {
        return currentValue;
    }

    public int getInitialValue() { return initialValue; }
    public void setInitialValue(int initialValue) { this.initialValue = initialValue; }
    public int getCurrentValue() { return currentValue; }
    public void setCurrentValue(int currentValue) { this.currentValue = currentValue; }
}
