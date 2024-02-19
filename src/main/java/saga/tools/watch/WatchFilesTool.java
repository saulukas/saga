package saga.tools.watch;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import static java.util.Arrays.asList;
import saga.tools.Tool;
import saga.util.ArgList;
import saga.util.Dto;

import static saga.util.SystemOut.println;
import static saga.util.Equal.equal;
import static saga.util.ExceptionUtils.ex;

public class WatchFilesTool extends Tool {

    public WatchFilesTool() {
        super("watch", "Watch files and directories recursively and exit on any change");
    }

    void printUsage() {
        println(name + " (c) saga 2019");
        println("");
        println("    " + oneLineDescription);
        println("");
        println("Parameters:");
        println("");
        println("   [--list] <files-and-dirs-separated-by-space-or-path-separator>  - watch and exit on any change");
        println("");
    }

    static class Config extends Dto {
        public boolean listWatches = false;
    }

    @Override
    public int run(String[] argArray) throws Exception {
        ArgList args = ArgList.of(argArray);
        Config config = new Config();
        if (args.isEmpty()) {
            printUsage();
            return 0;
        }
        if (equal(args.head(), "--list")) {
            args.removeHead();
            config.listWatches = true;
        }
        List<String> pathNames = new ArrayList<>();
        while (!args.isEmpty()) {
            pathNames.addAll(asList(args.removeHead().split(File.pathSeparator)));
        }
        watchFilesAndDirectories(pathNames, config);
        return 0;
    }

    private void watchFilesAndDirectories(List<String> pathNames, Config config) throws Exception {
        FileSystem fileSystem = FileSystems.getDefault();
        boolean changeHappend = false;
        while (!changeHappend) {
            DirsWithFiles dirs = new DirsWithFiles(fileSystem);
            for (String pathName : pathNames) {
                collectDirsAndFiles(dirs, fileSystem.getPath(pathName));
            }
            if (config.listWatches) {
                dirs.printWatches();
            }
            println("Watching"
                + " dirs=" + dirs.getDirCount()
                + " files=" + dirs.getFileCount()
                + " watchers=" + dirs.getWatcherCount()
            );
            changeHappend = dirs.waitForChangeAndClose();
        }
    }

    static void collectDirsAndFiles(DirsWithFiles map, Path path) throws Exception {
        File file = path.toFile();
        String canonicalPath = file.getCanonicalPath();

        if (file.isDirectory()) {
            map.addWholeDir(canonicalPath);
            Files.list(path).forEach((childPath) -> ex(() -> {
                if (childPath.toFile().isDirectory()) {
                    collectDirsAndFiles(map, childPath);
                }
            }));
            return;
        }

        if (file.isFile()) {
            String canonicalDirPath = file.getParentFile().getCanonicalPath();
            map.addFileToDir(canonicalDirPath, canonicalPath);
            return;
        }

        throw new RuntimeException("No file or directory found: " + canonicalPath);
    }

    static void printEvent(WatchEvent<?> event) throws IOException {
        WatchEvent.Kind<?> kind = event.kind();
        Path path = (Path) event.context();
        System.out.println("Entry changed " + kind + ": " + path + ":" + path.toFile().getCanonicalPath());
    }

    //
    //   Main difficulties
    //   -----------------
    //   Java API allows only watching directories and not individual files.
    //   It is possible to use single WatchService to watch several directories
    //   but changes are reported in relative file names only - no way to
    //   find out in which directory file did change.
    //
    //   Idea is to use single WatchService to detected change in any of directories
    //   and to use individual watchers for each dir to get canonical path.
    //
    //   Another problem: creating WatchService for each directory eats OS limits to fast.
    //   So separate watches are only created for directories where individual fileas
    //   are being watched.
    //
    static class Directory {
        final String canonicalPath;
        Set<String> files = new TreeSet<>(); // if not empty - watching individual files
        WatchService watcher;

        Directory(String canonicalPath) {
            this.canonicalPath = canonicalPath;
        }

        boolean isWholeDir() {
            return files.isEmpty();
        }
    }

    static class DirsWithFiles {
        private final FileSystem fileSystem;
        private WatchService globalWatcher;
        private final TreeMap<String, Directory> dirs = new TreeMap<>();

        public DirsWithFiles(FileSystem fileSystem) throws IOException {
            this.fileSystem = fileSystem;
            this.globalWatcher = fileSystem.newWatchService();
        }

        void addWholeDir(String canonicalPath) throws IOException {
            Directory dir = dirs.get(canonicalPath);
            if (dir != null && dir.watcher != null) {
                dir.watcher.close();
            }
            if (dir == null) {
                watchGlobally(canonicalPath);
            }
            dirs.put(canonicalPath, new Directory(canonicalPath));
        }

        private void addFileToDir(String canonicalDirPath, String filePath) throws IOException {
            Directory dir = dirs.get(canonicalDirPath);
            if (dir != null && dir.isWholeDir()) {
                return;
            }
            if (dir == null) {
                watchGlobally(canonicalDirPath);
                dir = new Directory(canonicalDirPath);
                dir.watcher = fileSystem.newWatchService();
                fileSystem.getPath(canonicalDirPath).register(
                    dir.watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.OVERFLOW
                );
            }
            String relativeFilePath =
                filePath.substring(canonicalDirPath.length() + File.separator.length());
            dir.files.add(relativeFilePath);
            dirs.put(canonicalDirPath, dir);
        }

        private void watchGlobally(String canonicalDirPath) throws IOException {
            fileSystem.getPath(canonicalDirPath).register(
                globalWatcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.OVERFLOW
            );
        }

        int getDirCount() {
            return dirs.size();
        }

        int getFileCount() {
            return dirs.values().stream().mapToInt((dir) -> dir.files.size()).sum();
        }

        int getWatcherCount() {
            return 1 + (int) dirs.values().stream().filter((dir) -> dir.watcher != null).count();
        }

        void printWatches() {
            println("Watching files and dirs:");
            dirs.entrySet().forEach((dir) -> {
                String dirName = dir.getKey();
                Set<String> files = dir.getValue().files;
                println("    " + dirName);
                files.forEach((fileName) -> {
                    println("        " + fileName);
                });
            });
        }

        boolean waitForChangeAndClose() throws Exception {
            WatchKey globalKey = globalWatcher.take(); // this call is blocking until events are present
            globalWatcher.close();
            boolean isIndividualFileChange = false;
            boolean isRelevantChange = false;
            for (Directory dir : dirs.values()) {
                if (dir.watcher == null) {
                    continue;
                }
                WatchKey key = dir.watcher.poll();
                if (key != null) {
                    isIndividualFileChange = true;
                    for (WatchEvent<?> event : key.pollEvents()) {
                        String fileName = "" + event.context();
                        if (dir.files.contains(fileName)) {
                            isRelevantChange = true;
                        }
                        String canonicalPath = dir.canonicalPath + File.separator + fileName;
                        println("   " + event.kind() + " [" + canonicalPath + "] relevant=" + isRelevantChange);
                    }
                }
                dir.watcher.close();
            }
            if (!isIndividualFileChange) {
                for (WatchEvent<?> event : globalKey.pollEvents()) {
                    println("   " + event.kind() + " [" + event.context() + "]");
                }
            }
            return !isIndividualFileChange || isRelevantChange;
        }

    }

}
