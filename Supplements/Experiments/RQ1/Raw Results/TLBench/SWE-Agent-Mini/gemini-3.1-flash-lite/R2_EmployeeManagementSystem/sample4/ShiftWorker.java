public class ShiftWorker extends Worker {
    private double holidayPremium;
    public ShiftWorker() {}
    public double getHolidayPremium() { return holidayPremium; }
    public void setHolidayPremium(double holidayPremium) { this.holidayPremium = holidayPremium; }
    @Override
    public void setDepartment(String department) {
        if ("DELIVERY".equals(department)) {
            super.setDepartment(department);
        } else {
            throw new IllegalArgumentException("Shift workers can only belong to the Delivery department.");
        }
    }
    public double calculateHolidayPremium() { return holidayPremium; }
}
