
package saga.tools.file;

import java.io.File;

public interface FileFilter
{
    boolean acceptFile(File file);
}
