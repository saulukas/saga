
package saga.file;

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
        FileUtils.visitFiles(new File("."), list);
        for (File file : list.getFiles())
            System.out.println("   " + file);
    }
}
