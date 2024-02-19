package saga;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import saga.tools.Tool;
import saga.tools.cvslog.CVSLogFormatterTool;
import saga.tools.file.SortByDateTool;
import saga.tools.ip.IPTool;
import saga.tools.jhat.PrintJhatInstanceCountDiffTool;
import saga.tools.jwplayer.JWPlayerTool;
import saga.tools.linecount.LineCountTool;
import saga.tools.linecount.ListFileTypesTool;
import saga.tools.rename.RenameFilesTool;
import saga.tools.srt.SrtTool;
import saga.tools.tabs.TabsTool;
import saga.tools.totp.TotpTool;
import saga.tools.unix_win.UnixWinTool;
import saga.tools.watch.WatchFilesTool;
import saga.tools.yaml.YamlTool;
import static saga.util.ListUtils.arrayOf;
import static saga.util.ListUtils.listOf;
import static saga.util.SystemOut.print;
import static saga.util.SystemOut.println;
import static saga.util.TextUtils.alignLeft;

public class SagaTools {

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
        registerTool(new IPTool());
        registerTool(new YamlTool());
        registerTool(new TabsTool());
        registerTool(new JWPlayerTool());
        registerTool(new WatchFilesTool());
        registerTool(new UnixWinTool());
        registerTool(new SrtTool());
        registerTool(new TotpTool());
    }

    static void registerTool(Tool tool) {
        name2toolMap.put(tool.name, tool);
    }

    static int maxToolNameLength() {
        int max = 0;
        for (Tool tool : name2toolMap.values()) {
            max = Math.max(max, tool.name.length());
        }
        return max;
    }

    static void printUsage() {
        println("SagaTools (c) saga 2022");
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
        int maxLength = maxToolNameLength();
        name2toolMap.values().forEach((tool) -> {
            print("\n    " + alignLeft(tool.name + " ", maxLength + 1, '.')
                    + ".... " + tool.oneLineDescription);
        });
    }

}
