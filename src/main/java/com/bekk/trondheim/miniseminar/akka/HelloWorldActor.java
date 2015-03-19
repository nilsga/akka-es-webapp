package com.bekk.trondheim.miniseminar.akka;

import akka.actor.AbstractActor;
import akka.actor.Props;

import static akka.japi.pf.ReceiveBuilder.match;

public class HelloWorldActor extends AbstractActor  {

    public static class HelloWorld {
        public String name;
        public HelloWorld(String name) {this.name = name;}
    }

    public static class HelloWorldResponse {
        public String message;
        public HelloWorldResponse(String message) {this.message = message;}
    }

    public static Props mkProps() {
        return Props.create(HelloWorldActor.class, HelloWorldActor::new);
    }

    public HelloWorldActor() {
        receive(match(HelloWorld.class, msg -> {
            sender().tell(new HelloWorldResponse("Hello " + msg.name), self());
        }).build());
    }

}
