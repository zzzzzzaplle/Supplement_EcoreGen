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
        // Shift workers can only belong to Delivery department
        super.setDepartment("DELIVERY");
    }

    public double calculateHolidayPremium() {
        return holidayPremium;
    }
}
