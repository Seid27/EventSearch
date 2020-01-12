package com.example.yd3843oa.eventsearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;

public class MainActivity extends AppCompatActivity {

    // spotify api variables
    private static String CLIENT_ID = "3b205d2169bb47078180e248a6a171ca";
    private static String REDIRECT_URI = "com.example.yd3843oa.eventsearch://callback/";
    private String accessToken = null;

    //container variables
    private ArrayList<TopArtist> spotifyTopArtists = new ArrayList<TopArtist>();

    //navigation bar
    private BottomNavigationView nav;

    //progress dialog while data is being collected from spotify API
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Authorization from spotify
        // A request is sent using AuthenticationRequest builder
        // After a request a token is sent back
        // the token can only be used to access user-top-read
        AuthenticationRequest.Builder authenticationRequest = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        authenticationRequest.setScopes(new String[]{"user-top-read"});
        AuthenticationRequest request = authenticationRequest.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        //Bottom navigation bar
        nav = (BottomNavigationView) findViewById(R.id.navigation);
        nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListner);


    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    public void onConnected(final String t) throws IOException {

        //progress dialog while data is being collected from spotify API
        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();


        //used an OkHttpClient to convert from Http2 to Http1
        OkHttpClient client = new OkHttpClient.Builder().protocols(Arrays.asList(Protocol.HTTP_1_1)).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        //use the interface Api class
        Api api = retrofit.create(Api.class);

        //getTopArtists scope requires an access token
        //GET http request is sent to the SPotify api
        Call<JsonObject> call = api.getTopArtists("Bearer "+accessToken, String.valueOf(20));
        call.enqueue(new Callback<JsonObject>() {
                         @Override
                         public void onResponse(Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                             //Log.d("onresponse", String.valueOf(response.code()));

                             //JSON resposnse from Spotify API
                             //Store the response into a JsonArray
                             JsonArray artistArray = (JsonArray) response.body().get("items");
                             for (int i = 0; i < artistArray.size(); i++){

                                 //initiate TopArtist class to store name, images, genres
                                 TopArtist topArtist = new TopArtist((JsonObject) artistArray.get(i));

                                 //store topArtist object in spotifyTopArtists array
                                 spotifyTopArtists.add(topArtist);
                                 //Log.d("onresponse", String.valueOf(topArtist.getImageUrl()));
                             }



                             // spotifyTopArtists array is sent to SpotifyTopArtists fragment
                             Bundle bundleList = new Bundle();
                             bundleList.putParcelableArrayList("spotifyTopArtists", spotifyTopArtists);
                             Log.d("sizeOf", String.valueOf(spotifyTopArtists.size()));

                             SpotifyTopArtists spotifyArtists = new SpotifyTopArtists();
                             spotifyArtists.setArguments(bundleList);

                             //SpotifyTopArtists fragment is shown on the screen
                             getSupportFragmentManager().beginTransaction().replace(R.id.top_artists,
                                     spotifyArtists, "fragment_spotify_top_artists").commit();
                             dialog.dismiss();
                         }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("onresponse", t.getMessage());
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // A token is sent back after AuthenticationRequest
        if (requestCode == REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

            //Log.d("This is my Token", response.getAccessToken());
            accessToken = response.getAccessToken();
        }

        try {
            nav.setVisibility(View.VISIBLE);
            onConnected(accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        //if home button is clicked show spotifyArtists fragment
                        case R.id.home:

                            //send spotifyTopArtists array to SpotifyTopArtists fragment
                            Bundle bundleList = new Bundle();
                            bundleList.putParcelableArrayList("spotifyTopArtists", spotifyTopArtists);

                            SpotifyTopArtists spotifyArtists = new SpotifyTopArtists();
                            spotifyArtists.setArguments(bundleList);
                            getSupportFragmentManager().beginTransaction().replace(R.id.top_artists,
                                    spotifyArtists, "fragment_spotify_top_artists").commit();
                            return true;


                            //if search button is clicked
                        case R.id.search:
                            Bundle bundle = new Bundle();
                            if (!accessToken.equals("")) {
                                //send access token to searchResult fragment
                                bundle.putString("accessToken", accessToken);
                                SearchResult searchResult = new SearchResult();
                                searchResult.setArguments(bundle);

                                //switch screen to SearchResult fragment
                                getSupportFragmentManager().beginTransaction().replace(R.id.top_artists,
                                        searchResult, "fragment_search_result").commit();

                            } else {
                                //switch screen to SearchResult fragment
                                getSupportFragmentManager().beginTransaction().replace(R.id.top_artists,
                                        (new SearchResult()), "fragment_search_result").commit();
                            }

                            return true;
                    }

                    return false;
                }
            };

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}


//        OkHttpClient client = new OkHttpClient.Builder().protocols(Arrays.asList(Protocol.HTTP_1_1)).build();
//
//        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/me/top/artists").newBuilder();
//        urlBuilder.addQueryParameter("limit", String.valueOf(20));
//        String url = urlBuilder.build().toString();
//
//        Request request = new Request.Builder()
//                .url("https://api.spotify.com/v1/me/top/artists")
//                .addHeader("Authorization" , "Bearer "+ accessToken)
//                .build();
//
//        client.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(okhttp3.Call call, IOException e) {
//                Log.d("onresponse", "failed");
//            }
//
//            @Override
//            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
//                String ob = response.body().string();
//                Log.d("onresponse", String.valueOf(ob));
//            }
//        });
