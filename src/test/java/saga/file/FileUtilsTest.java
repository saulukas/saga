
package saga.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import saga.util.Timestamp;

public class FileUtilsTest
{
    @Test
    public void shouldPrintProjectFiles()
    {
        FileUtils.visitFiles(new File("src/test/java/saga/file"), new FileVisitor()
        {
            @Override
            public void visitFile(File file)
            {
                System.out.println("    " + file);
            }
        });
    }

    @Test
    public void shouldSortFilesByDate()
    {
        File startDir = new File("src/test/java/saga/file");
        List<File> files = FileUtils.listFiles(startDir, new FileFilter()
        {
            @Override
            public boolean acceptFile(File file)
            {
                return file.isFile()
                    && !file.getAbsolutePath().contains(".svn")
                    && !file.getAbsolutePath().contains(".git");
            }
        });
        List<String> sorted = new ArrayList<String>();
        for (File file : files)
            sorted.add(new Timestamp(file.lastModified()) + " - " + file);
        for (String fileInfo : sorted)
            System.out.println(fileInfo);
    }
}
