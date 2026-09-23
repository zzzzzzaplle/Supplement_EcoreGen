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
        super.setDepartment(department);
    }

    public double calculateHolidayPremium() {
        return holidayPremium;
    }
}
