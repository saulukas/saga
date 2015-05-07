package saga.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import static java.util.Arrays.asList;
import java.util.LinkedList;
import java.util.List;
import static saga.util.ListUtils.asLinkedList;
import saga.util.SimpleClassName;
import static saga.util.SystemOut.println;

public class PrintMavenClasspath {

    public String name() {
        return SimpleClassName.of(getClass());
    }

    public void printUsage() {
        println("");
        println(name() + " 1.00 (c) saga 2014");
        println("");
        println("    Prints output of 'mvn dependency:build-classpath'.");
        println("    Usefull for wrrting shell scripts to run executable classes");
        println("    in single- or multi-module maven project.");
        println("");
        println("Paramters:");
        println("");
        println("    module-directory");
        println("");
    }

    public Params parseArgs(List<String> args) {
        LinkedList<String> tail = asLinkedList(args);
        if (tail.size() != 1) {
            return null;
        }
        Params params = new Params();
        params.moduleDirectory = tail.removeFirst();
        return params;
    }

    public static class Params {

        String moduleDirectory;

        @Override
        public String toString() {
            return SimpleClassName.of(this.getClass()) + "("
                    + "moduleDirectory=" + moduleDirectory
                    + ")";
        }

    }

    public static void main(String[] args) throws Exception {
        new PrintMavenClasspath().execute(asList(args));
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
        String classpath = getClasspathFromMavenOutput(params);
        String separator = classpath.contains(":") ? ":" : ";";
        String[] elements = classpath.split(separator);
        printClasspathForUnix(elements);
        printClasspathForWindows(elements);
    }

    private String getClasspathFromMavenOutput(final Params params) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("mvn", "dependency:build-classpath");
        builder.directory(new File(params.moduleDirectory));
        builder.redirectErrorStream(true);
        Process process = builder.start();
        LineNumberReader ouput = new LineNumberReader(new InputStreamReader(process.getInputStream()));
        String line = ouput.readLine();
        String classpath = null;
        while (line != null) {
            System.out.println(line);
            boolean nextLineWillBeClasspath = line.contains("Dependencies classpath:");
            line = ouput.readLine();
            if (nextLineWillBeClasspath) {
                classpath = line;
            }
        }
        return classpath;
    }

    private static void printClasspathForUnix(String[] elements) {
        println("");
        println("# CLASSPATH for unix:");
        println("");
        println("CP=");
        for (String element : elements) {
            println("CP=$CP:" + element);
        }
        println("");
    }

    private static void printClasspathForWindows(String[] elements) {
        println("");
        println("# CLASSPATH for windows:");
        println("");
        println("set CP=");
        for (String element : elements) {
            println("set CP=%CP%;" + element);
        }
        println("");
    }

}
