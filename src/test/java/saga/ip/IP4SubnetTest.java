package saga.ip;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static saga.TestUtils.check;
import static saga.ip.IP4Address.ip4Address;
import static saga.ip.IP4Subnet.ip4Subnet;
import static saga.ip.IP4Subnet.isValid;
import static saga.util.SystemOut.println;

public class IP4SubnetTest {

    @Test
    public void string_representation_can_be_validated() {
        check(isValid("10.0.0.0/8"));
        check(!isValid("10.20.30.40/99"));
        check(!isValid("10.0.0./5"));
        check(!isValid("   "));
        check(!isValid(null));
    }

    @Test
    public void non_zero_bits_are_not_allowed_outside_of_mask() {
        check(!isValid("10.20.30.40/16"));
    }

    @Test
    public void consists_of_IP4Address_and_subnet_maskfour_octets() {
        assertEquals(ip4Subnet("10.20.0.0/16").address().toString(), "10.20.0.0");
        assertEquals(ip4Subnet("10.20.0.0/16").mask().toString(), "16");
    }

    @Test
    public void has_toString_for_canonical_representation() {
        assertEquals(ip4Subnet("10.20.64.0/20").toString(), "10.20.64.0/20");
        assertEquals(ip4Subnet("010.020.064.0/20").toString(), "10.20.64.0/20");
    }

    @Test
    public void has_a_nice_explanation_for_invalid_string_values() {
        try {
            ip4Subnet("10.20..40/22");
            fail("Invalid IP4 address was not validated");
        } catch (Exception ex) {
            check(ex.getMessage().contains(IP4Subnet.PATTERN_DESCRIPTION));
            println(ex.getMessage());
        }
    }

    @Test
    public void checks_if_IP4_address_is_contaiend_in_subnet() {
        check(ip4Subnet("10.20.80.0/20").conains(ip4Address("10.20.80.0")));
        check(ip4Subnet("10.20.80.0/20").conains(ip4Address("10.20.81.0")));
        check(ip4Subnet("10.20.80.0/20").conains(ip4Address("10.20.95.1")));
        check(!ip4Subnet("10.20.80.0/20").conains(ip4Address("10.20.96.1")));
    }

}