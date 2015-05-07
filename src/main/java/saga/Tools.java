package saga;

import java.util.Map;
import java.util.TreeMap;
import saga.enumfiles.RenameFiles;
import saga.file.SortByDate;
import saga.linecount.LineCount;
import saga.linecount.ListFileTypes;
import static saga.util.SystemOut.print;
import static saga.util.SystemOut.println;
import static saga.util.TextUtils.alignLeft;

public class Tools {

    static int maxNameLength = 0;
    static Map<String, Tool> name2toolMap = new TreeMap<>();

    static void registerTool(Tool tool) {
        name2toolMap.put(tool.name, tool);
        maxNameLength = Math.max(maxNameLength, tool.name.length());
    }

    static void registerAllTools() {
        registerTool(new LineCount());
        registerTool(new ListFileTypes());
        registerTool(new RenameFiles());
        registerTool(new SortByDate());
    }
    
    private static void printAvailableTools() {
        for (Tool tool : name2toolMap.values()) {
            print("\n    " 
                    + alignLeft(tool.name + " ", maxNameLength + 1, '.') 
                    + ".... " 
                    + tool.oneLineDescription);
        }
    }

    public static void main(String[] args) {
        registerAllTools();
        if (args.length < 1) {
            println("SagaTools 1.00 (c) saga 2015");
            println("");
            println("Parameters / tools:");
            printAvailableTools();
            println("");
            println("");
            System.exit(0);
        }
        String toolName = args[0];
        Tool tool = name2toolMap.get(toolName);
        if (tool == null) {
            println(".");
            println(".    ERROR: saga tool with name '" + toolName + "' not found.");
            println(".");
            println("");
            println("Available tools:");
            printAvailableTools();
            println("");
            println("");
            System.exit(1);
        }
        String[] shiftedArgs = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            shiftedArgs[i - 1] = args[i];
        }
        Tool.runAndExit(tool, shiftedArgs);
    }

}
