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
        if (!"Delivery".equals(department) && !"DELIVERY".equals(department)) {
            throw new IllegalArgumentException("ShiftWorker can only belong to the Delivery department");
        }
        super.setDepartment(department);
    }

    public double calculateHolidayPremium() {
        return holidayPremium;
    }
}
