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
        if (department == null || !"DELIVERY".equals(department)) {
            return;
        }
        super.setDepartment(department);
    }

    public double calculateHolidayPremium() {
        return roundTwoDecimals(holidayPremium);
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
