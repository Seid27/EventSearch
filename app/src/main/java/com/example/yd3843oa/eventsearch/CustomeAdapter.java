package com.example.yd3843oa.eventsearch;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CustomeAdapter extends RecyclerView.Adapter<CustomeAdapter.MyViewHolder> {

    private List<TopArtist> topArtistsList;
    private Context context;

    public CustomeAdapter(List<TopArtist> topArtistsList, Context context) {
        this.topArtistsList = topArtistsList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView artistImg;
        TextView artistName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImg = itemView.findViewById(R.id.artist_img);
            artistName = itemView.findViewById(R.id.artist_name);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.each_artist,viewGroup,
                false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        TopArtist topArtist = topArtistsList.get(i);
        Log.d("Aname",topArtist.getName());

        Log.d("Aname",topArtist.getName());
        //String imgUrl = String.valueOf(topArtist.getImageUrl());

        if (topArtist.getImageUrl() == null) {
            Picasso.with(context).load(R.drawable.no_image).resize(350, 350).into(myViewHolder.artistImg);
        }
        else {
            Picasso.with(context).load(String.valueOf(topArtist.getImageUrl())).resize(350, 350).into(myViewHolder.artistImg);
        }
        myViewHolder.artistName.setText(topArtist.getName());


    }

    @Override
    public int getItemCount() {
        return  topArtistsList.size();
    }




}
