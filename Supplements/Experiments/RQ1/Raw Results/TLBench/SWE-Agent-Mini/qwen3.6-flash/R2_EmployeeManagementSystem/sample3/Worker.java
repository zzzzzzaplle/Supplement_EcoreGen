import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Worker extends Employee {
    private int weeklyWorkingHour;
    private double hourlyRates;

    public Worker() {
    }

    public int getWeeklyWorkingHour() {
        return weeklyWorkingHour;
    }

    public void setWeeklyWorkingHour(int weeklyWorkingHour) {
        this.weeklyWorkingHour = weeklyWorkingHour;
    }

    public double getHourlyRates() {
        return hourlyRates;
    }

    public void setHourlyRates(double hourlyRates) {
        this.hourlyRates = hourlyRates;
    }

    protected double getWeeklySalary() {
        BigDecimal hoursBD = BigDecimal.valueOf(weeklyWorkingHour);
        BigDecimal ratesBD = BigDecimal.valueOf(hourlyRates);
        return hoursBD.multiply(ratesBD).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
