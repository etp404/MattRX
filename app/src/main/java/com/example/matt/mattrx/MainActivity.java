package com.example.matt.mattrx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final long startTime = new Date().getTime();
        final List<String> names = new ArrayList<String>() {{
            add("Ringo");
            add("John");
            add("Paul");
            add("George");
            add("Ringo");
            add("John");
            add("Paul");
            add("George");
            add("Ringo");
            add("John");
            add("Paul");
            add("George");
            add("Ringo");
            add("John");
            add("Paul");
            add("George");
            add("Ringo");
            add("John");
            add("Paul");
            add("George");
            add("Ringo");
            add("John");
            add("Paul");
            add("George");
            add("Ringo");
            add("John");
            add("Paul");
            add("George");
            add("Ringo");
            add("John");
            add("Paul");
            add("George");
        }};

        final Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(5));

        Observable.just(names).subscribeOn(Schedulers.io())
                .flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(final List<String> names) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(final Subscriber<? super String> subscriber) {
                                for (final String name : names) {
                                    Observable
                                            .just(name)
                                            .observeOn(scheduler)
                                            .map(new ExpensiveOperation())
                                            .subscribe(new Subscriber<String>() {
                                                @Override
                                                public void onCompleted() {

                                                }

                                                @Override
                                                public void onError(Throwable e) {

                                                }

                                                @Override
                                                public void onNext(String s) {
                                                    subscriber.onNext(name);
                                                }
                                            });
                                }
                            }
                        }).subscribeOn(Schedulers.computation());
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
                Log.v("RXExample", string + " on " + Thread.currentThread().getName());
                Log.v("RXExample", "Elapsed time " + (new Date().getTime() - startTime));
            }
        });
    }

    private static class ExpensiveOperation implements Func1<String, String> {
        @Override
        public String call(String s) {
            //Simulate expensive operation
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return s.toUpperCase();
        }
    }
}
