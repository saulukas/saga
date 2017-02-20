package saga.util;

import java.util.List;

import static java.util.Collections.emptyList;
import static saga.util.ExceptionUtils.exception;
import static saga.util.ListUtils.listOf;

public class HeadList<T> {

    private final List<T> list;

    HeadList(List<T> list) {
        this.list = list;
    }

    public static <T> HeadList<T> of(T[] args) {
        return new HeadList(args == null ? emptyList() : listOf(args));
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public T head() {
        if (isEmpty()) {
            throw exception(HeadList.class.getSimpleName() + " is empty");
        }
        return list.get(0);
    }

    public void removeHead() {
        if (isEmpty()) {
            throw exception(getClass().getSimpleName() + " is empty.");
        }
        list.remove(0);
    }
}
