package saga.ip;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static saga.util.ExceptionUtils.exception;

public class IPSubnet {

    static Pattern PATTERN = compile("^(\\d{1,3}).(\\d{1,3}).(\\d{1,3}).(\\d{1,3})/(\\d{1,2})$");
    static String PATTERN_DESCRIPTION = "IP4 subnet must be 'nnn.nnn.nnn.nnn/mm'"
            + " where nnn is number [0..255] and mm is [1..32]."
            + " Subnet address and'ed bitwise with inverted mask must be zero.";

    private final IPAddress address;
    private final IPMask mask;

    private IPSubnet(long value) {
        int maskBitCount = (int)(value & 0xFF);
        int addressValue = (int)(value >> 8);
        this.address = IPAddress.of(addressValue);
        this.mask = IPMask.of(maskBitCount);
    }

    public static boolean isValid(String subnet) {
        return combinedValueOrNull(subnet) != null;
    }

    public static IPSubnet of(String subnet) {
        Long value = combinedValueOrNull(subnet);
        if (value == null) {
            throw exception("Invalid IP4 subnet '" + subnet + "'. " + PATTERN_DESCRIPTION);
        }
        return new IPSubnet(value);
    }

    public static IPSubnet of(IPAddress address) {
        int maskBitCount = 32;
        return new IPSubnet(((long)address.value() << 8) + maskBitCount);
    }

    public IPAddress address() {
        return address;
    }

    public IPMask mask() {
        return mask;
    }

    public boolean isSingleAddress() {
        return mask.bitCount() == 32;
    }

    public boolean conains(IPAddress ip) {
        return (ip.value() & mask.value()) == (address.value() & mask.value());
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
        int mask = IPMask.maskForBitCount(maskBitCount);
        if ((value & ~mask) != 0) {
            return null; // there are some non-zero bits outside of mask
        }
        value = (value << 8) + maskBitCount;
        return value;
    }

}
