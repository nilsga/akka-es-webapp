package com.bekk.trondheim.miniseminar.akka;

import akka.actor.AbstractActor;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import static akka.japi.pf.ReceiveBuilder.match;
import static akka.japi.pf.ReceiveBuilder.matchAny;

public class ActorWithImmutableState extends AbstractActor {


    public ActorWithImmutableState() {
        receive(matchAny(msg -> unhandled(msg)).build());
    }

    @Override
    public void preStart() throws Exception {
        context().become(immutableReceive(0));
    }

    public PartialFunction<Object, BoxedUnit> immutableReceive(int state) {
        return match(Increment.class, msg -> context().become(immutableReceive(state + 1)))
                .match(Decrement.class, msg -> context().become(immutableReceive(state - 1)))
                .match(Get.class, msg -> context().sender().tell(state, self()))
                .build();
    }

    public static class Increment {}
    public static class Decrement {}
    public static class Get {}

}
