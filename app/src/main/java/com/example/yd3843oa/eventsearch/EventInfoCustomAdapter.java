package com.example.yd3843oa.eventsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventInfoCustomAdapter extends RecyclerView.Adapter<EventInfoCustomAdapter.MyViewHolder>{

    private List<String> eventInfoList;
    private List<String> title;

    public EventInfoCustomAdapter(EventInfo event_Info){
        Log.d("resultList",event_Info.getEvent());
        eventInfoList = new ArrayList<String>();
        title = new ArrayList<String>();
        eventInfoList.add(event_Info.getEvent());
        eventInfoList.add(event_Info.getEventDate());
        eventInfoList.add(event_Info.getEventTime());
        eventInfoList.add(event_Info.getTimeZone());
        eventInfoList.add(event_Info.getCountryCode() + " " +
                event_Info.getCity() + " " + event_Info.getStateCode() );
        eventInfoList.add(event_Info.getVenueName());
        eventInfoList.add(event_Info.getAddress());
        eventInfoList.add(event_Info.getEventLink());
        title.add("Event");
        title.add("Date");
        title.add("Time");
        title.add("Time Zone");
        title.add("Country/City/State");
        title.add("Venue Name");
        title.add("Address");
        title.add("Link");
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txt_title;
        TextView txt_desc;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.text_title);
            txt_desc = itemView.findViewById(R.id.text_desc);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.each_info,viewGroup,
                false);
        return new MyViewHolder(itemView);
    }




    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.txt_title.setText(title.get(i));
        myViewHolder.txt_desc.setText(eventInfoList.get(i));

    }

    @Override
    public int getItemCount() {
        return eventInfoList.size();
    }


}
