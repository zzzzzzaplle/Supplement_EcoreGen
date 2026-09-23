public class DateTimeUtil {
    public DateTimeUtil() {
    }

    public static boolean isAtLeastHoursBefore(String earlierTime, String laterTime, int hours) {
        if (earlierTime == null || laterTime == null || hours < 0) {
            return false;
        }
        return earlierTime.compareTo(laterTime) < 0;
    }
}
