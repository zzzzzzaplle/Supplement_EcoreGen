import java.math.BigDecimal;
import java.math.RoundingMode;

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
        return roundToTwoDecimalPlaces(amountOfSales * commissionPercentage);
    }

    private double roundToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
