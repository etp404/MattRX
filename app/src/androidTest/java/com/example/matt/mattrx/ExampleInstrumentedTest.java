package com.example.matt.mattrx;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void threadMessin() throws Exception {
        Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Log.v("TEST_MATT", "Callable on " + Thread.currentThread().getName());
                return "Hello";
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        Log.v("TEST_MATT", "Converting to upper case on thread " + Thread.currentThread().getName());
                        return s.toUpperCase();
                    }
                })
                .observeOn(Schedulers.newThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        Log.v("TEST_MATT", "Adding world on thread " + Thread.currentThread().getName());
                        return s + " world";
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String string) {
                        Log.v("TEST_MATT", "Writing out " + Thread.currentThread().getName());
                        Log.v("TEST_MATT", string);
                    }
                });
    }
}
