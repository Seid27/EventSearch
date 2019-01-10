package com.example.yd3843oa.eventsearch;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpotifyTopArtists extends Fragment {

    //container variables
    private ArrayList<TopArtist> spotifyTopArtists = new ArrayList<TopArtist>();

    public SpotifyTopArtists() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spotify_top_artists, container, false);

        Bundle bundle = getArguments();

        spotifyTopArtists = bundle.getParcelableArrayList("spotifyTopArtists");
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        CustomeAdapter topArtistAdapter = new CustomeAdapter(spotifyTopArtists,getContext());

        //Recycler view that displays the results of topArtists from a user's spotify account
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(topArtistAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                View v = recyclerView.getChildViewHolder(view).itemView;
                TextView artist_name = v.findViewById(R.id.artist_name);
                ImageView artist_image = v.findViewById(R.id.artist_img);
                Log.d("artistName",artist_name.getText().toString());


                Bitmap bitmap = ((BitmapDrawable)artist_image.getDrawable()).getBitmap();

                EventInfoFragment eventInfoFragment = new EventInfoFragment();

                Bundle sendData = new Bundle();
                sendData.putString("artistName", String.valueOf(artist_name.getText()));
                sendData.putParcelable("imageBitmap",bitmap);

                eventInfoFragment.setArguments(sendData);

                getFragmentManager().beginTransaction().replace(R.id.top_artists,
                        eventInfoFragment,"fragment_event_info").addToBackStack(null).commit();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }




}
