package saga.tools.ip;

import java.util.List;
import saga.tools.Tool;
import saga.util.ArgList;

import static java.util.Arrays.asList;
import static saga.util.ListUtils.newList;
import static saga.util.SystemOut.println;
import static saga.util.Equal.equal;
import static saga.util.ExceptionUtils.exception;
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
        println("    print  ips-and-subnets                   - prints in binary");
        println("    check  ips-and-subnets  'contains'  ips  - prints ips-and-subnets which contain given ips");
        println("");
    }

    int doCheckIPRanges(ArgList args) {
        List<IPSubnet> ipRanges = doDoReadIPRanges(args);
        if (args.isEmpty() || !equal(args.head(), "contains")) {
            throw exception("Expected 'contains' after IPs and subnets.");
        }
        args.removeHead();
        List<IPAddress> candidateList = doDoReadIPAddresses(args);

        print(ipRanges);

        boolean allAreContained = true;
        for (IPAddress address : candidateList) {
            boolean isContained = false;
            println("");
            println("");
            print(address);
            for (IPSubnet subnet : ipRanges) {
                if (subnet.conains(address)) {
                    isContained = true;
                    println("  --------------- ---------   ---------   ---------   ---------");
                    print(subnet);
                }
            }
            if (!isContained) {
                println("  --------------- is not contained !!! ------------------------");
            }
            allAreContained = allAreContained && isContained;
        }
        println("");

        return allAreContained ? 0 : 1;
    }

    List<IPSubnet> doDoReadIPRanges(ArgList args) {
        if (args.isEmpty()) {
            throw exception("Expected comma-separated IP4 addresses and subnet masks.");
        }
        return doGetIPRangesFrom(args.removeHead());
    }

    List<IPAddress> doDoReadIPAddresses(ArgList args) {
        if (args.isEmpty()) {
            throw exception("Expected comma-separated IP4 addresses.");
        }
        return doGetIPsFrom(args.removeHead());
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

    static List<IPAddress> doGetIPsFrom(String commaSeparatedIps) {
        List<String> ipStrings = asList(commaSeparatedIps.split("\\,"));
        List<IPAddress> ipAddresses = newList();
        for (String ipString : ipStrings) {
            if (IPAddress.isValid(ipString)) {
                ipAddresses.add(IPAddress.of(ipString));
            } else {
                throw exception("Expected IP4 address but found: " + ipString);
            }
        }
        return ipAddresses;
    }

    int doPrintIPs(ArgList args) {
        List<IPSubnet> ipRanges = doDoReadIPRanges(args);
        print(ipRanges);
        return 0;
    }

    private void print(IPAddress address) {
        println("  " + alignLeft("" + address, 15) + " " + address.asBinaryString());
    }

    private void print(IPMask mask) {
        println("  " + alignRight("/" + mask.bitCount(), 15) + " " + mask.asBinaryString());
    }

    private void print(IPSubnet subnet) {
        print(subnet.address());
        if (!subnet.isSingleAddress()) {
            print(subnet.mask());
        }
    }

    private void print(List<IPSubnet> subnets) {
        println("");
        println("                  ---------   ---------   ---------   ---------");
        subnets.forEach(subnet -> {
            print(subnet);
        });
        println("                  ---------   ---------   ---------   ---------");
        println("");
    }

}
