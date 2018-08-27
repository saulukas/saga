package saga.tabs;

import java.io.*;
import saga.Tool;
import saga.util.ArgList;
import saga.util.TextUtils;

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

        int tabSize;
        String tabSizeText = args.removeHead();
        try {
            tabSize = Integer.parseInt(tabSizeText);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Expected tab size, but found: " + tabSizeText);
        }
        if (tabSize < 1) {
            throw new RuntimeException("Tab size must be >= 1");
        }

        convertTabsToSpaces(tabSize);

        return 0;
    }

    static private void convertTabsToSpaces(int tabSize) {
        ex(() -> {
            Reader input = new InputStreamReader(System.in);
            Writer output = new OutputStreamWriter(System.out);
            convertTabsToSpaces(tabSize, input, output);
            output.flush();
        });
    }

    static void convertTabsToSpaces(int tabSize, Reader input, Writer output) throws IOException {
        int col = 0;
        int symbol = input.read();
        while (symbol != -1) { // end-of-file
            if (symbol == '\r' || symbol == '\n') {
                output.write(symbol);
                col = 0;
            } else if (symbol == '\t') {
                int spaceCount = tabSize - (col % tabSize);
                output.write(TextUtils.fillChar(' ', spaceCount));
                col += spaceCount;
            } else {
                output.write(symbol);
                col += 1;
            }
            symbol = input.read();
        }
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
