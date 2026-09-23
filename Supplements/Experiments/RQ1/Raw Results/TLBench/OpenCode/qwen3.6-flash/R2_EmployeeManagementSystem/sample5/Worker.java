import java.util.Date;
import java.util.List;

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

  @Override
  public double calculateSalary() {
    return Math.round(weeklyWorkingHour * hourlyRates * 100.0) / 100.0;
  }

  @Override
  public double calculateCommission() {
    return 0;
  }

  @Override
  public double calculateHolidayPremium() {
    return 0;
  }
}
