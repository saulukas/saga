package saga.util;

import java.util.Arrays;
import java.util.Collection;

import static java.util.stream.Collectors.joining;

public class TextUtils {

    public static String fillChar(char c, int count) {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += c;
        }
        return result;
    }

    public static String alignRight(Object s, int width) {
        return alignLeft(s, width, ' ');
    }

    public static String alignRight(Object s, int width, char fillChar) {
        String result = "" + s;
        for (int i = result.length(); i < width; i++) {
            result = fillChar + result;
        }
        return result;
    }

    public static String alignLeft(Object s, int width) {
        return alignLeft(s, width, ' ');
    }

    public static String alignLeft(Object s, int width, char fillChar) {
        String result = "" + s;
        for (int i = result.length(); i < width; i++) {
            result += fillChar;
        }
        return result;
    }

    public static String add1000seps(Object number) {
        if (number == null) {
            return "";
        }
        String str = number.toString();
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            if (i > 0 && i % 3 == 0) {
                result = "," + result;
            }
            result = str.charAt(str.length() - 1 - i) + result;
        }
        return result;
    }

    public static String joinUsing(String delimiter, String... parts) {
        return joinUsing(delimiter, Arrays.asList(parts));
    }

    public static String joinUsing(String delimiter, Collection<String> parts) {
        return parts.stream().collect(joining(delimiter));
    }

}
