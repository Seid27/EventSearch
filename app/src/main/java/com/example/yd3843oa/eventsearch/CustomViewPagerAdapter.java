package com.example.yd3843oa.eventsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CustomViewPagerAdapter extends PagerAdapter{
    private Context context;
    private List<EventInfo> eventInfoList;
    private LayoutInflater layoutInflater;
    RecyclerView recyclerView;


    CustomViewPagerAdapter(Context context, List<EventInfo> eventInfoList) {
        this.context = context;
        this.eventInfoList = eventInfoList;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return eventInfoList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == ((View)o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.each_viewcard, container, false);
        //TextView textView = view.findViewById(R.id.jusText);

        recyclerView = view.findViewById(R.id.event_info_recycler);

        Log.d("resultList", String.valueOf(position));
        EventInfoCustomAdapter eventInfoCustomAdapter = new EventInfoCustomAdapter(eventInfoList.get(position));


        //Log.d("result22", String.valueOf(eventInfoList.get(position)));
        //Recycler view that displays the results of topArtists in a card view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(eventInfoCustomAdapter);

        container.addView(view);
        return view;
    }
}
