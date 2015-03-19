package com.bekk.trondheim.miniseminar.func;

import akka.dispatch.OnComplete;
import scala.Function1;
import scala.runtime.AbstractFunction1;
import scala.util.Failure;
import scala.util.Success;
import scala.util.Try;

import java.util.function.Consumer;
import java.util.function.Function;

public class Functions {

    public static <T, R> Function1<T, R> f(Function<T, R> func) {
        return new AbstractFunction1<T, R>() {
            @Override
            public R apply(T value) {
                return func.apply(value);
            }
        };
    }

    public static <T> OnComplete<T> onComplete(Consumer<Try<T>> handler) {
        return new OnComplete<T>() {
            @Override
            public void onComplete(Throwable failure, T success) throws Throwable {
                if(failure != null) {
                    handler.accept(Failure.apply(failure));
                }
                else {
                    handler.accept(Success.apply(success));
                }
            }
        };
    }

}
