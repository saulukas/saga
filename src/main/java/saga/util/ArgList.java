package saga.util;


import java.util.List;

import static java.util.Collections.emptyList;
import static saga.util.ListUtils.listOf;

public class ArgList extends HeadList<String> {

    ArgList(List<String> list) {
        super(list);
    }

    public static ArgList of(String[] args) {
        return new ArgList(args == null ? emptyList() : listOf(args));
    }
}
