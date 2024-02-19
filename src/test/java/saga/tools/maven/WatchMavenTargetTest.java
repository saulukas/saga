package saga.tools.maven;

import saga.maven.RunAndWatchMavenTargets;
import java.util.Arrays;
import org.junit.Test;
import static saga.util.SystemOut.println;

public class WatchMavenTargetTest {

    @Test
    public void testSomeMethod() {
        RunAndWatchMavenTargets script = new RunAndWatchMavenTargets();
        println(script.parseArgs(Arrays.asList("ohoho;ahaha", "class", "cmd", "a", "b")));
    }

}