import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public DateUtil() {
    }

    public static Date parseDateTime(String value) {
        if (value == null) {
            return null;
        }
        String[] patterns = new String[] {"yyyy-MM-dd HH:mm", "yyyy-MM-dd'T'HH:mm"};
        for (String pattern : patterns) {
            try {
                return new SimpleDateFormat(pattern).parse(value);
            } catch (ParseException e) {
            }
        }
        return null;
    }

    public static Date combineDateAndTime(Date date, String time) {
        if (date == null || time == null) {
            return null;
        }
        Date parsedTime = parseDateTime("1970-01-01 " + time);
        if (parsedTime == null) {
            return null;
        }
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTime(parsedTime);
        dateCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
        dateCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
        dateCalendar.set(Calendar.SECOND, 0);
        dateCalendar.set(Calendar.MILLISECOND, 0);
        return dateCalendar.getTime();
    }

    public static boolean isSameDay(Date first, Date second) {
        if (first == null || second == null) {
            return false;
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(first);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(second);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isInMonth(Date date, String month) {
        if (date == null || month == null || month.length() != 7) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String value = String.format("%04d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        return value.equals(month);
    }
}
