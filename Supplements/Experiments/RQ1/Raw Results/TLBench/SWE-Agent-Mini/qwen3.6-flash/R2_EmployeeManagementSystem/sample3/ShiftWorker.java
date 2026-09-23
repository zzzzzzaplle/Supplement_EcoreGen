import java.math.BigDecimal;
import java.math.RoundingMode;

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
            throw new IllegalArgumentException("Shift worker can only belong to the Delivery department");
        }
        super.setDepartment(department);
    }

    public double calculateHolidayPremium() {
        return BigDecimal.valueOf(holidayPremium).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double getCalculatedWeeklySalary() {
        BigDecimal weeklySalaryBD = BigDecimal.valueOf(getWeeklySalary());
        BigDecimal holidayPremiumBD = BigDecimal.valueOf(holidayPremium);
        return weeklySalaryBD.add(holidayPremiumBD).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
