package com.example.yd3843oa.eventsearch;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */

// This fragment class contains a text field where a user can input a search query
// The search query is an artist name
// Search is completed using SPotify api and an interface API class in this project
// Search results are displayed once the search is complete
// The search result will contain a picture and name of artists that have the same name
// as the search query strings
public class SearchResult extends Fragment {

    //layout variables
    EditText artist_search;
    Button search_btn;
    LinearLayout search_result_layout;
    TextView recent_search;

    //variables
    String previousSearchResult;
    String temp;

    RecyclerView search_recyclerView;

    //container variables
    private ArrayList<TopArtist> searchResult = new ArrayList<TopArtist>();

    //Bundle to get an access token
    private Bundle searchResultBundle;
    private String accessToken;

    //xml layout view related to this fragment
    private View v;

    public SearchResult() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        if (previousSearchResult != null) {
            Log.d("savedd",previousSearchResult);
            editor.putString("prevSearchResult", previousSearchResult);
            editor.apply();
        }
        else{
            editor.putString("prevSearchResult","");
            editor.apply();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_search_result, container, false);
        setRetainInstance(true);
        search_result_layout = v.findViewById(R.id.search_result_layout);
        recent_search = v.findViewById(R.id.recent_search);
        // getting saved data from shared prefercnce
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.apply();
        previousSearchResult = preferences.getString("prevSearchResult","");
        Log.d("savedd2",previousSearchResult);
        // if there is a save data which is the recent search by the user then
        // the view that shows the recent search will be visible
        // else invisible
        if (previousSearchResult.equals("")){
            Log.d("savedd2",previousSearchResult);
            search_result_layout.setVisibility(View.GONE);
        }

        else {
            recent_search.setText(previousSearchResult);
        }


        searchResultBundle = getArguments();
        accessToken = searchResultBundle.getString("accessToken");
        search_btn = v.findViewById(R.id.search_btn);
        artist_search = v.findViewById(R.id.artist_search);
        artist_search.setFocusableInTouchMode(true);
        artist_search.requestFocus();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

//        getActivity().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//

                artist_search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        if (artist_search.getText().toString().length() == 0) {
                            search_btn.setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (artist_search.getText().toString().length() > 0) {
                            search_btn.setEnabled(true);
                            recent_search.setText(previousSearchResult);
                        }
                    }
                });

        // onClickListenr for recent search
        // This will allow the user to search the previous search query without typing
        recent_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                artist_search.clearFocus();

                String artistName = String.valueOf(recent_search.getText());
                if (temp == null)
                    previousSearchResult = recent_search.getText().toString();
                else {
                    String temp2;
                    previousSearchResult = temp;
                }

                temp = recent_search.getText().toString();
                recent_search.setText(previousSearchResult);

                search(artistName);

            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                artist_search.clearFocus();
                if (temp != null)
                    recent_search.setText(temp);
                previousSearchResult = artist_search.getText().toString();
                temp = previousSearchResult;
                String artistName = String.valueOf(artist_search.getText());

                search(artistName);

            }
        });

        return v;
    }

    public void search(final String artistName){

        OkHttpClient client = new OkHttpClient.Builder().protocols(Arrays.asList(Protocol.HTTP_1_1)).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Api.SEARCH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        //getTopArtists scope requires an access token
        //getSearchItem is used to request to the spotify api
        Call<JsonObject> call = api.getSearchItem("Bearer "+accessToken,artistName,"artist");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject artistObject = (JsonObject) response.body().get("artists");
                JsonArray artistArray = (JsonArray) artistObject.get("items");
                searchResult.clear();
                for (int i = 0; i < artistArray.size(); i++){
                    TopArtist topArtist = new TopArtist((JsonObject) artistArray.get(i));
                    searchResult.add(topArtist);
                    Log.d("onresponse2", String.valueOf(topArtist.getImageUrl()));
                }

                artist_search.setText("");

                search_recyclerView = v.findViewById(R.id.search_recyclerView);
                CustomAdapter topArtistAdapter = new CustomAdapter(searchResult,getContext());

                //Recycler view that displays the results of from search query
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(v.getContext().getApplicationContext());
                search_recyclerView.setLayoutManager(layoutManager);
                search_recyclerView.setItemAnimator(new DefaultItemAnimator());
                search_recyclerView.setAdapter(topArtistAdapter);

                search_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), search_recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        View v = search_recyclerView.getChildViewHolder(view).itemView;
                        TextView artist_name = v.findViewById(R.id.artist_name);
                        ImageView artist_image = v.findViewById(R.id.artist_img);
                        Log.d("artistName",artist_name.getText().toString());

                        Bitmap bitmap = ((BitmapDrawable)artist_image.getDrawable()).getBitmap();

                        EventInfoFragment eventInfoFragment = new EventInfoFragment();

                        Bundle sendData = new Bundle();
                        sendData.putString("artistName", String.valueOf(artist_name.getText()));
                        sendData.putParcelable("imageBitmap",bitmap);

                        eventInfoFragment.setArguments(sendData);
                        previousSearchResult = artistName;
                        Bundle bundle = new Bundle();
                        bundle.putString("prevSearchResult", previousSearchResult);
                        onSaveInstanceState(bundle);
                        recent_search.setText(artistName);

                        getFragmentManager().beginTransaction().replace(R.id.top_artists,
                                eventInfoFragment,"fragment_event_info").addToBackStack(null).commit();

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }

                }));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

}
