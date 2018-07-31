package saga.ip;

import static saga.ip.IPAddress.binaryStringOf;
import static saga.util.ExceptionUtils.exception;

public class IPMask {

    private final int bitCount;
    private final int value;

    private IPMask(int bitCount) {
        this.bitCount = bitCount;
        this.value = maskForBitCount(bitCount);
    }

    public static IPMask of(int bitCount) {
        if (bitCount < 1 || bitCount > 32) {
            throw exception("Invalid bitCount '" + bitCount + "'."
                    + " Bit count of IP4 subnet mask must be [1..32]");
        }
        return new IPMask(bitCount);
    }

    public int bitCount() {
        return bitCount;
    }

    public int value() {
        return value;
    }

    public IPAddress asIPAddress() {
        return IPAddress.of(value);
    }

    public String asBinaryString() {
        return binaryStringOf(value);
    }

    @Override
    public String toString() {
        return "" + bitCount;
    }

    static int maskForBitCount(int bitCount) {
        int mask = 0;
        int bit = 0x80000000;
        for (int i = 1; i <= bitCount; i++) {
            mask |= bit;
            bit >>= 1;
        }
        return mask;
    }
}
