package saga.ip;

import java.util.List;
import saga.Tool;
import saga.util.ArgList;

import static saga.util.ListUtils.newList;
import static saga.util.SystemOut.println;
import static saga.util.Equal.equal;
import static saga.util.ExceptionUtils.exception;

public class IP4AddressTool extends Tool {

    public IP4AddressTool() {
        super("ip4", "IP v4 addresses and subnets");
    }

    @Override
    public int run(String[] argArray) throws Exception {
        ArgList args = ArgList.of(argArray);
        if (args.isEmpty()) {
            printUsage();
            return 0;
        }
        if (equal(args.head(), "check")) {
            args.removeHead();
            return doCheckIPRanges(args);
        }
        printUsage();
        return -1;
    }

    void printUsage() {
        println(name + " (c) saga 2017");
        println("");
        println("    " + oneLineDescription);
        println("");
        println("    IP address must be four decimal numbers 0..255 separated by '.' like:");
        println("");
        println("        192.168.3.12");
        println("");
        println("    Subnet must be IP address followed by '/' and maskr 1..32 like:");
        println("");
        println("        192.168.0.0/16");
        println("");
        println("        192.168.0.0 used as subnet has mask 32 like 192.168.0.0/32");
        println("");
        println("Parameters:");
        println("");
        println("    check  addresses-or-subnets  contains  addresses-or-subnets");
        println("");
    }

    int doCheckIPRanges(ArgList arg) {
        List<IP4Subnet> ipRanges = doDoReadIPRanges(arg);
        List<IP4Address> candidateList = doDoReadCandidateIPs(arg);
        println(ipRanges);
        println(candidateList);
        return 0;
    }

    List<IP4Subnet> doDoReadIPRanges(ArgList args) throws RuntimeException {
        List<IP4Subnet> ipRanges = newList();
        while (!args.isEmpty() && !equal(args.head(), "contains")) {
            if (IP4Subnet.isValid(args.head())) {
                ipRanges.add(IP4Subnet.of(args.head()));
            } else if (IP4Address.isValid(args.head())) {
                ipRanges.add(IP4Subnet.of(IP4Address.of(args.head())));
            } else {
                throw exception("Expected IP4 address or subnet but found: " + args.head());
            }
            args.removeHead();
        }
        if (args.isEmpty()) {
            throw exception("Expected 'contains'.");
        }
        args.removeHead(); // skip 'contains'
        return ipRanges;
    }

    List<IP4Address> doDoReadCandidateIPs(ArgList args) {
        List<IP4Address> candidateList = newList();
        while (!args.isEmpty()) {
            candidateList.add(IP4Address.of(args.head()));
            args.removeHead();
        }
        return candidateList;
    }

}
