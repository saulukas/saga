
package saga.tools.file;

import java.io.File;
import java.util.List;

public class FileUtils
{
    public static void visitFiles(File startDir, FileVisitor fv)
    {
        if (startDir.isFile())
            fv.visitFile(startDir);
        else
            for (File file : startDir.listFiles())
                visitFiles(file, fv);
    }

    public static List<File> listFiles(File startDir)
    {
        return listFiles(startDir, null);
    }

    public static List<File> listFiles(File startDir, FileFilter filter)
    {
        FileListBuilder listBuilder = new FileListBuilder(filter);
        FileUtils.visitFiles(startDir, listBuilder);
        return listBuilder.getFiles();
    }
}
