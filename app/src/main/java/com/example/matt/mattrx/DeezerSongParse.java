package com.example.matt.mattrx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DeezerSongParse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jsonParsingWithExecutor(Schedulers.computation());
        jsonParsingWithExecutor(Schedulers.from(Executors.newFixedThreadPool(5)));
    }

    private void jsonParsingWithExecutor(final Scheduler scheduler) {
        final long start = new Date().getTime();
        Observable.fromCallable(new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                return getJson();
            }
        }).subscribeOn(Schedulers.io())
                .flatMap(new Func1<JSONObject, Observable<JSONObject>>() {
                            @Override
                            public Observable<JSONObject> call(final JSONObject jsonObject) {
                                return Observable.create(new Observable.OnSubscribe<JSONObject>() {

                                    @Override
                                    public void call(final Subscriber<? super JSONObject> subscriber) {
                                        try {
                                            Log.v("RXMessing", "Data being iterated");
                                            final JSONArray data = jsonObject.getJSONArray("data");
                                            for (int i = 0; i < data.length(); i++) {
                                                try {
                                                    subscriber.onNext(data.getJSONObject(i));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            subscriber.onCompleted();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).observeOn(Schedulers.computation());
                            }
                        }).observeOn(Schedulers.immediate()).subscribe(new Observer<JSONObject>() {
            @Override
            public void onCompleted() {
                Log.v("RXMessing", "Completed in " + (new Date().getTime()-start));

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(JSONObject jsonObject) {
                Log.v("RXMessing","starting onNext for " + jsonObject.hashCode() + " on " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.v("RXMessing","completing onNext for " + jsonObject.hashCode());
            }
        });
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private JSONObject getJson() {
        try {
            Log.v("RXMessing", "Making call on thread: " + Thread.currentThread().getName());
            URL url = new URL("https://api.deezer.com/user/2529/flow");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            Log.v("RXMessing", "Returning JSON object");
            return new JSONObject(jsonText);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getColorList() throws InterruptedException, IOException {
        URL url = new URL("https://api.deezer.com/user/2529");
        URLConnection urlConnection = url.openConnection();
        InputStream is = urlConnection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        return response.toString();
    }
}
