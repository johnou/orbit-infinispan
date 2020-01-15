package com.sulake.proto.jgroups;

import cloud.orbit.actors.cluster.ClusterPeer;
import cloud.orbit.actors.cluster.DistributedMap;
import org.infinispan.AdvancedCache;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
public interface ExtendedClusterPeer extends ClusterPeer {
    <K, V> DistributedMap<K, V> getCache(String cacheName);
    <K, V> AdvancedCache<K, V> getAdvancedCache(String cacheName);
}
