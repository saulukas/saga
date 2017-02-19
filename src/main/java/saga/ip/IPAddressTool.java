package saga.ip;

import saga.Tool;

import static saga.util.SystemOut.println;

public class IPAddressTool extends Tool {

    public IPAddressTool() {
        super("ip", "IP v4 addresses and subnets");
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length < 1)
        {
            println(name + " (c) saga 2017");
            println("");
            println("    " + oneLineDescription);
            println("");
            println("    IP address must be four decimal numbers 0..255 separated by '.' like:");
            println("");
            println("        192.168.3.12");
            println("");
            println("    Subnet must be IP address followed by '/' and number 1..32 like:");
            println("");
            println("        192.168.0.0/16");
            println("");
            println("Parameters:");
            println("");
            println("    address/subnet" + args.length);
            println("");
            return 0;
        }



        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
