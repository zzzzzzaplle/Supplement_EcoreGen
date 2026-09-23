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
        if (department != null && department.equalsIgnoreCase(DepartmentType.DELIVERY.name())) {
            super.setDepartment(DepartmentType.DELIVERY.name());
        } else {
            super.setDepartment(DepartmentType.DELIVERY.name());
        }
    }

    public double calculateHolidayPremium() {
        return holidayPremium;
    }
}
