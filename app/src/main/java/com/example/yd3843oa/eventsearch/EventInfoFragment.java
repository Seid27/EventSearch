package com.example.yd3843oa.eventsearch;

import android.app.ProgressDialog;
import android.arch.core.util.Function;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class EventInfoFragment extends Fragment {

    String api_key = "ioFPg1YZl38GXmcMjAAMYTj3298jN59T";

    ImageView artist_img;
    TextView textView;

    ConstraintLayout no_eventLayout;
    //CardView there_is_event;
    ViewPager event_viewpager;
    int position;
    ProgressDialog dialog;
    private Handler handler;
    RecyclerView recyclerView;

    List<EventInfo> eventInfoList = new ArrayList<EventInfo>();

    private Context context;

    private ViewPager viewPager;
    CustomViewPagerAdapter mAdapter;

    private final int delay = 2000;
    private int page = 0;
//    Runnable runnable = new Runnable() {
//        public void run() {
//            if (mAdapter.getCount() == page) {
//                page = 0;
//            } else {
//                page++;
//            }
//            viewPager.setCurrentItem(page, true);
//            handler.postDelayed(this, delay);
//        }
//    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event_information, container, false);

        //recyclerView = v.findViewById(R.id.event_info_recycler);
        artist_img = v.findViewById(R.id.artist_img);
        textView = v.findViewById(R.id.event_artist_name);

        no_eventLayout = v.findViewById(R.id.no_eventLayout);
        event_viewpager = v.findViewById(R.id.event_viewpager);
        //there_is_event = v.findViewById(R.id.there_is_event);
        context = getContext();

        Bundle bundle = getArguments();
        Bitmap bitmap = bundle.getParcelable("imageBitmap");
        String artistName = bundle.getString("artistName");

        artist_img.setImageBitmap(bitmap);

        viewPager = v.findViewById(R.id.event_viewpager);

        //displays the artist name
        textView.setText(bundle.getString("artistName"));

        dialog = new ProgressDialog(getContext()); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();

        try {
            getEventInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return v;
    }

    public void getEventInfo() throws IOException {

        final String[] artistID = new String[1];
        //used an OkHttpClient to convert from Http2 to Http1
        OkHttpClient client = new OkHttpClient.Builder().protocols(Arrays.asList(Protocol.HTTP_1_1)).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Api.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);


        //http request to the ticketmaster api
        //to get event information for an artist
        Call<JsonObject> call = api.getAttractions(api_key, textView.getText().toString(), "Music");
        //Log.d("onresponse2", String.valueOf(call.request()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Log.d("onresponse2", String.valueOf(response.code()));
                // Log.d("onresponse2", String.valueOf(response.body()));

                JsonObject Jobject = response.body();

                if (response.body().has("_embedded")) {
                    JsonObject jobjAttraction = (JsonObject) Jobject.get("_embedded");
                    //Log.d("resdiscovery", String.valueOf(jobjAttraction));
                    JsonArray attractionArray = jobjAttraction.getAsJsonArray("attractions");
                    //Log.d("resdiscovery", String.valueOf(attractionArray.get(0)));
                    JsonObject jobjAttraction2 = (JsonObject) attractionArray.get(0);
                    //Log.d("resdiscovery", String.valueOf(jobjAttraction2));
                    String id = jobjAttraction2.get("id").getAsString();

                    //used an OkHttpClient to convert from Http2 to Http1
                    OkHttpClient client = new OkHttpClient.Builder().protocols(Arrays.asList(Protocol.HTTP_1_1)).build();
                    Retrofit retrofit_events = new Retrofit.Builder()
                            .client(client)
                            .baseUrl(Api.ROOT_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

//                    String start_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//                    Log.d("onresponse2", start_date);
//                    start_date += "T00:00:00Z";
                    String start_date = "2019-01-09T03:00:00Z";
                    //2019-01-09


                    Api api_events = retrofit_events.create(Api.class);

                    Call<JsonObject> call_events = api_events.getEvents(api_key, "50", "Music", id,
                            start_date,"date,asc");
                    Log.d("onresponse2", String.valueOf(call_events.request()));

                    call_events.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            //Log.d("onresponse2", String.valueOf(response.code()));
                            //Log.d("onresponse2", String.valueOf(response.body()));

                            if (response.body().has("_embedded")) {
                                JsonObject eventsObject = (JsonObject) response.body().get("_embedded");
                                JsonArray eventsArray = (JsonArray) eventsObject.get("events");

                                for (int i = 0; i < eventsArray.size(); i++) {
                                    EventInfo eventInfo = new EventInfo((JsonObject) eventsArray.get(i));
                                    Log.d("eventResponse", eventInfo.getAddress());
                                    eventInfoList.add(eventInfo);
                                }

                                setUpView();

                            } else {
                                //no_eventLayout will be shown on screen
                                dialog.dismiss();
                                no_eventLayout.setVisibility(View.VISIBLE);
                                event_viewpager.setVisibility(View.GONE);
                                //there_is_event.setVisibility(View.GONE);

                            }

                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });
                }

                else{
                    //no_eventLayout will be shown on screen
                    dialog.dismiss();
                    no_eventLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.GONE);
                    //there_is_event.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }

        });

    }

    public void setUpView(){


        mAdapter = new CustomViewPagerAdapter(context, eventInfoList);


        viewPager.setAdapter(mAdapter);



        //Log.d("resultList",eventInfoList.get(position).getEvent());
//        EventInfoCustomAdapter eventInfoCustomAdapter = new EventInfoCustomAdapter(eventInfoList.get(position));
//
//
//        //Recycler view that displays the results of topArtists in a card view
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(eventInfoCustomAdapter);

        dialog.dismiss();


    }

}

























/**
 * A simple {@link Fragment} subclass.
 */
//public class EventInfoFragment extends Fragment {
//
//    String api_key = "ioFPg1YZl38GXmcMjAAMYTj3298jN59T";
//
//    //fragment_event_info layout variables
//    ImageView imageView;
//    ImageView calendar_img;
//    TextView textView;
//    EditText eventName;
//    EditText eventDate;
//    EditText eventTime;
//    EditText timeZone;
//    EditText location;
//    EditText venueName;
//    TextView eventLink;
//    ImageView prev_btn;
//    ImageView next_btn;
//
//    ConstraintLayout no_eventLayout;
//    CardView there_is_event;
//    int position;
//
//    ProgressDialog dialog;
//
//    //Container
//    ArrayList<EventInfo> eventInfoList = new ArrayList<EventInfo>();
//
//    public EventInfoFragment() {
//        // Required empty public constructor
//    }
//
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View v = inflater.inflate(R.layout.fragment_event_info, container, false);
//
//        imageView = v.findViewById(R.id.img_artist);
//        calendar_img = v.findViewById(R.id.calendar_img);
//        textView = v.findViewById(R.id.name_artist);
//        eventName = v.findViewById(R.id.event_name);
//        eventDate = v.findViewById(R.id.event_date);
//        eventTime = v.findViewById(R.id.event_time);
//        timeZone = v.findViewById(R.id.time_zone);
//        venueName = v.findViewById(R.id.venue_name);
//        location = v.findViewById(R.id.country_city_state);
//        eventLink = v.findViewById(R.id.event_link);
//        prev_btn = v.findViewById(R.id.prev_btn);
//        next_btn = v.findViewById(R.id.next_btn);
//
//        no_eventLayout = v.findViewById(R.id.no_eventLayout);
//        there_is_event = v.findViewById(R.id.there_is_event);
//
//        Bundle bundle = getArguments();
//        Bitmap bitmap = bundle.getParcelable("imageBitmap");
//        String artistName = bundle.getString("artistName");
//
//        imageView.setImageBitmap(bitmap);
//
//        //displays the artist name
//        textView.setText(bundle.getString("artistName"));
//
//        dialog = new ProgressDialog(getContext()); // this = YourActivity
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        dialog.setTitle("Loading");
//        dialog.setMessage("Loading. Please wait...");
//        dialog.setIndeterminate(true);
//        dialog.setCanceledOnTouchOutside(false);
//
//        dialog.show();
//
//        try {
//            getEventInfo();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        //need to move the icon to the end of the page
//        calendar_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_INSERT);
//                intent.setType("vnd.android.cursor.item/event");
//                String[] arrOfDateStr = eventDate.getText().toString().split("-");
//                Calendar startTime = Calendar.getInstance();
//                startTime.set(Integer.valueOf(arrOfDateStr[0]),Integer.valueOf(arrOfDateStr[1])-1,Integer.valueOf(arrOfDateStr[2]));
//                Log.d("valueofevent",Integer.valueOf(arrOfDateStr[1]).toString());
//                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.getTimeInMillis());
//                intent.putExtra(CalendarContract.Events.TITLE, eventName.getText().toString());
//                Log.d("valueofevent",eventName.getText().toString() );
//                startActivity(intent);
//
////                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
////                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,endTime);
//                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
//
//                intent.putExtra(CalendarContract.Events.DESCRIPTION, "This is a sample description");
//                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "My Guest House");
//            }
//        });
//
//        eventLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!eventLink.getText().toString().equals("")){
//                    String url = eventLink.getText().toString();
//                    if (!url.startsWith("https://") && !url.startsWith("http://")){
//                        url = "http://" + url;
//                    }
//                    Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(openUrlIntent);
//                }
//            }
//        });
//
//        return v;
//    }
//
//    public void setUpInfo(EventInfo eventInfo){
//
//        eventName.setText(eventInfo.getEvent());
//        eventDate.setText(eventInfo.getEventDate());
//        eventTime.setText(eventInfo.getEventTime());
//        timeZone.setText(eventInfo.getTimeZone());
//        location.setText(eventInfo.getCountryCode()+ "/" + eventInfo.getCity() + "/" + eventInfo.getStateCode());
//        venueName.setText(eventInfo.getVenueName());
//        eventLink.setText(eventInfo.getEventLink());
//    }
//
//    public void getEventInfo() throws IOException {
//
//        final String[] artistID = new String[1];
//        //used an OkHttpClient to convert from Http2 to Http1
//        OkHttpClient client = new OkHttpClient.Builder().protocols(Arrays.asList(Protocol.HTTP_1_1)).build();
//        Retrofit retrofit = new Retrofit.Builder()
//                .client(client)
//                .baseUrl(Api.ROOT_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        Api api = retrofit.create(Api.class);
//
//
//        //http request to the ticketmaster api
//        //to get event information for an artist
//        Call<JsonObject> call = api.getAttractions(api_key, textView.getText().toString(), "Music");
//        //Log.d("onresponse2", String.valueOf(call.request()));
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                //Log.d("onresponse2", String.valueOf(response.code()));
//                // Log.d("onresponse2", String.valueOf(response.body()));
//
//                JsonObject Jobject = response.body();
//
//                if (response.body().has("_embedded")) {
//                    JsonObject jobjAttraction = (JsonObject) Jobject.get("_embedded");
//                    //Log.d("resdiscovery", String.valueOf(jobjAttraction));
//                    JsonArray attractionArray = jobjAttraction.getAsJsonArray("attractions");
//                    //Log.d("resdiscovery", String.valueOf(attractionArray.get(0)));
//                    JsonObject jobjAttraction2 = (JsonObject) attractionArray.get(0);
//                    //Log.d("resdiscovery", String.valueOf(jobjAttraction2));
//                    String id = jobjAttraction2.get("id").getAsString();
//
//                    //used an OkHttpClient to convert from Http2 to Http1
//                    OkHttpClient client = new OkHttpClient.Builder().protocols(Arrays.asList(Protocol.HTTP_1_1)).build();
//                    Retrofit retrofit_events = new Retrofit.Builder()
//                            .client(client)
//                            .baseUrl(Api.ROOT_URL)
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .build();
//
////                    String start_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
////                    Log.d("onresponse2", start_date);
////                    start_date += "T00:00:00Z";
//                    String start_date = "2019-01-09T03:00:00Z";
//                    //2019-01-09
//
//
//                    Api api_events = retrofit_events.create(Api.class);
//
//                    Call<JsonObject> call_events = api_events.getEvents(api_key, "5", "Music", id,
//                            start_date,"date,asc");
//                    Log.d("onresponse2", String.valueOf(call_events.request()));
//
//                    call_events.enqueue(new Callback<JsonObject>() {
//                        @Override
//                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                            //Log.d("onresponse2", String.valueOf(response.code()));
//                            //Log.d("onresponse2", String.valueOf(response.body()));
//
//                            if (response.body().has("_embedded")) {
//                                JsonObject eventsObject = (JsonObject) response.body().get("_embedded");
//                                JsonArray eventsArray = (JsonArray) eventsObject.get("events");
//
//                                for (int i = 0; i < eventsArray.size(); i++) {
//                                    EventInfo eventInfo = new EventInfo((JsonObject) eventsArray.get(i));
//                                    Log.d("eventResponse", eventInfo.getAddress());
//                                    eventInfoList.add(eventInfo);
//                                }
//
//                                browseEvents();
//
//                            } else {
//                                //no_eventLayout will be shown on screen
//                                dialog.dismiss();
//                                no_eventLayout.setVisibility(View.VISIBLE);
//                                there_is_event.setVisibility(View.GONE);
//
//                            }
//
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                        }
//                    });
//                }
//
//                else{
//                    //no_eventLayout will be shown on screen
//                    dialog.dismiss();
//                    no_eventLayout.setVisibility(View.VISIBLE);
//                    there_is_event.setVisibility(View.GONE);
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//
//            }
//
//        });
//
//    }
//
//    public void browseEvents(){
//
//        dialog.dismiss();
//        position = 0;
//        setUpInfo(eventInfoList.get(position));
//
//
//        //need to replace this button by a swpe
//
//        prev_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(position > 0){
//                    position--;
//                    setUpInfo(eventInfoList.get(position));
//                }
//            }
//        });
//
//        next_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(position < eventInfoList.size()-1){
//                    position++;
//                    setUpInfo(eventInfoList.get(position));
//                }
//            }
//        });
//
//
//
//    }

//}
