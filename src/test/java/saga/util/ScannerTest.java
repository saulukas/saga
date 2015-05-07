package saga.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static saga.util.SystemOut.println;

public class ScannerTest {

    public static void main(String[] args) throws IOException {
        Scanner.watch(
                1,
                Arrays.asList(new File(".")),
                new Scanner.BulkListener() {
                    @Override
                    public void filesChanged(List<String> fileNames) throws Exception {
                        onFilesChanged(fileNames);
                    }
                });
    }

    static void onFilesChanged(List<String> fileNames) throws Exception {
        println("Files changed: " + new Date());
        for (String name : fileNames) {
            println("    " + name);
        }
    }
}
