package saga.maven;

import java.util.Arrays;
import org.junit.Test;
import static saga.util.Script.println;

public class WatchMavenTargetTest {

    @Test
    public void testSomeMethod() {
        WatchMavenTargets script = new WatchMavenTargets();
        println(script.parseArgs(Arrays.asList("ohoho;ahaha", "class", "cmd", "a", "b")));
    }

}