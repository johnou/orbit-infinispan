package com.sulake.proto;

import cloud.orbit.actors.Actor;
import com.sulake.proto.actor.Hello;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
public class ClientMain {

    private static final Logger logger = LogManager.getLogger(ClientMain.class.getName());

    static {
        System.setProperty("jgroups.bind_addr", "127.0.0.1");
        System.setProperty("jgroups.tcpping.initial_hosts", "127.0.0.1[7800]");
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static void main(String[] args) {
        final OrbitService orbitService = new OrbitService("test", "client-node", false, true);
        orbitService.start();
        logger.info("Actor response: " + Actor.getReference(Hello.class, "1").hello().join());
        orbitService.stop();
    }
}
