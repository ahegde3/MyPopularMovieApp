package com.example.mypopularmovie;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.mypopularmovie.models.Movie;
import com.example.mypopularmovie.util.JsonUtil;
import com.example.mypopularmovie.util.SharedPrefUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private RecyclerView.LayoutManager manager;
    private int column=2;
    private final String POPULAR_QUERY = "popular";
    private final String TOP_RATED_QUERY = "top_rated";
    Spinner spin;
    Movie[] movies;
    Image mImageAdapter;
    Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting recycler View
        rv=findViewById(R.id.rv);
        manager= new GridLayoutManager(this,column);
        rv.setLayoutManager(manager);
        int defaultlayout= SharedPrefUtil.getItemType(this);
        if(defaultlayout==1) {
            setGridLayout();
    } else {
        setListLayout();
    }
        //setting spinner
        spin=findViewById(R.id.spinner);
        /*JsonUtil.retroGetInstance().create(MovieService.class)
                    .getMovie("49cedaedd21232e07523c4105be0c104")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DisposableObserver<Movie>() {


                        @Override
                        public void onNext(Movie movie) {
                            mImageAdapter = new Image(movies,getApplicationContext());
                            rv.setAdapter(mImageAdapter);
                        }

                        @Override
                        public void onError(Throwable e) {
                      Log.d("retro","onError"+e.toString());
                        }

                        @Override
                        public void onComplete() {
                     Log.d("retro","complete");
                        }
                    });*/
        final int currentSelection=spin.getSelectedItemPosition();
        if(true){
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentSelection == i) {
                    // If most popular was selected
                    new FetchDataAsyncTask().execute(POPULAR_QUERY);
                } else {
                    // If top rated was selected
                    new FetchDataAsyncTask().execute(TOP_RATED_QUERY);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });}
        else {
            Log.d("MainActivty", "Network not connected");
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mMenu = menu;
        updateMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_list_item:
                setListLayout();
                break;
            case R.id.action_grid_item:
                setGridLayout();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void setGridLayout() {
        SharedPrefUtil.saveItemType(this, SharedPrefUtil.GRID_LAYOUT_ID);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rv.setLayoutManager(gridLayoutManager);
        updateMenu();
      }
    private void setListLayout() {
        SharedPrefUtil.saveItemType(this, SharedPrefUtil.LIST_LAYOUT_ID);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(linearLayoutManager);
        updateMenu();
    }

    private void updateMenu() {
        if (mMenu == null) {
            return;
        }
        int defaultLayout = SharedPrefUtil.getItemType(this);
        if (defaultLayout == SharedPrefUtil.GRID_LAYOUT_ID) {
            mMenu.findItem(R.id.action_grid_item).setVisible(false);
            mMenu.findItem(R.id.action_list_item).setVisible(true);
        } else {
            mMenu.findItem(R.id.action_grid_item).setVisible(true);
            mMenu.findItem(R.id.action_list_item).setVisible(false);
        }
    }
    public class FetchDataAsyncTask extends AsyncTask<String, Void, Movie[]> {
        public FetchDataAsyncTask() {
            super();
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            // Holds data returned from the API
            String movieSearchResults = null;

            try {
                URL url = JsonUtil.buildUrl(params);
                movieSearchResults = JsonUtil.getResponseFromHttpUrl(url);

                if(movieSearchResults == null) {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }

            try {
                return makeMoviesDataToArray (movieSearchResults);
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            return null;
        }

        protected void onPostExecute(Movie[] movies) {
            mImageAdapter = new Image(movies,getApplicationContext());
            rv.setAdapter(mImageAdapter);
        }
    }
//    private boolean isNetworkConnected() {
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
//    }
public Movie[] makeMoviesDataToArray(String moviesJsonResults) throws JSONException {
    // JSON filters
    final String RESULTS = "results";
    final String ORIGINAL_TITLE = "original_title";
    final String POSTER_PATH = "poster_path";
    final String OVERVIEW = "overview";
    final String VOTER_AVERAGE = "vote_average";
    final String RELEASE_DATE = "release_date";

    // Get results as an array
    JSONObject moviesJson = new JSONObject(moviesJsonResults);
    JSONArray resultsArray = moviesJson.getJSONArray(RESULTS);

    // Create array of Movie objects that stores data from the JSON string
    movies = new Movie[resultsArray.length()];

    // Go through movies one by one and get data
    for (int i = 0; i < resultsArray.length(); i++) {
        // Initialize each object before it can be used
        movies[i] = new Movie();

        // Object contains all tags we're looking for
        JSONObject movieInfo = resultsArray.getJSONObject(i);

        // Store data in movie object
        movies[i].setOriginalTitle(movieInfo.getString(ORIGINAL_TITLE));
        movies[i].setPosterPath(movieInfo.getString(POSTER_PATH));
        movies[i].setOverview(movieInfo.getString(OVERVIEW));
        movies[i].setVoterAverage(movieInfo.getDouble(VOTER_AVERAGE));
        movies[i].setReleaseDate(movieInfo.getString(RELEASE_DATE));
    }
    return movies;
}/*
public interface MovieService{
        @GET("movie/popular")
        Observable<Movie> getMovie(@Query("api_key") String api_key) ;
}*/
}
