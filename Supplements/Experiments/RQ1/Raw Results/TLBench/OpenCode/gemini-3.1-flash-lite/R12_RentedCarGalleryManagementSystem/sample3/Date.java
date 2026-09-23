public class Date {
    private long time;
    public Date() {}
    public Date(long time) { this.time = time; }
    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }
    public boolean after(Date other) {
        return this.time > other.time;
    }
}
