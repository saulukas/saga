package saga.tools.ip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import static saga.TestUtils.check;
import static saga.util.SystemOut.println;

public class IPAddressTest {

    @Test
    public void string_representation_can_be_validated() {
        check(IPAddress.isValid("10.20.30.40"));
        check(!IPAddress.isValid("10.20.30."));
        check(!IPAddress.isValid("10.20"));
        check(!IPAddress.isValid("10.20.30.400"));
        check(!IPAddress.isValid(" 10.20.30.40"));
        check(!IPAddress.isValid("10.20.30.40 "));
        check(!IPAddress.isValid("   "));
        check(!IPAddress.isValid(null));
    }

    @Test
    public void consists_of_four_octets() {
        assertEquals(IPAddress.of("10.20.30.40").octet(0), 10);
        assertEquals(IPAddress.of("10.20.30.40").octet(1), 20);
        assertEquals(IPAddress.of("10.20.30.40").octet(2), 30);
        assertEquals(IPAddress.of("10.20.30.40").octet(3), 40);
        assertEquals(IPAddress.of("10.20.30.40").octets()[0], 10);
        assertEquals(IPAddress.of("10.20.30.40").octets()[1], 20);
        assertEquals(IPAddress.of("10.20.30.40").octets()[2], 30);
        assertEquals(IPAddress.of("10.20.30.40").octets()[3], 40);
    }

    @Test
    public void has_toString_for_canonical_representation() {
        assertEquals(IPAddress.of("10.20.30.40").toString(), "10.20.30.40");
        assertEquals(IPAddress.of("010.020.030.040").toString(), "10.20.30.40");
        assertEquals(IPAddress.of("0.0.0.0").toString(), "0.0.0.0");
        assertEquals(IPAddress.of("255.255.255.255").toString(), "255.255.255.255");
    }

    @Test
    public void has_a_nice_explanation_for_invalid_string_values() {
        try {
            IPAddress.of("10.20..40");
            fail("Invalid IP4 address was not validated");
        } catch (Exception ex) {
            check(ex.getMessage().contains(IPAddress.PATTERN_DESCRIPTION));
            println(ex.getMessage());
        }
    }

    @Test
    public void asBinaryString_creates_readable_binary_representation() {
        assertEquals(IPAddress.of("10.20.30.40").asBinaryString(),
                "0000 1010   0001 0100   0001 1110   0010 1000");
    }

}
