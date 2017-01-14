package com.teamoptimal.cse110project.data;

import android.util.Log;
import android.widget.Toast;

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
import com.teamoptimal.cse110project.MainActivity;
import com.teamoptimal.cse110project.ReportActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DynamoDBTable(tableName = "Y4R_Reviews")
public class Review {
    private static final String TAG = "Review";
    private String ID;
    private String userEmail;
    private String message;
    private String restroomID;
    private float rating;
    private int thumbsUp;
    private int thumbsDown;
    private int flags;
    private int size;
    private int reports;
    private boolean reported;

    @DynamoDBHashKey(attributeName = "ID")
    @DynamoDBAutoGeneratedKey()
    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    @DynamoDBAttribute(attributeName = "UserEmail")
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    @DynamoDBAttribute(attributeName = "Message")
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @DynamoDBAttribute(attributeName = "Rating")
    public float getRating() { return rating; }
    public void setRating(float val) { rating = val; }

    @DynamoDBAttribute(attributeName = "ThumbsUp")
    public int getThumbsUp() { return thumbsUp; }
    public void setThumbsUp(int thumbsUp) { this.thumbsUp = thumbsUp; }

    @DynamoDBAttribute(attributeName = "ThumbsDown")
    public int getThumbsDown() { return thumbsDown; }
    public void setThumbsDown(int thumbsDown) { this.thumbsDown = thumbsDown; }

    @DynamoDBAttribute(attributeName = "Flags")
    public int getFlags() { return flags; }
    public void setFlags(int flags) { this.flags = flags; }

    @DynamoDBAttribute(attributeName = "RestroomID")
    public String getRestroomID() { return restroomID; }
    public void setRestroomID(String restroomID) { this.restroomID = restroomID; }

    @DynamoDBAttribute(attributeName = "TimesReported")
    public int getReportCount(){return reports;}
    public void setReportCount(int count){reports = count;}

    @DynamoDBIgnore
    public boolean isReported() { return reported; }
    public void setReported(boolean reported) { this.reported = reported; }

    @DynamoDBIgnore
    public void addReport(){reports++;}

    public void create() {
        AmazonDynamoDBClient ddb = ReportActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        mapper.save(this);
    }

    @DynamoDBIgnore
    public void createReview() {
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        mapper.save(this);
    }

    @DynamoDBIgnore
    public boolean isInitialized() {
        if(rating != 0.0 && !message.equals(""))
            return true;
        return false;
    }

    // Grabs rating for restroom of list of ratings
    @DynamoDBIgnore
    public static double getRating(String restroomID) {
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        Restroom curRestroom = mapper.load(Restroom.class, restroomID);
        return curRestroom.getRating();
    }

    @DynamoDBIgnore
    public double updateRating(String restroomID, double newRating) {
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        Restroom curRestroom = mapper.load(Restroom.class, restroomID);
        double average = curRestroom.updateRating(newRating);
        mapper.save(curRestroom);
        return average;

    }

    @DynamoDBIgnore
    public static Review getReview(String id) {
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        Review review = mapper.load(Review.class, id);
        return review;
    }

    @DynamoDBIgnore
    public static ArrayList<Review> getReviews(String currentID) {
        AmazonDynamoDBClient ddb = MainActivity.clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        Condition restroomID = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(currentID));

        Map<String, Condition> conditions = new HashMap<>();
        conditions.put("RestroomID", restroomID);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.setScanFilter(conditions);

        List<Review> scanReviews;
        ArrayList<Review> combined = new ArrayList<>();

        try{
            scanReviews = mapper.scan(Review.class, scanExpression);
        } catch(AmazonClientException a) {
            Log.d(TAG, "AmazonClientException Thrown, returning empty list");
            return combined;
        }

        for(Review review : scanReviews) {
            combined.add(review);
        }
        return combined;
    }
}
