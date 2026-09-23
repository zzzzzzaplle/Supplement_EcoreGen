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
        BigDecimal salaryBD = BigDecimal.valueOf(salary);
        BigDecimal salesBD = BigDecimal.valueOf(amountOfSales);
        BigDecimal percentageBD = BigDecimal.valueOf(commissionPercentage);

        BigDecimal totalCommission = salesBD.multiply(percentageBD);
        return totalCommission.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
