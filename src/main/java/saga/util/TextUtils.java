package saga.util;

public class TextUtils {

    public static String fillChar(char c, int count) {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += c;
        }
        return result;
    }

    public static String alignRight(String s, int width) {
        String result = "" + s;
        for (int i = result.length(); i < width; i++) {
            result = " " + result;
        }
        return result;
    }

    public static String alignLeft(String s, int width) {
        String result = "" + s;
        for (int i = result.length(); i < width; i++) {
            result = result + " ";
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
}
