package com.sulake.proto.actor;

import cloud.orbit.actors.runtime.AbstractActor;
import cloud.orbit.concurrent.Task;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
public class HelloActor extends AbstractActor implements Hello {

    @Override
    public Task<String> hello() {
        return Task.fromValue("huuhaa");
    }
}
