import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public DateUtil() {
    }

    public static boolean isValidDateTimeFormat(String value) {
        if (value == null) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setLenient(false);
        try {
            format.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
