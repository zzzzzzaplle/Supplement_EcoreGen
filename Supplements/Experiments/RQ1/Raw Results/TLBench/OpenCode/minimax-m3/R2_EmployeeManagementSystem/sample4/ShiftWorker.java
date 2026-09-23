public class ShiftWorker extends Worker {
    private double holidayPremium;

    public ShiftWorker() {
        super();
        super.setDepartment(DepartmentType.DELIVERY.name());
    }

    public double getHolidayPremium() {
        return holidayPremium;
    }

    public void setHolidayPremium(double holidayPremium) {
        this.holidayPremium = holidayPremium;
    }

    @Override
    public void setDepartment(String department) {
        if (department != null && DepartmentType.DELIVERY.name().equalsIgnoreCase(department)) {
            super.setDepartment(DepartmentType.DELIVERY.name());
        }
    }

    public double calculateHolidayPremium() {
        return Math.round(holidayPremium * 100.0) / 100.0;
    }
}
