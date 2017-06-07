package saga.tabs;

import java.io.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.events.ScalarEvent;
import saga.Tool;
import saga.util.ArgList;
import saga.yaml.YamlEventSource;

import static saga.util.ExceptionUtils.ex;
import static saga.util.SystemOut.println;

public class TabsTool extends Tool {

    public TabsTool() {
        super("tabs", "Convert tab-characters to spaces: from stdin to stdout");
    }

    @Override
    public int run(String[] argArray) {
        ArgList args = ArgList.of(argArray);
        if (args.isEmpty()) {
            printUsage();
            return 0;
        }

        int spaceCount;
        String spaceCountText = args.removeHead();
        try {
            spaceCount = Integer.parseInt(spaceCountText);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Expected space character count, but found: " + spaceCountText);
        }
        if (spaceCount < 1) {
            throw new RuntimeException("Space character count must be >= 1");
        }

        convertTabsToSpaces(spaceCount);

        return 0;
    }

    private void convertTabsToSpaces(int spaceCount) {
        ex(() -> {
            String spaces = "";
            for (int i = 0;  i < spaceCount;  i++) {
                spaces += " ";
            }
            Reader input = new InputStreamReader(System.in);
            Writer output = new OutputStreamWriter(System.out);
            int symbol = input.read();
            while (symbol != -1) { // end-of-file
                if (symbol == '\t') {
                    output.write(spaces);
                } else {
                    output.write(symbol);
                }
                symbol = input.read();
            }
            output.flush();
        });
    }

    void printUsage() {
        println(name + " (c) saga 2017");
        println("");
        println("    " + oneLineDescription);
        println("");
        println("Parameters:");
        println("");
        println("    space-count");
        println("");
    }

}
