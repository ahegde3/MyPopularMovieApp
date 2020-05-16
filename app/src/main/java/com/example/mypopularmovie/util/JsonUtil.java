package com.example.mypopularmovie.util;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class JsonUtil {
    final static String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    final static String API_KEY = "49cedaedd21232e07523c4105be0c104";

    public static final String  TITLE = "title",
            OVERVIEW = "overview",
            RELEASE_DATE = "release_date",
            RATING = "vote_average",
            POSTER = "poster_path";
 public static Retrofit retroGetInstance(){
     HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
     interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
     OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

     Gson gson = new GsonBuilder()
             .setLenient()
             .create();

     Retrofit retrofit = new Retrofit.Builder()
             .baseUrl(MOVIEDB_BASE_URL)
             .client(client)
             .addConverterFactory(GsonConverterFactory.create(gson))
             .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
             .build();
     return retrofit;
 }

    public static URL buildUrl(String[] query) throws MalformedURLException {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(query[0])
                .appendQueryParameter("api_key", API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}

