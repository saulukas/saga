package saga;

import static saga.util.SystemOut.println;

public abstract class Tool {

    public final String name;
    public final String oneLineDescription;

    public Tool(String name, String oneLineDescription) {
        this.name = name;
        this.oneLineDescription = oneLineDescription;
    }

    public abstract int run(String[] args) throws Exception;

    public static void runAndExit(Tool tool, String[] args) {
        try {
            System.exit(tool.run(args));
        } catch (Throwable t) {
            println(".");
            println(".   ERROR: " + t);
            println(".");
            t.printStackTrace();
            System.exit(-1);
        }
    }
}
