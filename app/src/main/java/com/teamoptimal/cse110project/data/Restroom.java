package com.teamoptimal.cse110project.data;

import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAutoGeneratedKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.teamoptimal.cse110project.CreateRestroomActivity;
import com.teamoptimal.cse110project.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DynamoDBTable(tableName = "Y4R_Restrooms")
public class Restroom {
    private static final String TAG = "Restroom";
    private String id;
    private double longitude;
    private double latitude;
    private String userEmail;
    private String description;
    private float color;
    private String tags;
    private String floor;
    private double rating;
    private int ratingsCount;
    private int reports;

    private int SIZEOF_GENDER=3;
    private int SIZEOF_ACCESS=3;
    private int SIZEOF_EXTRANEOUS=8;

    public static final float[] colors = { BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_ROSE, BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_AZURE };

    public Restroom () {
        userEmail = "";
        longitude = 0.0d;
        latitude = 0.0d;
        description = "";
        tags = "0000000000000000000000000000000";
        floor = "1";
        rating = 0.00;
        ratingsCount = 1;
    }

    @DynamoDBHashKey (attributeName = "ID")
    @DynamoDBAutoGeneratedKey
    public String getID() { return id; }
    public void setID(String id) { this.id = id; }

    @DynamoDBAttribute (attributeName = "User")
    public String getUser() { return userEmail; }
    public void setUser(String userEmail) { this.userEmail = userEmail; }

    @DynamoDBAttribute (attributeName = "Latitude")
    public double getLatitude() { return latitude;}
    public void setLatitude(double latitude) { this.latitude = latitude; }

    @DynamoDBAttribute(attributeName = "Longitude")
    public double getLongitude() { return longitude;}
    public void setLongitude(double longitude) { this.longitude = longitude;}

    @DynamoDBAttribute (attributeName = "Description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @DynamoDBAttribute (attributeName = "Tags")
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    @DynamoDBIgnore
    public static String getFormattedTags(String tags) {
        String result = "";
        for(int i = 0; i < CreateRestroomActivity.tags.length; i++) {
            if(tags.charAt(i) == '1') {
                result += CreateRestroomActivity.tags[i].toLowerCase() + ", ";
            }
        }
        if(result != "")
            result = result.substring(0, result.length() - 2);
        else
            result = "No tags";

        return result;
    }

    @DynamoDBIgnore
    public String getFormattedTags() {
        String result = "";
        for(int i = 0; i < CreateRestroomActivity.tags.length; i++) {
            if(getTags().charAt(i) == '1') {
                result += CreateRestroomActivity.tags[i].toLowerCase() + ", ";
            }
        }
        if(result != "")
            result = result.substring(0, result.length() - 2);
        else
            result = "No tags";

        return result;
    }

    @DynamoDBIgnore
    public String getFormattedTags(boolean firstCapitalized) {
        String result = getFormattedTags();
        if(firstCapitalized)
            return result.substring(0, 1).toUpperCase() + result.substring(1);
        else
            return result;
    }

    @DynamoDBAttribute (attributeName = "Floor")
    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    @DynamoDBIgnore
    public float getColor() { return color; }
    public void setColor(float color) { this.color = color; }

    @DynamoDBAttribute(attributeName = "Rating")
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    @DynamoDBAttribute(attributeName = "NumberOfRatings")
    public int getRatingsCount() { return ratingsCount; }
    public void setRatingsCount(int ratingsCount) { this.ratingsCount = ratingsCount; }

    @DynamoDBAttribute(attributeName = "Times_Reported")
    public int getReportCount(){return reports;}
    public void setReportCount(int count){reports = count;}

    @DynamoDBIgnore
    public void setGender(int index){
        if(index < 0 || index >=SIZEOF_GENDER) return;
        char[] chars = tags.toCharArray();
        for(int i=0; i<SIZEOF_GENDER;i++){
            chars[i]= '0';
        }
        chars[index] = '1';
        tags = String.valueOf(chars);
    }

    @DynamoDBIgnore
    public void setAccess(int index){
        if(index < 0 || index >=SIZEOF_ACCESS) return;
        char[] chars = tags.toCharArray();
        for(int i=0; i<SIZEOF_ACCESS;i++){
            chars[i+SIZEOF_GENDER]= '0';
        }
        chars[index+SIZEOF_GENDER] = '1';
        tags = String.valueOf(chars);
    }

    @DynamoDBIgnore
    public void setExtraneous(int index, boolean choice){
        if(index < 0 || index >=(31-SIZEOF_GENDER-SIZEOF_ACCESS)) return;
        char[] chars = tags.toCharArray();
        if(choice) chars[index+SIZEOF_GENDER+SIZEOF_ACCESS]='1';
        else chars[index+SIZEOF_GENDER+SIZEOF_ACCESS] = '0';
        tags = String.valueOf(chars);
    }

    @DynamoDBIgnore
    public void setTag(int index, boolean choice){
        char[] chars = tags.toCharArray();
        if (choice)chars[index] = 1;
        else chars[index] = 0;
        tags = String.valueOf(chars);
    }

    @DynamoDBIgnore
    public boolean filtersThrough(String filter){
        if(filter.length()==0) return true;
        if(filter.length()<31){
            for(int c=0; c<31-filter.length(); c++){
                filter+="0";
            }
        }
        char[] myTags = tags.toCharArray();
        char[] filterTags = filter.toCharArray();
        for(int i=0; i<31;i++){
            if(filterTags[i] == '1'){
                if(myTags[i] != '1') return false;
            }
        }
        return true;
    }

    @DynamoDBIgnore
    public double[] getLocation() {
        double[] location = new double[2];
        location[0] = latitude;
        location[1] = longitude;
        return location;
    }

    @DynamoDBIgnore
    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @DynamoDBIgnore
    public double updateRating(double review) {
        double sum = review + (rating * ratingsCount);
        double val = sum / (++ratingsCount);

        Log.d(TAG, "average = "+val);
        setRating(val);

        return val;
    }

    @DynamoDBIgnore
    public void addReport(){reports++;}

    public void create() {
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        mapper.save(this);
    }

    @DynamoDBIgnore
    public static ArrayList<Restroom> getRestrooms(double latitude, double longitude, double diameter) {
        Log.d(TAG, "getRestrooms");
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        Log.d(TAG, latitude + ", " + longitude);

        Condition minLatitude = new Condition()
                .withComparisonOperator(ComparisonOperator.GE)
                .withAttributeValueList(new AttributeValue().withN("" + (latitude - (diameter / 2))));
        Condition maxLatitude = new Condition()
                .withComparisonOperator(ComparisonOperator.LE)
                .withAttributeValueList(new AttributeValue().withN("" + (latitude + (diameter / 2))));
        Condition minLongitude = new Condition()
                .withComparisonOperator(ComparisonOperator.GE)
                .withAttributeValueList(new AttributeValue().withN("" + (longitude - (diameter / 2))));
        Condition maxLongitude = new Condition()
                .withComparisonOperator(ComparisonOperator.LE)
                .withAttributeValueList(new AttributeValue().withN("" + (longitude + (diameter / 2))));

        Map<String, Condition> conditions = new HashMap<String, Condition>();
        conditions.put("Latitude", minLatitude);
        conditions.put("Latitude", maxLatitude);
        conditions.put("Longitude", minLongitude);
        conditions.put("Longitude", maxLongitude);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.setScanFilter(conditions);

        List<Restroom> scanResult;
        ArrayList<Restroom> returnVal = new ArrayList<>();

        try{
            scanResult = mapper.scan(Restroom.class, scanExpression);
        }catch(AmazonClientException s){
            Log.d(TAG, "AmazonClientException Thrown, returning empty list");
            return returnVal;
        }

        Log.d(TAG, "scanResult, " + scanResult.size());


        int colorIndex = 0;
        for(Restroom restroom : scanResult) {
            if(restroom.getReportCount()<5) {
                float color = Restroom.colors[colorIndex];
                restroom.setColor(color);
                if (colorIndex == 9)
                    colorIndex = 0;
                else
                    colorIndex++;
                returnVal.add(restroom);
            }
        }
        return returnVal;
    }

    @DynamoDBIgnore
    public static ArrayList<Restroom> filterRestrooms(List<Restroom> scan, String filter, double rated){
        ArrayList<Restroom> ret = new ArrayList<Restroom>();
        for(int i =0;i<scan.size(); i++){
            Restroom temp = scan.get(i);

            Log.d(TAG, temp.getTags()+" - "+temp.getRating());
            Log.d(TAG, filter+" - "+rated);
            Log.d(TAG, " "+temp.filtersThrough(filter));

            if(temp.filtersThrough(filter) && rated <= temp.getRating()){
                ret.add(temp);
            }
        }
        Log.d(TAG, ""+scan.size());
        Log.d(TAG, ""+ret.size());
        return ret;
    }
}