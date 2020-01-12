package com.example.yd3843oa.eventsearch;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// This class contains attributes that replicate the
// objects of JSON responce from getTopArtists call.
public class TopArtist implements Parcelable{

    private String name;
    private JsonArray images;
    private JsonArray genres;
    private String imageUrl;
    private String genre;


    //TopArtist constructor
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

    private TopArtist(Parcel in) {
        name = in.readString();
        imageUrl = in.readString();
        genre = in.readString();
    }

    public static final Creator<TopArtist> CREATOR = new Creator<TopArtist>() {
        @Override
        public TopArtist createFromParcel(Parcel in) {
            return new TopArtist(in);
        }

        @Override
        public TopArtist[] newArray(int size) {
            return new TopArtist[size];
        }
    };

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
        parcel.writeString(name);
        parcel.writeString(imageUrl);
        parcel.writeString(genre);
    }
}
