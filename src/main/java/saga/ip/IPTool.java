package saga.ip;

import java.util.List;
import saga.Tool;
import saga.util.ArgList;

import static saga.util.ListUtils.newList;
import static saga.util.SystemOut.println;
import static saga.util.Equal.equal;
import static saga.util.ExceptionUtils.exception;
import static saga.ip.IPAddress.of;

public class IPTool extends Tool {

    public IPTool() {
        super("ip", "IP v4 addresses and subnets");
    }

    @Override
    public int run(String[] argArray) {
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
        List<IPSubnet> ipRanges = doDoReadIPRanges(arg);
        List<IPAddress> candidateList = doDoReadCandidateIPs(arg);
        println(ipRanges);
        println(candidateList);
        return 0;
    }

    List<IPSubnet> doDoReadIPRanges(ArgList args) throws RuntimeException {
        List<IPSubnet> ipRanges = newList();
        while (!args.isEmpty() && !equal(args.head(), "contains")) {
            if (IPSubnet.isValid(args.head())) {
                ipRanges.add(IPSubnet.of(args.head()));
            } else if (IPAddress.isValid(args.head())) {
                ipRanges.add(IPSubnet.of(IPAddress.of(args.head())));
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

    List<IPAddress> doDoReadCandidateIPs(ArgList args) {
        List<IPAddress> candidateList = newList();
        while (!args.isEmpty()) {
            candidateList.add(of(args.removeHead()));
        }
        return candidateList;
    }

}
