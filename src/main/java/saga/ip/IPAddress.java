package saga.ip;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static saga.util.ExceptionUtils.exception;

public class IPAddress {

    static Pattern PATTERN = compile("^(\\d{1,3}).(\\d{1,3}).(\\d{1,3}).(\\d{1,3})$");
    static String PATTERN_DESCRIPTION = "IP4 address must be 'nnn.nnn.nnn.nnn' where nnn is number [0..255]";

    final int value;

    private IPAddress(int value) {
        this.value = value;
    }

    public static boolean isValid(String address) {
        return valueOrNull(address) != null;
    }

    public static IPAddress of(String address) {
        Integer value = valueOrNull(address);
        if (value == null) {
            throw exception("Invalid IP4 address '" + address + "'. " + PATTERN_DESCRIPTION);
        }
        return new IPAddress(value);
    }

    public static IPAddress of(int value) {
        return new IPAddress(value);
    }

    @Override
    public String toString() {
        return octet(0) + "." + octet(1) + "." + octet(2) + "." + octet(3);
    }

    public int value() {
        return value;
    }

    public int octet(int index) {
        return octetOf(value, index);
    }

    public int[] octets() {
        return new int[]{octet(0), octet(1), octet(2), octet(3)};
    }

    static Integer valueOrNull(String address) {
        if (address == null || address.isEmpty()) {
            return null;
        }
        Matcher matcher = PATTERN.matcher(address);
        if (!matcher.matches()) {
            return null;
        }
        int value = 0;
        for (int i = 1; i <= 4; i++) {
            int octet = Integer.parseInt(matcher.group(i));
            if (octet < 0 || octet > 255) {
                return null;
            }
            value = (value << 8) + octet;
        }
        return value;
    }

    static int octetOf(int value, int index) {
        if (index < 0 || index > 3) {
            throw exception("IP octet index must be [0..3]");
        }
        int shiftLeftByBits = 8 * (3 - index);
        int octet = (value >> shiftLeftByBits) & 0xFF;
        return octet;
    }

}
