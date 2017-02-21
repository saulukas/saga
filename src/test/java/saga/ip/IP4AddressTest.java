package saga.ip;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static saga.TestUtils.check;
import static saga.ip.IP4Address.ip4Address;
import static saga.ip.IP4Address.isValid;
import static saga.util.SystemOut.println;

public class IP4AddressTest {

    @Test
    public void string_representation_can_be_validated() {
        check(isValid("10.20.30.40"));
        check(!isValid("10.20.30."));
        check(!isValid("10.20"));
        check(!isValid("10.20.30.400"));
        check(!isValid(" 10.20.30.40"));
        check(!isValid("10.20.30.40 "));
        check(!isValid("   "));
        check(!isValid(null));
    }

    @Test
    public void consists_of_four_octets() {
        assertEquals(ip4Address("10.20.30.40").octet(0), 10);
        assertEquals(ip4Address("10.20.30.40").octet(1), 20);
        assertEquals(ip4Address("10.20.30.40").octet(2), 30);
        assertEquals(ip4Address("10.20.30.40").octet(3), 40);
        assertEquals(ip4Address("10.20.30.40").octets()[0], 10);
        assertEquals(ip4Address("10.20.30.40").octets()[1], 20);
        assertEquals(ip4Address("10.20.30.40").octets()[2], 30);
        assertEquals(ip4Address("10.20.30.40").octets()[3], 40);
    }

    @Test
    public void has_toString_for_canonical_representation() {
        assertEquals(ip4Address("10.20.30.40").toString(), "10.20.30.40");
        assertEquals(ip4Address("010.020.030.040").toString(), "10.20.30.40");
        assertEquals(ip4Address("0.0.0.0").toString(), "0.0.0.0");
        assertEquals(ip4Address("255.255.255.255").toString(), "255.255.255.255");
    }

    @Test
    public void has_a_nice_explanation_for_invalid_string_values() {
        try {
            ip4Address("10.20..40");
            fail("Invalid IP4 address was not validated");
        } catch (Exception ex) {
            check(ex.getMessage().contains(IP4Address.PATTERN_DESCRIPTION));
            println(ex.getMessage());
        }
    }

}