package saga.ip;

import java.util.List;
import saga.Tool;
import saga.util.ArgList;

import static java.util.Arrays.asList;
import static saga.util.ListUtils.newList;
import static saga.util.SystemOut.println;
import static saga.util.Equal.equal;
import static saga.util.ExceptionUtils.exception;
import static saga.ip.IPAddress.of;
import static saga.util.TextUtils.alignLeft;
import static saga.util.TextUtils.alignRight;

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
        if (equal(args.head(), "print")) {
            args.removeHead();
            return doPrintIPs(args);
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
        println("    Subnet must be IP address followed by '/' and mask 1..32 like:");
        println("");
        println("        192.168.0.0/16");
        println("");
        println("        192.168.0.0 used as subnet has mask 32 like 192.168.0.0/32");
        println("");
        println("    Multiply IP's and subnets must be comma separated like:");
        println("");
        println("        192.168.3.12,192.168.0.0/16");
        println("");
        println("Parameters:");
        println("");
        println("    print  ips-and-subnets                              - prints in binary");
        println("    check  ips-and-subnets  'contains'  ips-and-subnets - under construction");
        println("");
    }

    int doCheckIPRanges(ArgList arg) {
        List<IPSubnet> ipRanges = doDoReadIPRanges(arg);
        List<IPAddress> candidateList = doDoReadCandidateIPs(arg);
        println(ipRanges);
        println(candidateList);
        return 0;
    }

    List<IPSubnet> doDoReadIPRanges(ArgList args) {
        if (args.isEmpty()) {
            throw exception("Expected comma-separated IP4 addresses and subnet masks.");
        }
        String commaSeparatedIps = args.removeHead();
        return doGetIPRangesFrom(commaSeparatedIps);
    }

    static List<IPSubnet> doGetIPRangesFrom(String commaSeparatedIps) {
        List<String> ipStrings = asList(commaSeparatedIps.split("\\,"));
        List<IPSubnet> ipRanges = newList();
        for (String ipString : ipStrings) {
            if (IPSubnet.isValid(ipString)) {
                ipRanges.add(IPSubnet.of(ipString));
            } else if (IPAddress.isValid(ipString)) {
                ipRanges.add(IPSubnet.of(IPAddress.of(ipString)));
            } else {
                throw exception("Expected IP4 address or subnet but found: " + ipString);
            }
        }
        return ipRanges;
    }

    List<IPAddress> doDoReadCandidateIPs(ArgList args) {
        List<IPAddress> candidateList = newList();
        while (!args.isEmpty()) {
            candidateList.add(of(args.removeHead()));
        }
        return candidateList;
    }

    int doPrintIPs(ArgList args) {
        List<IPSubnet> ipRanges = doDoReadIPRanges(args);
        println("");
        println("                  ---------   ---------   ---------   ---------");
        ipRanges.forEach(range -> {
            println("  "
                    + alignLeft("" + range.address(), 15)
                    + " "
                    + range.address().asBinaryString()
            );
            if (!range.isSingleAddress()) {
                println("  "
                        + alignRight("/" + range.mask().bitCount(), 15)
                        + " "
                        + range.mask().asBinaryString()
                );
            }
        });
        println("                  ---------   ---------   ---------   ---------");
        println("");
        return 0;
    }

}
