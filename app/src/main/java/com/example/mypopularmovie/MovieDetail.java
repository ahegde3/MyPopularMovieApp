package com.example.mypopularmovie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.example.mypopularmovie.models.Movie;
public class MovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        TextView originalTitleTV = (TextView) findViewById (R.id.titleTextView);
        TextView ratingTV = (TextView) findViewById (R.id.ratingTextView);
        TextView releaseDateTV = (TextView) findViewById (R.id.releaseDateTextView);
        TextView overviewTV = (TextView) findViewById (R.id.overviewTextView);
        ImageView posterIV = (ImageView) findViewById (R.id.posterImageView);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }  Movie movie;
        try {
             movie = (Movie)intent.getParcelableExtra("movie");
        }catch(Exception e) {throw e;}

        // TITLE
        originalTitleTV.setText(movie.getOriginalTitle());
        // VOTER AVERAGE / RATING
        ratingTV.setText (String.valueOf(movie.getVoteAverage()) + " / 10");
        // IMAGE
        Picasso.with(this)
                .load("https://image.tmdb.org/t/p/w500/"+movie.getPosterPath())
                .fit()
                .error(R.mipmap.ic_launcher_round)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(posterIV);

        // OVERVIEW
        overviewTV.setText (movie.getOverview ());

        // RELEASE DATE
        releaseDateTV.setText (movie.getReleaseDate());
    }


    private void closeOnError() {
        finish();
        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
    }
}

