package saga.srt;

import saga.Tool;
import saga.util.ArgList;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static saga.util.Equal.equal;
import static saga.util.ExceptionUtils.exception;
import static saga.util.SystemOut.print;
import static saga.util.SystemOut.println;
import static saga.util.TextUtils.joinUsing;

public class SrtTool extends Tool {

    public SrtTool() {
        super("srt", "Movie subtitles SRT file");
    }

    @Override
    public int run(String[] argArray) throws Exception {
        ArgList args = ArgList.of(argArray);
        if (args.isEmpty()) {
            printUsage();
            return 0;
        }
        if (equal(args.head(), "shift")) {
            args.removeHead();
            return shiftTime(args);
        }
        printUsage();
        return -1;
    }

    void printUsage() {
        println(name + " (c) saga 2022");
        println("");
        println("    " + oneLineDescription);
        println("");
        println("Parameters:");
        println("");
        println("    shift <secs> <srt-file> - shift time by given seconds ('-' - backwards)");
        println("");
    }

    int shiftTime(ArgList args) throws Exception {
        if (args.isEmpty()) {
            throw exception("Expected <seconds>.");
        }
        int shiftBySecs = Integer.parseInt(args.removeHead());
        if (args.isEmpty()) {
            throw exception("Expected <srt-file>.");
        }
        String filename = args.removeHead();

        doShiftFileTime(shiftBySecs, filename);
        return 0;
    }

    private void doShiftFileTime(int shiftBySecs, String filename) throws Exception {
        LineNumberReader reader = new LineNumberReader(
                new InputStreamReader(new FileInputStream(filename), "UTF-8")
        );
        PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(System.out, "UTF-8")
        );
        while (true) {
            String line = skipEmptyLines(reader);
            if (line == null) {
                break;
            }
            String nrString = line;
            String timeString = shiftTimeLine(shiftBySecs, reader.readLine());
            String subtitleString = reader.readLine();
            line = reader.readLine();
            while (line != null && !line.isEmpty()) {
                subtitleString += "\n" + line;
                line = reader.readLine();
            }
            writer.println(nrString);
            writer.println(timeString);
            writer.println(subtitleString);
            writer.println();
        }
        writer.flush();
        writer.close();
        reader.close();
    }

    private String skipEmptyLines(LineNumberReader reader) throws Exception {
        String line;
        do {
            line = reader.readLine();
        } while (line != null && line.isEmpty());
        return line;
    }

    private String shiftTimeLine(int shiftBySecs, String line) {
        String[] parts = line.split(" ");
        return shiftSingleTime(parts[0], shiftBySecs)
                + " " + parts[1] + " "
                + shiftSingleTime(parts[2], shiftBySecs);
    }

    private String shiftSingleTime(String time, int shiftBySecs) {
        String[] parts = time.split(",");
        return LocalTime.parse(parts[0]).plusSeconds(shiftBySecs)
                .format(DateTimeFormatter.ISO_LOCAL_TIME)
                + ","
                + parts[1];
    }

}
