package com.example.matt.mattrx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView)findViewById(R.id.textview);
        textView.setText("starting");

        Observable<String> listObservable = Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getColorList();
            }
        });

        listObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String string) {
                        textView.setText(string);
                    }
                });
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
