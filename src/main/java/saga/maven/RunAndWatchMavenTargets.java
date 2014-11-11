package saga.maven;

import java.io.File;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.LinkedList;
import java.util.List;
import saga.util.Scanner;
import saga.util.Script;
import static saga.util.Script.println;
import saga.util.SimpleClassName;

public class RunAndWatchMavenTargets extends Script {

    public String name() {
        return SimpleClassName.of(getClass());
    }

    public void printUsage() {
        println("");
        println(name() + " 1.00 (c) saga 2014");
        println("");
        println("    Runs given 'command' in a separate process and watches 'target'");
        println("    subdirectories of given maven 'modules' for changes.");
        println("    If changes are detected 'command' process is requested to exit");
        println("    by closing its standard input stream and restarted.");
        println("");
        println("    Command is expected to block on reading its input stream and");
        println("    exit if any input (like end-of-file) is received.");
        println("");
        println("    Press ENTER to exit " + name() + ".");
        println("");
        println("Paramters:");
        println("");
        println("   module[:module]  all|class  command [args]");
        println("");
        println("   module[:module]  - list of colon separated maven module directories");
        println("   all|class        - check all files for changes or '.class' files only");
        println("   command          - command with args to start and restart if changes detected");
        println("");
    }

    public Params parseArgs(List<String> args) {
        LinkedList<String> tail = asLinkedList(args);
        if (tail.size() < 3) {
            return null;
        }
        Params params = new Params();
        params.modules = asList(tail.removeFirst().split(":"));
        params.classesOnly = tail.removeFirst().equals("class");
        params.commandAndArgs = tail;
        return params;
    }

    public static class Params {

        List<String> modules;
        boolean classesOnly;
        List<String> commandAndArgs;

        String[] commandAndArgsArray() {
            return commandAndArgs.toArray(new String[0]);
        }

        @Override
        public String toString() {
            return SimpleClassName.of(this.getClass()) + "("
                    + "modules=" + modules
                    + ", classOnly=" + classesOnly
                    + ", commandAndArgs=" + commandAndArgs
                    + ")";
        }

    }

    public static void main(String[] args) throws Exception {
        new RunAndWatchMavenTargets().execute(asList(args));
    }

    private void execute(List<String> args) throws Exception {
        Params params = parseArgs(args);
        if (params == null) {
            printUsage();
            return;

        }
        executeWith(params);
    }

    private void executeWith(final Params params) throws Exception {
        final Process[] process = new Process[]{startCommandProcess(params)};
        int scanIntervalSeconds = 1;
        List<File> dirs = targetDirsOf(params.modules);
        Scanner.watch(scanIntervalSeconds, dirs, new Scanner.BulkListener() {
            @Override
            public void filesChanged(List<String> fileNames) throws Exception {
                onFilesChanged(fileNames, params, process);
            }
        });
        stopCommandProcess(process[0]);
    }

    private static void onFilesChanged(List<String> fileNames, Params params, Process[] process) throws Exception {
        List<String> changedFiles = changedFilesOf(fileNames, params.classesOnly);
        if (!changedFiles.isEmpty()) {
            stopCommandProcess(process[0]);
            process[0] = startCommandProcess(params);
        }
    }

    private static List<File> targetDirsOf(List<String> modules) {
        ArrayList<File> dirs = new ArrayList<>();
        for (String module : modules) {
            dirs.add(new File(module + "/target"));
        }
        return dirs;
    }

    private static List<String> changedFilesOf(List<String> fileNames, boolean classesOnly) {
        if (!classesOnly) {
            return fileNames;
        }
        List<String> changedFiles = new ArrayList<>();
        for (String fileName : fileNames) {
            if (fileName.endsWith(".class")) {
                changedFiles.add(fileName);
            }
        }
        return changedFiles;
    }

    private static Process startCommandProcess(Params params) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(params.commandAndArgs);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        return processBuilder.start();
    }

    private static void stopCommandProcess(Process process) throws Exception {
        process.getOutputStream().close();
        process.waitFor();
    }
}
