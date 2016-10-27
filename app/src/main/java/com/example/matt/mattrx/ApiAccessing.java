package com.example.matt.mattrx;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class ApiAccessing {

    public static void main(String args[]) throws IOException, JSONException {
        System.out.println("Hello");
        URL url = new URL("https://api.deezer.com/user/2529/flow");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String responseMessage = connection.getResponseMessage();
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String jsonText = readAll(rd);
        JSONObject json = new JSONObject(jsonText);
        System.out.println(json.toString());
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
