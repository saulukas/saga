package saga.util;

import java.util.concurrent.Callable;

public class ExceptionUtils {

    public static RuntimeException exception(String message) {
        return new RuntimeException(message);
    }

    public static RuntimeException exception(String message, Throwable cause) {
        return new RuntimeException(message, cause);
    }

    public static RuntimeException exception(Throwable cause) {
        return new RuntimeException(cause);
    }

    public static void ex(RunnableWithExceptions runnable) {
        try {
            runnable.run();
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T ex(Callable<T> callable) {
        try {
            return callable.call();
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
