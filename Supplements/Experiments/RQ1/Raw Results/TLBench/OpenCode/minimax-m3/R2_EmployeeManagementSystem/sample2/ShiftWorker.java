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
        super.setDepartment(department);
    }

    public double calculateHolidayPremium() {
        double premium = holidayPremium;
        return Math.round(premium * 100.0) / 100.0;
    }
}
