
package saga.date;

import java.util.Calendar;

public class DateUtils
{
    public static long getDaysBetween(Date d1, Date d2)
    {
        return getDaysBetween(d2.getCalendar(), d1.getCalendar());
    }

    public static long getYearsBetween(Timestamp d1, Timestamp d2)
    {
        return getYearsBetween(d2.getCalendar(), d1.getCalendar());
    }

    public static Calendar unixMillis2Calendar(long unixMillis)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixMillis);
        return calendar;
    }

    private static long getDaysBetween(Calendar d1, Calendar d2)
    {
        int sign = 1;
        if (d1.after(d2))
        {
            Calendar swap = d1;
            d1 = d2;
            d2 = swap;
            sign = -1;
        }
        long days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
        int y2 = d2.get(Calendar.YEAR);
        if (d1.get(Calendar.YEAR) != y2)
        {
            d1 = (Calendar) d1.clone();
            do
            {
                days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
                d1.add(Calendar.YEAR, 1);
            }
            while (d1.get(Calendar.YEAR) != y2);
        }
        return days * sign;
    }

    private static long getYearsBetween(Calendar d1, Calendar d2)
    {
        int sign = 1;
        if (d1.after(d2))
        {
            Calendar swap = d1;
            d1 = d2;
            d2 = swap;
            sign = -1;
        }
        long years = 0;
        d1.add(Calendar.YEAR, 1);
        while (d2.compareTo(d1) >= 0)
        {
            d1.add(Calendar.YEAR, 1);
            years++;
        }
        return years * sign;
    }

    public static Timestamp trimTrailingSeconds(Timestamp t)
    {
        return new Timestamp(t.getDate(), new Time(t.getHours(), t.getMinutes()));
    }
}
