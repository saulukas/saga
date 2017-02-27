package saga.ip;

import org.junit.Test;

import static saga.util.ListUtils.arrayOf;

public class IPToolTest {

    IPTool tool = new IPTool();

    @Test
    public void checks_ip_list_if_ontained_in_given_ranges() {
        tool.run(arrayOf("check  172.16.255.173/16 contains  172.16.255.173"));
    }

}