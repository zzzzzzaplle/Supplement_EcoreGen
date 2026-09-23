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

    public double getSalary() {
        BigDecimal workerSalary = BigDecimal.valueOf(weeklyWorkingHour * hourlyRates);
        return workerSalary.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
