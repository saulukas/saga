package saga.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import saga.util.Scanner;
import saga.util.Script;
import static saga.util.Script.println;
import saga.util.SimpleClassName;

public class RunAndWatchMavenSources extends Script {

    public String name() {
        return SimpleClassName.of(getClass());
    }

    public void printUsage() {
        println("");
        println(name() + " 1.00 (c) saga 2014");
        println("");
        println("    Runs given 'command' in a separate process and watches 'src'");
        println("    subdirectories of given maven 'modules' for changes.");
        println("    If changes are detected 'mvn test-compile' is executed for affected 'modules'.");
        println("    If '.java' files were changed or 'all' changes are being watched then 'command'");
        println("    process is requested to exit by closing its input stream and restarted.");
        println("");
        println("    Command is expected to block on reading its input stream and");
        println("    exit if any input (like end-of-file) is received.");
        println("");
        println("    Press ENTER to exit " + name() + ".");
        println("");
        println("Paramters:");
        println("");
        println("   module[:module]  all|java  command [args]");
        println("");
        println("   module[:module]  - list of colon separated maven module directories");
        println("   all|java         - check all files for changes or '.java' files only");
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
        params.javaOnly = tail.removeFirst().equals("java");
        params.commandAndArgs = tail;
        return params;
    }

    public static class Params {

        List<String> modules;
        boolean javaOnly;
        List<String> commandAndArgs;

        @Override
        public String toString() {
            return SimpleClassName.of(this.getClass()) + "("
                    + "modules=" + modules
                    + ", javaOnly=" + javaOnly
                    + ", commandAndArgs=" + commandAndArgs
                    + ")";
        }

    }

    public static void main(String[] args) throws Exception {
        new RunAndWatchMavenSources().execute(asList(args));
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
        List<File> dirs = srcDirsOf(params.modules);
        final List<String> canonicalPaths = canonicalPathsOf(params.modules);
        Scanner.watch(scanIntervalSeconds, dirs, new Scanner.BulkListener() {
            @Override
            public void filesChanged(List<String> fileNames) throws Exception {
                onFilesChanged(fileNames, canonicalPaths, params, process);
            }
        });
        stopCommandProcess(process[0]);
    }

    private static void onFilesChanged(List<String> fileNames, List<String> modulePaths, Params params, Process[] process) throws Exception {
        Set<String> changedModules = changedModulesOf(modulePaths, fileNames);
        for (String moduleDir : changedModules) {
            executeMavenTestCompileIn(moduleDir);
        }
        List<String> changedFiles = changedFilesOf(fileNames, params.javaOnly);
        if (!changedFiles.isEmpty()) {
            stopCommandProcess(process[0]);
            process[0] = startCommandProcess(params);
        }
    }

    private static List<File> srcDirsOf(List<String> modules) {
        ArrayList<File> dirs = new ArrayList<>();
        for (String module : modules) {
            dirs.add(new File(module + "/src"));
        }
        return dirs;
    }

    private List<String> canonicalPathsOf(List<String> modules) throws IOException {
        ArrayList<String> names = new ArrayList<>();
        for (String moduleName : modules) {
            names.add(new File(moduleName).getCanonicalPath());
        }
        return names;
    }

    private static List<String> changedFilesOf(List<String> fileNames, boolean javaOnly) {
        if (!javaOnly) {
            return fileNames;
        }
        List<String> changedFiles = new ArrayList<>();
        for (String fileName : fileNames) {
            if (fileName.endsWith(".java")) {
                changedFiles.add(fileName);
            }
        }
        return changedFiles;
    }

    private static Set<String> changedModulesOf(List<String> modulePaths, List<String> changedFiles) {
        Set<String> changedModules = new TreeSet<>();
        for (String changedFile : changedFiles) {

            for (String modulePath : modulePaths) {
                if (changedFile.startsWith(modulePath)) {
                    changedModules.add(modulePath);
                }
            }
        }
        return changedModules;
    }

    private static void executeMavenTestCompileIn(String dir) throws Exception {
        ProcessBuilder process = new ProcessBuilder("mvn", "test-compile");
        process.directory(new File(dir));
        process.redirectErrorStream(true);
        process.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        process.start().waitFor();
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
