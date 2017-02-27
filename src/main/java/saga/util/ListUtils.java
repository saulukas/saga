package saga.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.emptyList;

public class ListUtils {

    public static <T> LinkedList<T> asLinkedList(List<T> list) {
        return new LinkedList<>(list);
    }

    public static <T> LinkedList<T> asLinkedList(T... elements) {
        return asLinkedList(Arrays.asList(elements));
    }

    public static <T> List<T> listOf(T[] array) {
        return new ArrayList<>(Arrays.asList(array));
    }

    public static String[] arrayOf(List<String> list) {
        return list.toArray(new String[list.size()]);
    }

    public static <T> T[] arrayOf(T... array) {
        return array;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> newList() {
        return (List<T>) new ArrayList<>();
    }

}
