package saga.tools.totp;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import java.util.Scanner;
import saga.tools.Tool;
import saga.util.ArgList;
import static saga.util.Equal.equal;
import saga.util.SystemIn;
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

    int generateTotp(ArgList args) throws Exception {
        Scanner stdin = new Scanner(System.in);
        String seed = stdin.next();
        
        DefaultCodeGenerator generator = new DefaultCodeGenerator();
        long counter = currentCounter();

        String otp = generator.generate(seed, counter);

        System.out.print(otp);
        return 0;
    }

}
