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
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RXMessing {

    @Test
    public void messingAboutWithParallelizing() throws Exception {
        jsonParsingWithExecutor(Schedulers.computation());
//        jsonParsingWithExecutor(Schedulers.from(Executors.newFixedThreadPool(20)));
    }

    private void jsonParsingWithExecutor(Scheduler scheduler) {
        Date start = new Date();
        Observable.fromCallable(new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                return getJson();
            }
        }).flatMap(new Func1<JSONObject, Observable<JSONObject>>(){
            @Override
            public Observable<JSONObject> call(final JSONObject jsonObject) {
                return Observable.create(new Observable.OnSubscribe<JSONObject>() {

                    @Override
                    public void call(Subscriber<? super JSONObject> subscriber) {
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i=0; i<data.length(); i++) {
                                System.out.println("Next in data on " + Thread.currentThread().getName());
                                subscriber.onNext(data.getJSONObject(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        })
        .observeOn(scheduler)
        .map(new Func1<JSONObject, String>() {

            @Override
            public String call(JSONObject jsonObject) {
                try {
                    System.out.println("Getting title_short on " + Thread.currentThread().getName());
                    Thread.sleep(100);
                    return jsonObject.getString("title_short");
                } catch (JSONException | InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        })
                .subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                System.out.println("Completed");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String string) {
                System.out.println(string);
            }
        });


        System.out.println("Elapsed: " + (new Date().getTime()-start.getTime()));
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
            System.out.println("Making call on thread: " + Thread.currentThread().getName());
            URL url = new URL("https://api.deezer.com/user/2529/flow");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
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
}