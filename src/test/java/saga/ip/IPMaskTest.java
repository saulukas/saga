package saga.ip;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IPMaskTest {

    @Test
    public void uses_bitCount_to_calculate_mask_value() {
        assertEquals(IPMask.of(1).value(), 0x80000000);
        assertEquals(IPMask.of(17).value(), 0xFFFF8000);
        assertEquals(IPMask.of(32).value(), 0xFFFFFFFF);
    }

    @Test
    public void can_be_used_as_IP4Address() {
        assertEquals(IPMask.of(1).asIPAddress().toString(), "128.0.0.0");
        assertEquals(IPMask.of(17).asIPAddress().toString(), "255.255.128.0");
        assertEquals(IPMask.of(32).asIPAddress().toString(), "255.255.255.255");
    }

    @Test
    public void has_toString_for_canonical_representation() {
        assertEquals(IPMask.of(1).toString(), "1");
        assertEquals(IPMask.of(17).toString(), "17");
        assertEquals(IPMask.of(32).toString(), "32");
    }
}
