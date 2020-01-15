package com.sulake.proto.actor;

import cloud.orbit.actors.Actor;
import cloud.orbit.concurrent.Task;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
public interface Hello extends Actor {
    Task<String> hello();
}
