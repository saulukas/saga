
package saga.file;

import java.io.File;

public interface FileFilter
{
    boolean acceptFile(File file);
}
