package su.Jalapeno.AntiSpam.Util;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class DateUtil {
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return cal.getTime();
	}

	public static int DiffInDays(Date dateFirst, Date dateSecond) {
		int days = Days.daysBetween(new DateTime(dateFirst), new DateTime(dateSecond)).getDays();
		return days;
	}
}