package com.example.yd3843oa.eventsearch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class  EventInfo {
    private String event = "";
    private String eventDate = "";
    private String eventTime = "";
    private String venueName = "";
    private String city = "";
    private String countryCode = "";
    private String address = "";
    private String stateCode = "";
    private String timeZone = "";
    private String eventLink = "";

    public EventInfo(JsonObject jsonObject) {
        if(jsonObject.has("name")) {
            this.event = jsonObject.get("name").getAsString();
        }
        if(jsonObject.has("url")) {
            this.eventLink = jsonObject.get("url").getAsString();
        }
        JsonObject dateObject = (JsonObject) jsonObject.get("dates");
        if(dateObject.has("timezone")) {
            this.timeZone = dateObject.get("timezone").getAsString();
        }
        JsonObject start = (JsonObject) dateObject.get("start");
        if(start.has("localDate"))
            this.eventDate = start.get("localDate").getAsString();
        if(start.has("localTime"))
            this.eventTime = start.get("localTime").getAsString();
        JsonObject embedded = (JsonObject) jsonObject.get("_embedded");
        JsonArray venue = (JsonArray) embedded.get("venues");
        JsonObject venueObject = (JsonObject) venue.get(0);
        if(venueObject.has("name"))
            this.venueName = venueObject.get("name").getAsString();
        if(venueObject.has("city")) {
            JsonObject cityObject = (JsonObject) venueObject.get("city");
            this.city = cityObject.get("name").getAsString();
        }
        if (venueObject.has("country")) {
            JsonObject countryObject = (JsonObject) venueObject.get("country");
            this.countryCode = countryObject.get("countryCode").getAsString();
        }
        if (venueObject.has("address")) {
            JsonObject addressObject = (JsonObject) venueObject.get("address");
            this.address = addressObject.get("line1").getAsString();
        }
        if (venueObject.has("state")) {
            JsonObject stateObject = (JsonObject) venueObject.get("state");
            this.stateCode = stateObject.get("stateCode").getAsString();
        }

    }

    public String getEvent() {
        return event;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getVenueName() {
        return venueName;
    }

    public String getCity() {
        return city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getAddress() {
        return address;
    }

    public String getStateCode() {
        return stateCode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getEventLink() {
        return eventLink;
    }
}
