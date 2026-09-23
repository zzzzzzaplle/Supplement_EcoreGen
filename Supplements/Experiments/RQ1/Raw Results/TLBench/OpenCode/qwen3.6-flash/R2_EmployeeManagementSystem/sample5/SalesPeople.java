public class SalesPeople extends Employee {
  private double salary;
  private double amountOfSales;
  private double commissionPercentage;

  public SalesPeople() {
  }

  public double getSalary() {
    return salary;
  }

  public void setSalary(double salary) {
    this.salary = salary;
  }

  public double getAmountOfSales() {
    return amountOfSales;
  }

  public void setAmountOfSales(double amountOfSales) {
    this.amountOfSales = amountOfSales;
  }

  public double getCommissionPercentage() {
    return commissionPercentage;
  }

  public void setCommissionPercentage(double commissionPercentage) {
    this.commissionPercentage = commissionPercentage;
  }

  public double getTotalCommission() {
    return Math.round(amountOfSales * commissionPercentage * 100.0) / 100.0;
  }

  @Override
  public double calculateSalary() {
    double salary = this.salary + amountOfSales * commissionPercentage;
    return Math.round(salary * 100.0) / 100.0;
  }

  @Override
  public double calculateCommission() {
    return Math.round(amountOfSales * commissionPercentage * 100.0) / 100.0;
  }

  @Override
  public double calculateHolidayPremium() {
    return 0;
  }
}
