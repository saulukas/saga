/*
 * Copyright Omnitel 2015. All rights reserved.
 */
package saga.ip;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.IntStream.rangeClosed;

public class IPAddress {

    public final int value;

    IPAddress(int value) {
        this.value = value;
    }

    public static IPAddress fromString(String string) {
        String[] parts = string.split("\\.");
    }

    public int part(int index) {
        return part(value, index);
    }

    public int[] parts() {
        return new int[] {
            part(0),
            part(1),
            part(2),
            part(3)
        };
    }

    static int part(int value, int index) {
        if (index < 0 || index > 3) {
            throw new RuntimeException("IP part index must be [0..3]");
        }
        int shiftLeftByBits = 8 * (3 - index);
        int part = (value >> shiftLeftByBits) & 0xFF;
        return part;
    }

    @Override
    public String toString() {
        return part(0) + "." + part(1) + "." + part(2) + "." + part(3);
    }

}
