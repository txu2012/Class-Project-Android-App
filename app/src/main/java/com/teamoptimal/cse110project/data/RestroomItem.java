package com.teamoptimal.cse110project.data;

public class RestroomItem {
    String restroomID;
    String title;
    String distance;
    String tags;
    double rating;
    float color;

    public RestroomItem(String restroomID, String title, String distance, String tags,
                        double rating, float color) {
        this.restroomID = restroomID;
        this.title = title;
        this.distance = distance;
        this.tags = tags;
        this.rating = rating;
        this.color = color;
    }

    public String getRestroomID() { return restroomID; }
    public void setRestroomID(String restroomID) { this.restroomID = restroomID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public float getColor() { return color; }
    public void setColor(float color) { this.color = color; }
}
