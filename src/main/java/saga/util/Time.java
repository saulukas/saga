
package saga.util;

import java.util.Calendar;

public class Time implements Comparable<Time>
{
    private final int hours;
    private final int minutes;
    private final int seconds;
    private final int millis;

    public Time(int hours, int minutes, int seconds, int millis)
    {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.millis = millis;
    }

    public Time(int hours, int minutes, int seconds)
    {
        this(hours, minutes, seconds, 0);
    }

    public Time(int hours, int minutes)
    {
        this(hours, minutes, 0, 0);
    }

    public Time()
    {
        this(Calendar.getInstance());
    }

    public Time(Calendar calendar)
    {
        this.hours = calendar.get(Calendar.HOUR_OF_DAY);
        this.minutes = calendar.get(Calendar.MINUTE);
        this.seconds = calendar.get(Calendar.SECOND);
        this.millis = calendar.get(Calendar.MILLISECOND);
    }

    public Time(long unixMillis)
    {
        this(DateUtils.unixMillis2Calendar(unixMillis));
    }

    public Time(java.util.Date javaUtilDate)
    {
        this(javaUtilDate.getTime());
    }

    public static Time valueOf(java.util.Date utilDate)
    {
        if (utilDate == null)
            return null;
        return new Time(utilDate);
    }

    public int getHours()
    {
        return hours;
    }

    public int getMinutes()
    {
        return minutes;
    }

    public int getSeconds()
    {
        return seconds;
    }

    public int getMillis()
    {
        return millis;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (!(o instanceof Time))
            return false;
        Time time = (Time) o;
        return hours == time.hours
            && minutes == time.minutes
            && seconds == time.seconds
            && millis == time.millis;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 29 * hash + this.hours;
        hash = 29 * hash + this.minutes;
        hash = 29 * hash + this.seconds;
        hash = 29 * hash + this.millis;
        return hash;
    }

    @Override
    public int compareTo(Time time)
    {
        if (time == null)
            return 1;
        if (time == this)
            return 0;
        int sign = hours - time.hours;
        if (sign != 0)
            return sign;
        sign = minutes - time.minutes;
        if (sign != 0)
            return sign;
        sign = seconds - time.seconds;
        if (sign != 0)
            return sign;
        return millis - time.millis;
    }

    public String toStringWithMillis()
    {
        return toString()
            + "." + (millis / 100 % 10)
            + (millis / 10 % 10)
            + (millis / 1 % 10);
    }

    @Override
    public String toString()
    {
        return "" + (hours / 10) + (hours % 10)
            + ":" + (minutes / 10) + (minutes % 10)
            + ":" + (seconds / 10) + (seconds % 10);
    }

}
