package saga.util;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

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
        } catch (RuntimeException rt) {
            throw rt;
        } catch (Error e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static <T> Consumer<? super T> ex(Consumer<? super T> action) {
        return (t) -> ex(() -> action.accept(t));
    }

    public static <T> T ex(Callable<T> callable) {
        try {
            return callable.call();
        } catch (RuntimeException rt) {
            throw rt;
        } catch (Error e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
