
package saga.util;

import java.util.Calendar;
import saga.util.Equal;

public class Timestamp implements Comparable<Timestamp>
{
    private final Date date;
    private final Time time;

    public Timestamp(Date date)
    {
        this(date, new Time(0, 0, 0));
    }

    public Timestamp(Time time)
    {
        this(new Date(0, 0, 0), time);
    }

    public Timestamp(Date date, Time time)
    {
        this.date = date;
        this.time = time;
    }

    public Timestamp()
    {
        this(Calendar.getInstance());
    }

    public Timestamp(Calendar calendar)
    {
        this.date = new Date(calendar);
        this.time = new Time(calendar);
    }

    public Timestamp(long unixMillis)
    {
        this(DateUtils.unixMillis2Calendar(unixMillis));
    }

    public Timestamp(java.util.Date javaUtilDate)
    {
        this(javaUtilDate.getTime());
    }

    public static Timestamp valueOf(java.util.Date javaUtilDate)
    {
        if (javaUtilDate == null)
            return null;
        return new Timestamp(javaUtilDate);
    }

    public static Timestamp valueOf(Date date)
    {
        if (date == null)
            return null;
        return new Timestamp(date);
    }

    public Date getDate()
    {
        return date;
    }

    public Time getTime()
    {
        return time;
    }

    public int getYear()
    {
        return date.getYear();
    }

    public int getMonth()
    {
        return date.getMonth();
    }

    public int getDay()
    {
        return date.getDay();
    }

    public int getHours()
    {
        return time.getHours();
    }

    public int getMinutes()
    {
        return time.getMinutes();
    }

    public int getSeconds()
    {
        return time.getSeconds();
    }

    public int getMillis()
    {
        return time.getMillis();
    }

    public Calendar getCalendar()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getYear(), getMonth() - 1, getDay(),
            getHours(), getMinutes(), getSeconds());
        calendar.set(Calendar.MILLISECOND, getMillis());
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

    public java.sql.Time getSqlTime()
    {
        return new java.sql.Time(getUnixMillis());
    }

    public java.sql.Timestamp getSqlTimestamp()
    {
        return new java.sql.Timestamp(getUnixMillis());
    }

    public Timestamp getWithAdded(int calendarField, int amount)
    {
        Calendar calendar = getCalendar();
        calendar.add(calendarField, amount);
        return new Timestamp(calendar);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (!(o instanceof Timestamp))
            return false;
        Timestamp timestamp = (Timestamp) o;
        return Equal.areEqual(date, timestamp.date)
            && Equal.areEqual(time, timestamp.time);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 59 * hash + (this.time != null ? this.time.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Timestamp timestamp)
    {
        if (timestamp == null)
            return 1;
        if (timestamp == this)
            return 0;
        int sign = Equal.compare(date, timestamp.date);
        if (sign != 0)
            return sign;
        return Equal.compare(time, timestamp.time);
    }

    @Override
    public String toString()
    {
        return date + " " + time;
    }

    public String toStringWithMillis ()
    {
        return date + " " + time.toStringWithMillis();
    }
}
