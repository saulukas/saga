package saga.tools.unix_win;

import java.io.File;
import saga.tools.Tool;
import saga.util.ArgList;

import static saga.util.SystemOut.println;
import static saga.util.Equal.equal;
import static saga.util.SystemOut.print;

public class UnixWinTool extends Tool {

    public UnixWinTool() {
        super("unix-win", "Outputs operating system dependent info");
    }

    void printUsage() {
        println(name + " (c) saga 2019");
        println("");
        println("    " + oneLineDescription);
        println("");
        println("Parameters:");
        println("");
        println("   dir-symbol         - prints symbol used to separate directories in a single path");
        println("   path-separator     - prints separator of directories in a PATH variable");
        println("");
    }

    @Override
    public int run(String[] argArray) throws Exception {
        ArgList args = ArgList.of(argArray);
        if (args.isEmpty()) {
            printUsage();
            return 0;
        }
        if (equal(args.head(), "dir-symbol")) {
            print(File.separator);
            return 0;
        }
        if (equal(args.head(), "path-separator")) {
            print(File.pathSeparator);
            return 0;
        }
        printUsage();
        return -1;
    }
}
