package saga.util;

public class ExceptionUtils {

    public static RuntimeException exception(String message) {
        return new RuntimeException(message);
    }

    public static RuntimeException exception(String message, Throwable cause) {
        return new RuntimeException(message, cause);
    }

}
