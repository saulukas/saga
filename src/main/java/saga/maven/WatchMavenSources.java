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

public class WatchMavenSources extends Script {

    public void printUsage() {
        println("WatchMavenSources 1.00 (c) saga 2014");
        println("");
        println("    Watches 'target' subdirectories of maven module directories and");
        println("    executes command if changes detected.");
        println("");
        println("Paramters:");
        println("");
        println("   module[:module]  all|class  command [args]");
        println("");
        println("   module[:module]  - list of colon separated maven module directories");
        println("   all|class        - check all changes or '.class' files only");
        println("   command          - command to execute if changes detected");
        println("");
    }

    public Params parseArgs(List<String> args) {
        LinkedList<String> tail = asLinkedList(args);
        if (tail.size() < 3) {
            return null;
        }
        Params params = new Params();
        params.modules = asList(tail.removeFirst().split(";"));
        params.classOnly = tail.removeFirst().equals("class");
        params.commandAndArgs = tail;
        return params;
    }

    public static class Params {

        List<String> modules;
        boolean classOnly;
        List<String> commandAndArgs;

        @Override
        public String toString() {
            return SimpleClassName.of(this.getClass()) + "("
                    + "modules=" + modules
                    + ", classOnly=" + classOnly
                    + ", commandAndArgs=" + commandAndArgs
                    + ")";
        }

    }

    public static void main(String[] args) throws Exception {
        new WatchMavenSources().execute(asList(args));
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
        int scanIntervalSeconds = 1;
        List<File> dirs = targetDirsOf(params.modules);
        final List<String> canonicalPaths = canonicalPathsOf(params.modules);
        Scanner.watch(scanIntervalSeconds, dirs, new Scanner.BulkListener() {
            @Override
            public void filesChanged(List<String> fileNames) throws Exception {
                onFilesChanged(fileNames, canonicalPaths, params.classOnly);
            }
        });
    }

    private static List<File> targetDirsOf(List<String> modules) {
        ArrayList<File> dirs = new ArrayList<>();
        for (String module : modules) {
            dirs.add(new File(module + "/target"));
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

    private static void onFilesChanged(List<String> fileNames, List<String> modulePaths, boolean classesOnly) throws Exception {
        List<String> changedFiles = changedFilesOf(fileNames, classesOnly);
        Set<String> changedModules = changedModulesOf(modulePaths, changedFiles);
        for (String moduleDir : changedModules) {
            executeMavenTestCompileIn(moduleDir);
        }
    }

    private static void executeMavenTestCompileIn(String dir) throws Exception {
        ProcessBuilder process = new ProcessBuilder("mvn", "test-compile");
        process.directory(new File(dir));
        process.redirectErrorStream(true);
        process.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        process.start().waitFor();
    }
}
