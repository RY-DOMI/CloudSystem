package net.labormc.slave.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (c) Ryixz 2015 - 2022. All rights reserved.
 * Project : LaborCloud
 * Author  : Ryixz (Dominik Auer)
 */
public class PortBlocker {

    private final List<Integer> blockedPortList = new LinkedList<>();

    public void blockPort(int port) {
        try {
            this.blockedPortList.add(port);
            new ProcessBuilder("iptables", "-A", "INPUT", "-p", "tcp", "--src",
                    InetAddress.getLocalHost().getHostAddress(), "--dport", port + "", "-j", "ACCEPT").start();
            new ProcessBuilder("iptables", "-A", "INPUT", "-p", "tcp", "--dport", port + "", "-j", "DROP")
                    .start();
        } catch (IOException ex) {
            Logger.getLogger(PortBlocker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void unblockPort(int port) {
        try {
            this.blockedPortList.remove((Integer) port);
            new ProcessBuilder("iptables", "-D", "INPUT", "-p", "tcp", "--dport", port + "", "-j", "DROP")
                    .start();
        } catch (IOException ex) {
            Logger.getLogger(PortBlocker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int generateRandomPort() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            final int random = ThreadLocalRandom.current().nextInt(33000, 60000);
            if (this.isAvailable(random))
                return random;
        }
        return 0;
    }

    private boolean isAvailable(int port) {
        try {
            final ServerSocket socket = new ServerSocket(port);
            socket.close();
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }
}
