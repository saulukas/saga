
package saga.util;

public class Equal
{
    public static boolean areEqual(Object a, Object b)
    {
        if (a == b)
            return true;
        if (a == null || b == null)
            return false;
        return a.equals(b);
    }

    public static <T extends Comparable<T>> int compare(T a, T b)
    {
        if (a == b)
            return 0;
        if (a == null)
            return -1;
        if (b == null)
            return 1;
        return a.compareTo(b);
    }
}
