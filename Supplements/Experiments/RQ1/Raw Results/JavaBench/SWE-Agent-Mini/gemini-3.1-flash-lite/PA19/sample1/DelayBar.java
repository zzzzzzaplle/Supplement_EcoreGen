public class DelayBar {
    private int initialValue;
    private int currentValue;

    public DelayBar() {
    }

    public void countdown() {
        if (currentValue > 0) {
            currentValue--;
        }
    }

    public int distance() {
        if (currentValue > 0) return 0;
        return initialValue - currentValue;
    }

    public int getInitialValue() { return initialValue; }
    public void setInitialValue(int initialValue) { this.initialValue = initialValue; }
    public int getCurrentValue() { return currentValue; }
    public void setCurrentValue(int currentValue) { this.currentValue = currentValue; }
}
