package com.mytest;

import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.http.ActionCreator;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Result;

public class MyActionCreator implements ActionCreator {

    private static final Logger LOG = LoggerFactory.getLogger(MyActionCreator.class);

    @Override
    public Action<?> createAction(final Request request, final Method actionMethod) {
        System.out.println("Create Action - Thread: " + Thread.currentThread().getId());

        return new Action.Simple() {

            @Override
            public CompletionStage<Result> call(final Context ctx) {
                System.out.println("Call - Thread: " + Thread.currentThread().getId());
                return this.delegate.call(ctx);
            }

        };
    }

}
