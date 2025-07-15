package saga.tools.totp;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import java.util.Scanner;
import saga.tools.Tool;
import saga.util.ArgList;
import static saga.util.Equal.equal;
import static saga.util.SystemOut.println;

public class TotpTool extends Tool {

    public TotpTool() {
        super("totp", "Time-based One-Time Password");
    }

    @Override
    public int run(String[] argArray) throws Exception {
        ArgList args = ArgList.of(argArray);
        if (args.isEmpty()) {
            printUsage();
            return 0;
        }
        if (equal(args.head(), "generate")) {
            args.removeHead();
            return generateTotp(args);
        }
        printUsage();
        return -1;
    }

    void printUsage() {
        println(name + " (c) saga 2024");
        println("");
        println("    " + oneLineDescription);
        println("");
        println("Parameters:");
        println("");
        println("    generate - generates time-based one-time password");
        println("               expects seed on stdin");
        println("");
    }

    static long currentCounter() {
        return System.currentTimeMillis() / 30_000;
    }

    static long secondsLeft(long counter) {
        long secondsPassed = (System.currentTimeMillis() - counter * 30_000) / 1000;
        return (secondsPassed >= 30) ? 0 : (30 - secondsPassed);
    }

    int generateTotp(ArgList args) throws Exception {
        Scanner stdin = new Scanner(System.in);
        String seed = stdin.next();

        DefaultCodeGenerator generator = new DefaultCodeGenerator();
        long counter = currentCounter();
        long validForSecs = secondsLeft(counter);

        String otp = generator.generate(seed, counter);

        System.out.println("");
        System.out.println("    Time-based OTP : " + otp);
        System.out.println("    Seconds left   : " + validForSecs);
        System.out.println("");

        return 0;
    }

}
