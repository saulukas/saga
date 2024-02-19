
package saga.tools.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListBuilder implements FileVisitor
{
    private final List<File> files = new ArrayList<File>();
    private final FileFilter fileFilter;

    public FileListBuilder()
    {
        this(null);
    }

    public FileListBuilder(FileFilter fileFilter)
    {
        this.fileFilter = fileFilter;
    }

    @Override
    public void visitFile(File file)
    {
        if (fileFilter == null || fileFilter.acceptFile(file))
            files.add(file);
    }

    public List<File> getFiles()
    {
        return files;
    }

}
