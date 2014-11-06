package saga.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Script {

    public static void print(Object o) {
        System.out.print(o);
    }

    public static void println(Object o) {
        System.out.println(o);
    }

    public static <T> LinkedList<T> asLinkedList(List<T> list) {
        return new LinkedList<>(list);
    }

    public static <T> LinkedList<T> asLinkedList(T... elements) {
        return asLinkedList(Arrays.asList(elements));
    }
}
