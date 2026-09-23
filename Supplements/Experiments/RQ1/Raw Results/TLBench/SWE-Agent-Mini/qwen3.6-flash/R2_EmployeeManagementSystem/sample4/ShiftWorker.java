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

    @Override
    public void setDepartment(String department) {
        if (department != null && !department.equals("DELIVERY")) {
            throw new IllegalArgumentException("Shift workers can only belong to the DELIVERY department");
        }
        super.setDepartment(department);
    }

    public double calculateHolidayPremium() {
        return roundToTwoDecimalPlaces(holidayPremium);
    }

    private double roundToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
