package saga.util;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import static saga.util.ExceptionUtils.ex;

public class PlayWithFileWatchService {

    static class WatcherCounts {

        public int fileCount;
        public int dirCount;
    }

    public static void main(String[] args) throws Exception {
        Path path = FileSystems.getDefault().getPath(".");
        WatchService watchService = path.getFileSystem().newWatchService();

        WatcherCounts counts = new WatcherCounts();
        watchPathAndItsChildren(path, watchService, counts);

        System.out.println("Watching:");
        System.out.println("   directories  : " + counts.dirCount);
        System.out.println("   files        : " + counts.fileCount);
        WatchKey watchKey = watchService.take(); // this call is blocking until events are present
        System.out.println("Watching path: " + path + " .......");

        // poll for file system events on the WatchKey
        for (final WatchEvent<?> event : watchKey.pollEvents()) {
            printEvent(event);
        }

    }

    static void watchPathAndItsChildren(Path path, WatchService watchService, WatcherCounts counts) throws Exception {
        System.out.println("watching [" + path + "]");
        path.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.OVERFLOW
        );
        if (!path.toFile().isDirectory()) {
            counts.fileCount += 1;
            return;
        }
        counts.dirCount += 1;
        Files.list(path).forEach((childPath) -> {
            try {
                if (childPath.toFile().isDirectory()) {
                    watchPathAndItsChildren(childPath, watchService, counts);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    static void printEvent(WatchEvent<?> event) {
        Kind<?> kind = event.kind();
        System.out.println("Entry changed " + kind + ": " + (Path) event.context());
    }
}
