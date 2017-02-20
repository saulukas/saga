package saga.ip;

import static saga.util.ExceptionUtils.exception;

public class IP4SubnetMask {

    final int bitCount;
    final int value;

    IP4SubnetMask(int bitCount) {
        this.bitCount = bitCount;
        this.value = maskForBitCount(bitCount);
    }

    public static IP4SubnetMask of(int bitCount) {
        if (bitCount < 1 || bitCount > 32) {
            throw exception("Invalid bitCount '" + bitCount + "'."
                    + " Bit count of IP4 subnet mask must be [1..32]");
        }
        return new IP4SubnetMask(bitCount);
    }


    public int bitCount() {
        return bitCount;
    }

    public int value() {
        return value;
    }

    public IP4Address asIP4Address() {
        return new IP4Address(value);
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
