package saga.ip;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static saga.util.ExceptionUtils.exception;

public class IP4Subnet {

    static Pattern PATTERN = compile("^(\\d{1,3}).(\\d{1,3}).(\\d{1,3}).(\\d{1,3})/(\\d{1,2})$");
    static String PATTERN_DESCRIPTION = "IP4 subnet must be 'nnn.nnn.nnn.nnn/mm'"
            + " where nnn is number [0..255] and mm is [1..32]."
            + " Subnet address AND'ed with inverted mask must be zero.";

    final IP4Address address;
    final IP4SubnetMask mask;

    IP4Subnet(long value) {
        int maskBitCount = (int)(value & 0xFF);
        int addressValue = (int)(value >> 8);
        this.address = new IP4Address(addressValue);
        this.mask = new IP4SubnetMask(maskBitCount);
    }

    public static boolean isValid(String subnet) {
        return combinedValueOrNull(subnet) != null;
    }

    public static IP4Subnet of(String subnet) {
        Long value = combinedValueOrNull(subnet);
        if (value == null) {
            throw exception("Invalid IP4 subnet '" + subnet + "'. " + PATTERN_DESCRIPTION);
        }
        return new IP4Subnet(value);
    }

    public static IP4Subnet of(IP4Address address) {
        int maskBitCount = 32;
        return new IP4Subnet(((long)address.value() << 8) + maskBitCount);
    }

    public IP4Address address() {
        return address;
    }

    public IP4SubnetMask mask() {
        return mask;
    }

    public boolean isSingleAddress() {
        return mask.bitCount() == 32;
    }

    public boolean conains(IP4Address ip) {
        return (address.value() & mask.value()) == (ip.value() & mask.value());
    }

    @Override
    public String toString() {
        return address + "/" + mask;
    }

    static Long combinedValueOrNull(String subnet) {
        if (subnet == null || subnet.isEmpty()) {
            return null;
        }
        Matcher matcher = PATTERN.matcher(subnet);
        if (!matcher.matches()) {
            return null;
        }
        long value = 0;
        for (int i = 1; i <= 4; i++) {
            int octet = Integer.parseInt(matcher.group(i));
            if (octet < 0 || octet > 255) {
                return null;
            }
            value = (value << 8) + octet;
        }
        int maskBitCount = Integer.parseInt(matcher.group(5));
        if (maskBitCount < 1 || maskBitCount > 32) {
            return null;
        }
        int mask = IP4SubnetMask.maskForBitCount(maskBitCount);
        if ((value & ~mask) != 0) {
            return null; // there are some non-zero bits outside of mask
        }
        value = (value << 8) + maskBitCount;
        return value;
    }

}
