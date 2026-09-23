public class OffShiftWorker extends Worker {
  private boolean weekendPermit;
  private boolean officialHolidayPermit;

  public OffShiftWorker() {
  }

  public boolean isWeekendPermit() {
    return weekendPermit;
  }

  public void setWeekendPermit(boolean weekendPermit) {
    this.weekendPermit = weekendPermit;
  }

  public boolean isOfficialHolidayPermit() {
    return officialHolidayPermit;
  }

  public void setOfficialHolidayPermit(boolean officialHolidayPermit) {
    this.officialHolidayPermit = officialHolidayPermit;
  }
}
