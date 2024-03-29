
package saga.tools.file;

import saga.tools.file.FileFilter;
import saga.tools.file.FileUtils;
import saga.tools.file.FileListBuilder;
import java.io.File;
import org.junit.Test;

public class FileListBuilderTest
{
    @Test
    public void shouldPrintTestFiles()
    {
        FileListBuilder list = new FileListBuilder(new FileFilter()
        {
            @Override
            public boolean acceptFile(File file)
            {
                return file.isFile()
                    && file.getAbsolutePath().contains("test")
                    && !file.getAbsolutePath().contains(".svn");
            }
        });
        FileUtils.visitFiles(new File("src/test/java/saga/tools/file/u1"), list);
        for (File file : list.getFiles())
            System.out.println("   " + file);
    }
}
