package saga.tools.tabs;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Test;

import static org.junit.Assert.*;

public class TabsToolTest {

    @Test
    public void convertTabsToSpaces() throws IOException {
        String input = "\n   \r"
                + "\t.\t..\t...\t....\t.....\t......\t.......\t........\t."
                + "\n\r\n"
                + "\t.\t..\t...\t....\t.....\t......\t.......\t........\t.";
        String expected = "\n   \r"
                + "        .       ..      ...     ....    .....   ......  ....... ........        ."
                + "\n\r\n"
                + "        .       ..      ...     ....    .....   ......  ....... ........        .";
        StringWriter actual = new StringWriter();

        TabsTool.convertTabsToSpaces(8, new StringReader(input), actual);

        assertEquals(expected, actual.toString());
    }

}
