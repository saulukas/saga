package saga.tools.yaml;

import java.io.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.events.ScalarEvent;
import saga.tools.Tool;
import saga.util.ArgList;

import static saga.util.ExceptionUtils.ex;
import static saga.util.SystemOut.println;

public class YamlTool extends Tool {

    public YamlTool() {
        super("yaml", "Reads YAML file and writes to stdout Java properties");
    }

    @Override
    public int run(String[] argArray) {
        ArgList args = ArgList.of(argArray);
        if (args.isEmpty()) {
            printUsage();
            return 0;
        }

        String yamlFileName = args.removeHead();

        ex(() -> {
            Reader input = new InputStreamReader(new FileInputStream(yamlFileName), "UTF-8");
            Writer output = new OutputStreamWriter(System.out);
            yamlToProperties(input, output);
            output.flush();
        });

        return 0;
    }

    void printUsage() {
        println(name + " (c) saga 2017");
        println("");
        println("    " + oneLineDescription);
        println("");
        println("Parameters:");
        println("");
        println("    properties-file.yml");
        println("");
    }

    void yamlToProperties(Reader input, Writer output) throws IOException {
        YamlEventSource events = new YamlEventSource(new Yaml().parse(input).iterator());
        events.nextScalarNotUpwards(0);
        while (events.lastScalar().isPresent()) {
            doYamlToProperties(events, "", output);
            events.nextScalarNotUpwards(0);
        }
    }

    void doYamlToProperties(YamlEventSource input, String path, Writer output)
            throws IOException {
        ScalarEvent scalar = input.lastScalar().get();
        int column = input.lastColumnOrZero();
        while (input.hasNext()) {
            input.nextScalarNotUpwards(column);
            if (!input.hasNext()) {
                return;
            }
            if (input.lastColumnOrZero() < column) {
                if (input.separatorFound) {
                    writeLineSeparator(output);
                }
                input.separatorFound = false;
                writeProperty(output, path, scalar.getValue());
                return;
            }
            // ScalarEvent found
            if (input.lastColumnOrZero() == column) {
                scalar = input.lastScalar().get();
                continue;
            }
            // (events.lastColumnOrZero() > column)  process child
            String newPath = path + (path.isEmpty() ? "" : ".") + scalar.getValue();
            doYamlToProperties(input, newPath, output);
            if (input.lastColumnOrZero() < column) {
                return;
            }
            if (input.lastColumnOrZero() == column) {
                if (input.lastScalar().isPresent()) {
                    scalar = input.lastScalar().get();
                }
                continue;
            }
            throw new RuntimeException("Internal YAML error 45");
        }
    }

    void writeProperty(Writer output, String path, String value) throws IOException {
        output.write(path + "=" + value.replace("\n", "\\n\\\n") + "\n");
        output.flush();
    }

    void writeLineSeparator(Writer output) throws IOException {
        output.write("\n");
        output.flush();
    }

}
