package saga.ip;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static saga.TestUtils.check;

public class IP4SubnetMaskTest {

    @Test
    public void uses_bitCount_to_calculate_mask_value() {
        check(IP4SubnetMask.of(1).value() == 0x80000000);
        check(IP4SubnetMask.of(17).value() == 0xFFFF8000);
        check(IP4SubnetMask.of(32).value() == 0xFFFFFFFF);
    }

    @Test
    public void can_be_used_as_IP4Address() {
        assertEquals(IP4SubnetMask.of(1).asIP4Address().toString(), "128.0.0.0");
        assertEquals(IP4SubnetMask.of(17).asIP4Address().toString(), "255.255.128.0");
        assertEquals(IP4SubnetMask.of(32).asIP4Address().toString(), "255.255.255.255");
    }

    @Test
    public void has_toString_for_canonical_representation() {
        assertEquals(IP4SubnetMask.of(1).toString(), "1");
        assertEquals(IP4SubnetMask.of(17).toString(), "17");
        assertEquals(IP4SubnetMask.of(32).toString(), "32");
    }
}