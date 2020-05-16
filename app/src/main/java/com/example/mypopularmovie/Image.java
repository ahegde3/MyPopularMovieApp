package com.example.mypopularmovie;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.example.mypopularmovie.models.Movie;

public class Image extends RecyclerView.Adapter<Image.ViewHolder> {
    private final Movie[] mv;
    private final Context context;

    public Image(Movie[] mv, Context cnt) {
        this.mv = mv;
        context = cnt;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(ImageView imgi) {
            super(imgi);
            img = imgi;
        }
    }

    public Image.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {


        ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Picasso.with(context)
                .load(mv[position].getPosterPath())
                .fit()
                .error(R.mipmap.ic_launcher_round)
                .placeholder(R.mipmap.ic_launcher_round)
                .into((ImageView) holder.img.findViewById(R.id.imageView2));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, MovieDetail.class);
            intent.putExtra("movie", mv[position]);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (mv == null || mv.length == 0) {
            return -1;
        }

        return mv.length;
    }

}