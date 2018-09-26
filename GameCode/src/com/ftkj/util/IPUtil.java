package com.ftkj.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IPUtil {

    public static String localIP = "";
    public static String testServerIP = "";

    static {
        localIP = getLocalIp();
        testServerIP = getTestServerIp();
    }

    /**
     * 本地IP
     *
     * @return
     */
    public static String getLocalIp() {
        try {
            if (isWindowsOS()) {
                return InetAddress.getLocalHost().getHostAddress();
            } else {
                return getLinuxLocalIp();
            }
        } catch (Exception e) {
        }
        return "127.0.0.1";
    }
    
    private static String getLinuxLocalIp() throws SocketException {
        String ip = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress().toString();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                                ip = ipaddress;
                                System.out.println(ipaddress);
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.println("获取ip地址异常");
            ip = "127.0.0.1";
            ex.printStackTrace();
        }
        //System.out.println("IP:"+ip);
        return ip;
    }
    public static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") > -1) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }
 

    /**
     * 测试服务器IP
     *
     * @return
     */
    public static String getTestServerIp() {
        return "192.168.10.181";
    }

    /**
     * Split a string in the form of "host:port host2:port" into a List of
     * InetSocketAddress instances suitable for instantiating a MemcachedClient.
     * <p>
     * Note that colon-delimited IPv6 is also supported. For example: ::1:11211
     */
    public static List<InetSocketAddress> getAddresses(String s) {
        if (s == null) {
            throw new NullPointerException("Null host list");
        }
        if (s.trim().equals("")) {
            throw new IllegalArgumentException("No hosts in list:  ``" + s
                    + "''");
        }
        s = s.trim();
        ArrayList<InetSocketAddress> addrs = new ArrayList<InetSocketAddress>();

        for (String hoststuff : s.split(" ")) {
            int finalColon = hoststuff.lastIndexOf(':');
            if (finalColon < 1) {
                throw new IllegalArgumentException("Invalid server ``"
                        + hoststuff + "'' in list:  " + s);

            }
            String hostPart = hoststuff.substring(0, finalColon).trim();
            String portNum = hoststuff.substring(finalColon + 1).trim();

            addrs
                    .add(new InetSocketAddress(hostPart, Integer
                            .parseInt(portNum)));
        }
        assert !addrs.isEmpty() : "No addrs found";
        return addrs;
    }

    public static InetSocketAddress getOneAddress(String server) {
        if (server == null) {
            throw new NullPointerException("Null host");
        }
        if (server.trim().equals("")) {
            throw new IllegalArgumentException("No hosts in:  ``" + server
                    + "''");
        }
        server = server.trim();
        int finalColon = server.lastIndexOf(':');
        if (finalColon < 1) {
            throw new IllegalArgumentException("Invalid server ``" + server
                    + "''");

        }
        String hostPart = server.substring(0, finalColon).trim();
        String portNum = server.substring(finalColon + 1).trim();
        return new InetSocketAddress(hostPart, Integer.parseInt(portNum));
    }

}
