import java.math.RoundingMode;
import java.math.BigDecimal;

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
        if (!"DELIVERY".equals(department)) {
            throw new IllegalArgumentException("ShiftWorker can only belong to DELIVERY department");
        }
        super.setDepartment(department);
    }

    public double calculateHolidayPremium() {
        BigDecimal bd = new BigDecimal(holidayPremium);
        return bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
