package com.example.gittersandsittersdatabase;

import android.graphics.Bitmap;
import android.location.Location;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;

/**
 * This class represents a HabitEvent in the HabitTracker app.
 */
public class HabitEvent implements Serializable, Comparable<HabitEvent> {

    private String eventID;
    private String parentHabitID;
    private String eventName;
    private Calendar eventDate;     // always today's date
    private String eventComment;
    private Location eventLocation;
    private Bitmap eventPhoto;

    // Constructor with the required attributes: name and date
    public HabitEvent(String eventID, String parentHabitID, String eventName, Calendar eventDate,
             String eventComment) {
        this.eventID = eventID;
        this.parentHabitID = parentHabitID;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventComment = eventComment;
    }

    // Constructor with the required attributes: name and date
    public HabitEvent(String parentHabitID, String eventName, Calendar eventDate,
             String eventComment) {
        this.eventID = "temp";
        this.parentHabitID = parentHabitID;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventComment = eventComment;
    }

    // Getters and Setters

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getParentHabitID() {
        return parentHabitID;
    }

    public void setParentHabitID(String parentHabitID) {
        this.parentHabitID = parentHabitID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Calendar getEventDate() {
        return eventDate;
    }

    public void setEventDate(Calendar eventDate) {
        // Set EventDate to today's date
        this.eventDate = eventDate;
    }

    public Location getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(Location eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventComment() {
        return eventComment;
    }

    public void setEventComment(String eventComment) {
        this.eventComment = eventComment;
    }

    public Bitmap getEventPhoto() {
        return eventPhoto;
    }

    public void setEventPhoto(Bitmap eventPhoto) {
        this.eventPhoto = eventPhoto;
    }


    /**
     * This method implements the HabitEvent sorting logic.
     * HabitEvents are to be sorted by the the eventDate attribute.
     */
    @Override
    public int compareTo(HabitEvent h) {
        return (this.getEventDate().compareTo(h.getEventDate()));
    }
}
