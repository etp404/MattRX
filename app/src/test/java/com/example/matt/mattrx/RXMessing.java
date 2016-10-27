package com.example.matt.mattrx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RXMessing {
    @Test
    public void addition_isCorrect() throws Exception {

        Observable<JSONObject> gettObservable = Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(Subscriber<? super JSONObject> subscriber) {
                try {
                    URL url = new URL("https://api.deezer.com/user/2529/flow");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    JSONObject json = new JSONObject(jsonText);
                    subscriber.onNext(json);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        gettObservable.flatMap(new Func1<JSONObject, Observable<JSONObject>>(){

            @Override
            public Observable<JSONObject> call(final JSONObject jsonObject) {
                return Observable.create(new Observable.OnSubscribe<JSONObject>() {

                    @Override
                    public void call(Subscriber<? super JSONObject> subscriber) {
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i=0; i<data.length(); i++) {
                                subscriber.onNext(data.getJSONObject(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).
        subscribe(new Observer<JSONObject>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(JSONObject jsonObject) {
                System.out.println(jsonObject);
            }
        });

//        Observable<String> observable = subject.flatMap(new Func1<PairOfString, Observable<String>>() {
//            @Override
//            public Observable<String> call(final PairOfString pairOfString) {
//                return Observable.create(new Observable.OnSubscribe<String>() {
//                    @Override
//                    public void call(Subscriber<? super String> subscriber) {
//                        subscriber.onNext(pairOfString.hello.toUpperCase());
//                        subscriber.onNext(pairOfString.world.toUpperCase());
//                    }
//                });
//            }
//        });


//        Observable<String> observable = subject.flatMap(new Func1<Object, Observable<? extends String>>() {
//            @Override
//            public Observable<String> call(final String s) {
//                return Observable.create(new Observable.OnSubscribe<String>() {
//                                             @Override
//                                             public void call(Subscriber<? super String> subscriber) {
//                                                 subscriber.onNext(s.toUpperCase());
//                                                 subscriber.onNext(s.substring(0,2));
//                                             }
//                                         });
//            }
//        });

//        observable.subscribe(new Observer<String>() {
//            @Override
//            public void onCompleted() {
//                System.out.println("completed");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onNext(String string) {
//                System.out.println(string);
//            }
//        });
//        subject.onCompleted();
//
//        subject.onNext(new PairOfString("hello again", "new world"));

    }

    private class PairOfString {
        private final String hello;
        private final String world;

        public PairOfString(String hello, String world) {
            this.hello = hello;
            this.world = world;
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}