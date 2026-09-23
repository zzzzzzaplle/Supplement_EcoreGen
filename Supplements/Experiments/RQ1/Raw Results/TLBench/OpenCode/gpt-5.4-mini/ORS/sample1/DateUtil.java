import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public DateUtil() {
    }

    public static Date combineDateAndTime(Date date, String time) {
        if (date == null || time == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        format.setLenient(false);
        String datePart = new SimpleDateFormat("yyyy-MM-dd").format(date);
        try {
            return format.parse(datePart + " " + time);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseDateTime(String value) {
        if (value == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        format.setLenient(false);
        try {
            return format.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatMonth(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM").format(date);
    }
}
