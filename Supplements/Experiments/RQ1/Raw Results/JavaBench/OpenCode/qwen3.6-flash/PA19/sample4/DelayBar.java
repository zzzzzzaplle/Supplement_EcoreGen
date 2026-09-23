public class DelayBar {
    private final int initialValue;
    private int currentValue;

    public DelayBar(int initialValue) {
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    public void countdown() {
        if (currentValue > 0) {
            currentValue--;
        }
    }

    public int distance() {
        return -currentValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public boolean isActive() {
        return currentValue > 0;
    }
}
