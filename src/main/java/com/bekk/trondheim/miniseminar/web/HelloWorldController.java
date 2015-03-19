package com.bekk.trondheim.miniseminar.web;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.bekk.trondheim.miniseminar.akka.HelloWorldActor;
import com.bekk.trondheim.miniseminar.akka.PatternsExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;

@RestController
public class HelloWorldController {

    @Autowired
    ActorSystem system;

    @Autowired
    PatternsExt patterns;

    @RequestMapping("/hello")
    public DeferredResult<String> helloWorld(@RequestParam("name") String name) {
        ActorRef helloActor = system.actorOf(HelloWorldActor.mkProps());
        DeferredResult<String> result = new DeferredResult<>();

        CompletableFuture<HelloWorldActor.HelloWorldResponse> futureResult = patterns.ask(helloActor, new HelloWorldActor.HelloWorld(name));
        futureResult.whenComplete((resp, err) -> {
            if(err != null) {
                result.setErrorResult(err);
            }
            else {
                result.setResult(resp.message);
            }
        });

        return result;
    }

}
