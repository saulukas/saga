package saga.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static saga.util.SystemOut.println;

public class ScannerTest {

    public static void main(String[] args) throws IOException {
        Scanner.watch(1, asList(new File(".")), (Scanner.BulkListener) (List<String> fileNames) -> {
            onFilesChanged(fileNames);
        });
    }

    static void onFilesChanged(List<String> fileNames) throws Exception {
        println("Files changed: " + new Date());
        fileNames.forEach((name) -> {
            println("    " + name);
        });
    }
}
