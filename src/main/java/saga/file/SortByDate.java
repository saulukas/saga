
package saga.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import saga.Tool;
import static saga.util.SystemOut.println;
import saga.util.Timestamp;

public class SortByDate extends Tool
{

    public SortByDate() {
        super("sort-by-date", "Sorts files recursively by modification date.");
    }

    @Override
    public int run(String[] args) throws Exception 
    {
        if (args.length < 1)
        {
            println(name + " (c) saga 2009");
            println("");
            println("    " + oneLineDescription);
            println("");
            println("Parameters:");
            println("");
            println("    asc | desc");
            println("");
            return 0;
        }
        boolean descending = "desc".equals(args[0]);
        
        File startDir = new File(".");
        List<File> files = FileUtils.listFiles(startDir, new FileFilter()
        {
            @Override
            public boolean acceptFile(File file)
            {
                return file.isFile()
                    && !file.getAbsolutePath().contains("/.git/")
                    && !file.getAbsolutePath().contains("/.svn/")
                    && !file.getAbsolutePath().contains("/CVS/");
            }
        });
        List<String> sorted = new ArrayList<String>();
        for (File file : files)
            sorted.add(new Timestamp(file.lastModified()) + " - " + file);
        Collections.sort(sorted);
        if (descending)
            Collections.reverse(sorted);
        for (String fileInfo : sorted)
            System.out.println(fileInfo);
        
        return 0;
    }
}
