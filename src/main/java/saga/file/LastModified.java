
package saga.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import saga.date.Timestamp;

public class LastModified
{
    public static void main(String[] args)
    {
        File startDir = new File(".");
        List<File> files = FileUtils.listFiles(startDir, new FileFilter()
        {
            @Override
            public boolean acceptFile(File file)
            {
                return file.isFile()
                    && !file.getAbsolutePath().contains("/.svn/")
                    && !file.getAbsolutePath().contains("/CVS/");
            }
        });
        List<String> sorted = new ArrayList<String>();
        for (File file : files)
            sorted.add(new Timestamp(file.lastModified()) + " - " + file);
        Collections.sort(sorted);
        Collections.reverse(sorted);
        for (String fileInfo : sorted)
            System.out.println(fileInfo);
    }
}
