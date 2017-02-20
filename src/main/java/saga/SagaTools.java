package saga;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import saga.cvslog.CVSLogFormatterTool;
import saga.rename.RenameFilesTool;
import saga.file.SortByDateTool;
import saga.ip.IP4AddressTool;
import saga.jhat.PrintJhatInstanceCountDiffTool;
import saga.linecount.LineCountTool;
import saga.linecount.ListFileTypesTool;

import static saga.util.ListUtils.listOf;
import static saga.util.SystemOut.print;
import static saga.util.SystemOut.println;
import static saga.util.TextUtils.alignLeft;
import static saga.util.ListUtils.arrayOf;

public class SagaTools {

    static int maxNameLength = 0;
    static Map<String, Tool> name2toolMap = new TreeMap<>();

    public static void main(String[] args) {
        registerAllTools();
        List<String> argList = listOf(args);
        if (argList.isEmpty()) {
            printUsage();
            System.exit(0);
        }
        String toolName = argList.remove(0);
        Tool tool = name2toolMap.get(toolName);
        if (tool == null) {
            printErrorToolNotFound(toolName);
            System.exit(1);
        }
        try {
            System.exit(tool.run(arrayOf(argList)));
        } catch (Exception ex) {
            println(".");
            println(".   ERROR: " + ex);
            println(".");
            ex.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    static void registerAllTools() {
        registerTool(new LineCountTool());
        registerTool(new ListFileTypesTool());
        registerTool(new RenameFilesTool());
        registerTool(new SortByDateTool());
        registerTool(new CVSLogFormatterTool());
        registerTool(new PrintJhatInstanceCountDiffTool());
        registerTool(new IP4AddressTool());
    }

    static void registerTool(Tool tool) {
        name2toolMap.put(tool.name, tool);
        maxNameLength = Math.max(maxNameLength, tool.name.length());
    }

    static void printUsage() {
        println("SagaTools (c) saga 2017");
        println("");
        println("Parameters / tools:");
        printAvailableTools();
        println("");
        println("");
    }

    static void printErrorToolNotFound(String toolName) {
        println(".");
        println(".    ERROR: saga tool with name '" + toolName + "' not found.");
        println(".");
        println("");
        println("Available tools:");
        printAvailableTools();
        println("");
        println("");
    }

    static void printAvailableTools() {
        name2toolMap.values().forEach((tool) -> {
            print("\n    " + alignLeft(tool.name + " ", maxNameLength + 1, '.')
                    + ".... " + tool.oneLineDescription);
        });
    }

}
