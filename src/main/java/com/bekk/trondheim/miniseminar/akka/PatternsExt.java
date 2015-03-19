package com.bekk.trondheim.miniseminar.akka;


import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.bekk.trondheim.miniseminar.func.Functions;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

import java.util.concurrent.CompletableFuture;

public class PatternsExt {

    private final ExecutionContext executionContext;
    private final Timeout timeout;

    public PatternsExt(ExecutionContext executionContext, Timeout timeout) {this.executionContext = executionContext;
        this.timeout = timeout;
    }

    public <T> CompletableFuture<T> ask(ActorRef ref, Object msg) {
        Future<Object> future = Patterns.ask(ref, msg, timeout);
        CompletableFuture<T> cf = new CompletableFuture<>();
        future.onComplete(Functions.onComplete(res -> {
            if(res.isFailure()) {
                cf.completeExceptionally(res.failed().get());
            }
            else {
                cf.complete((T)res.get());
            }
        }), executionContext);
        return cf;
    }

}
