package saga.tools.ip;

import saga.tools.ip.IPTool;
import org.junit.Test;

import static saga.tools.ip.IPTool.doGetIPRangesFrom;
import static saga.util.ListUtils.arrayOf;
import static saga.util.SystemOut.println;

public class IPToolTest {

    IPTool tool = new IPTool();

    @Test
    public void checks_ip_list_if_ontained_in_given_ranges() {
//        tool.run(arrayOf("check  172.16.255.173/16 contains  172.16.255.173"));
    }

    @Test
    public void doGetIPRangesFrom_creates_IPRange_list_from_comma_separated_values() {
        println(doGetIPRangesFrom("172.16.0.0/16,127.0.0.1"));
    }

}