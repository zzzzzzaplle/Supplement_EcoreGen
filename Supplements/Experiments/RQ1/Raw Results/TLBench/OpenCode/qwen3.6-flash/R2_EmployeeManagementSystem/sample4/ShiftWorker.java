import java.util.Set;

public class ShiftWorker extends Worker {
    private static final Set<String> ALLOWED_DEPARTMENTS = Set.of("Delivery");
    private double holidayPremium;

    public ShiftWorker() {
    }

    public double getHolidayPremium() {
        return holidayPremium;
    }

    public void setHolidayPremium(double holidayPremium) {
        this.holidayPremium = holidayPremium;
    }

    public double calculateHolidayPremium() {
        return holidayPremium;
    }

    public void setDepartment(String department) {
        if (!ALLOWED_DEPARTMENTS.contains(department)) {
            throw new IllegalArgumentException("ShiftWorker can only belong to the Delivery department");
        }
        super.setDepartment(department);
    }
}
