package com.example.matt.mattrx;


import org.junit.Test;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ThreadsMessing {

    @Test
    public void threadMessin() throws Exception {
        Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("Callable on " + Thread.currentThread().getName());
                return "Hello";
            }
        })
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        System.out.println("Converting to upper case on thread " + Thread.currentThread().getName());
                        return s.toUpperCase();
                    }
                })
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        System.out.println("Adding world on thread " + Thread.currentThread().getName());
                        return s + " world";
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String string) {
                        System.out.println("Writing out " + Thread.currentThread().getName());
                        System.out.println(string);
                    }
                });
    }
}
