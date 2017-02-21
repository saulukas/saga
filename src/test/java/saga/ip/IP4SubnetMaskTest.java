package saga.ip;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static saga.TestUtils.check;
import static saga.ip.IP4SubnetMask.ip4SubnetMask;

public class IP4SubnetMaskTest {

    @Test
    public void uses_bitCount_to_calculate_mask_value() {
        check(ip4SubnetMask(1).value() == 0x80000000);
        check(ip4SubnetMask(17).value() == 0xFFFF8000);
        check(ip4SubnetMask(32).value() == 0xFFFFFFFF);
    }

    @Test
    public void can_be_used_as_IP4Address() {
        assertEquals(ip4SubnetMask(1).asIP4Address().toString(), "128.0.0.0");
        assertEquals(ip4SubnetMask(17).asIP4Address().toString(), "255.255.128.0");
        assertEquals(ip4SubnetMask(32).asIP4Address().toString(), "255.255.255.255");
    }

    @Test
    public void has_toString_for_canonical_representation() {
        assertEquals(ip4SubnetMask(1).toString(), "1");
        assertEquals(ip4SubnetMask(17).toString(), "17");
        assertEquals(ip4SubnetMask(32).toString(), "32");
    }
}
