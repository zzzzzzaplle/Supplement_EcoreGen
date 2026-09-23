public class ShiftWorker extends Worker {
  private double holidayPremium;

  public ShiftWorker() {
  }

  public double getHolidayPremium() {
    return holidayPremium;
  }

  public void setHolidayPremium(double holidayPremium) {
    this.holidayPremium = holidayPremium;
  }

  public void setDepartment(String department) {
    if (!"Delivery".equals(department)) {
      throw new IllegalStateException("ShiftWorker can only belong to the Delivery department");
    }
    super.setDepartment(department);
  }

  public double calculateHolidayPremium() {
    return Math.round(holidayPremium * 100.0) / 100.0;
  }

  @Override
  public double calculateSalary() {
    double base = getWeeklyWorkingHour() * getHourlyRates();
    double total = base + holidayPremium;
    return Math.round(total * 100.0) / 100.0;
  }
}
