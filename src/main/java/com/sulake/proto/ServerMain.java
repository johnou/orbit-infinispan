package com.sulake.proto;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
public class ServerMain {

    static {
        System.setProperty("jgroups.bind_addr", "127.0.0.1");
        System.setProperty("jgroups.tcpping.initial_hosts", "127.0.0.1[7800]");
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static void main(String[] args) {
        final OrbitService orbitService = new OrbitService("test", "server-node", true, false);
        orbitService.start();

        final Object lock = new Object();
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        orbitService.stop();
    }
}
