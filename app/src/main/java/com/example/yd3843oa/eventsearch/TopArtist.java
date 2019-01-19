package com.example.yd3843oa.eventsearch;


// This class contains attributes that replicate the objects of JSON responce from getTopArtists call.

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TopArtist implements Parcelable{

    private String name;
    private JsonArray images;
    private JsonArray genres;
    private String imageUrl;
    private String genre;

    public TopArtist(JsonObject jsonObject) {
        this.name = String.valueOf(jsonObject.get("name"));
        this.images = (JsonArray) jsonObject.get("images");
        this.genres = (JsonArray) jsonObject.get("genres");

        if (genres.size() != 0){
            genre = genres.get(0).getAsString();
        }
        else{
            genre = null;
        }

        if (images.size() != 0) {
            JsonObject artistImage = (JsonObject) images.get(1);
            if (artistImage != null) {
                imageUrl = artistImage.get("url").getAsString();
            }
            else{
                imageUrl = null;
            }
        }
    }

    public String getName() {
        return name;
    }

    public JsonArray getImages() {
        return images;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getGenre() {
        return genre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
