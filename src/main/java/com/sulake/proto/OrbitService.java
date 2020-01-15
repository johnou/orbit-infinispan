package com.sulake.proto;

import cloud.orbit.actors.Stage;
import cloud.orbit.actors.extensions.ActorClassFinder;
import cloud.orbit.actors.runtime.FastActorClassFinder;
import cloud.orbit.actors.runtime.KryoSerializer;
import cloud.orbit.actors.runtime.Messaging;
import cloud.orbit.actors.runtime.NodeCapabilities;
import com.sulake.proto.jgroups.JGroupsClusterPeer;
import com.sulake.proto.util.ExecutorUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
public class OrbitService {

    private static final Logger logger = LogManager.getLogger(OrbitService.class.getName());

    private final String clusterName;
    private final String nodeName;
    private final boolean activations;
    private final boolean zeroCapacity;

    private ExecutorService orbitExecutionPool;
    private Stage stage;

    public OrbitService(String clusterName, String nodeName, boolean activations, boolean zeroCapacity) {
        this.clusterName = clusterName;
        this.nodeName = nodeName;
        this.activations = activations;
        this.zeroCapacity = zeroCapacity;
    }

    public void start() {
        orbitExecutionPool = ExecutorUtils.newScalingThreadPool("OrbitThread-", 16, false);

        ActorClassFinder actorClassFinder = new FastActorClassFinder("com.sulake");
        Stage.Builder builder = new Stage.Builder();
        builder.extensions(actorClassFinder);

        builder.clusterPeer(new JGroupsClusterPeer("classpath:/jgroups-ec2.xml", orbitExecutionPool, zeroCapacity));

        builder.clusterName(clusterName);
        builder.nodeName(nodeName);

        builder.executionPool(orbitExecutionPool);

        Messaging messaging = new Messaging();
        messaging.setResponseTimeoutMillis(TimeUnit.SECONDS.toMillis(6));
        builder.messaging(messaging);

        builder.enableShutdownHook(false); // invoked manually
        builder.broadcastActorDeactivations(false); // taken care of by the cache ttl

        KryoSerializer kryoSerializer = new KryoSerializer();
        builder.objectCloner(kryoSerializer);
        builder.messageSerializer(kryoSerializer);
        builder.messageLoopbackObjectCloner(kryoSerializer);
        builder.localAddressCacheMaximumSize(40_000);
        builder.deactivationTimeout(15, TimeUnit.SECONDS);
        builder.numReminderControllers(6); // sharded reminders

        stage = builder.build();
        stage.setMessageSerializer(kryoSerializer);
        stage.setMode(activations ? Stage.StageMode.HOST : Stage.StageMode.CLIENT);

        stage.start().join();
    }

    public void stop() {
        if (NodeCapabilities.NodeState.RUNNING.equals(stage.getState())) {
            logger.info("Shutting down stage");
            stage.stop().join();
        }
        ExecutorUtils.shutdown(orbitExecutionPool, logger, () -> {});
    }
}
