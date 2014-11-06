
package saga.date;

import java.util.Calendar;

public class Date implements Comparable<Date>
{
    private final int year;
    private final int month;
    private final int day;

    public Date(int year, int month, int day)
    {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public Date()
    {
        this(Calendar.getInstance());
    }

    public Date(Calendar calendar)
    {
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH) + 1;
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public Date(long unixMillis)
    {
        this(DateUtils.unixMillis2Calendar(unixMillis));
    }

    public Date(java.util.Date javaUtilDate)
    {
        this(javaUtilDate.getTime());
    }

    public static Date valueOf(java.util.Date javaUtilDate)
    {
        if (javaUtilDate == null)
            return null;
        return new Date(javaUtilDate);
    }

    public int getYear()
    {
        return year;
    }

    public int getMonth()
    {
        return month;
    }

    public int getDay()
    {
        return day;
    }

    public Calendar getCalendar()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar;
    }

    public long getUnixMillis()
    {
        return getCalendar().getTimeInMillis();
    }

    public java.util.Date getJavaUtilDate()
    {
        return new java.util.Date(getUnixMillis());
    }

    public java.sql.Date getSqlDate()
    {
        return new java.sql.Date(getUnixMillis());
    }

    public Date getWithAddedDays(int daysDelta)
    {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, daysDelta);
        return new Date(calendar);
    }

    public Date getWithAddedMonth(int monthDelta)
    {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.MONTH, monthDelta);
        return new Date(calendar);
    }

    public Date getWithAddedMinutes(int minuteDelta)
    {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.MINUTE, minuteDelta);
        return new Date(calendar);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (!(o instanceof Date))
            return false;
        Date date = (Date) o;
        return year == date.year && month == date.month && day == date.day;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + this.year;
        hash = 97 * hash + this.month;
        hash = 97 * hash + this.day;
        return hash;
    }

    @Override
    public int compareTo(Date date)
    {
        if (date == null)
            return 1;
        if (date == this)
            return 0;
        int sign = year - date.year;
        if (sign != 0)
            return sign;
        sign = month - date.month;
        if (sign != 0)
            return sign;
        return day - date.day;
    }

    @Override
    public String toString()
    {
        return year + "." + (month / 10) + (month % 10) + "." + (day / 10) + (day
            % 10);
    }
}
