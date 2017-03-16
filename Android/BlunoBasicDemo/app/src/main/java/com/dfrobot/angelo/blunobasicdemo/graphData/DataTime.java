package com.dfrobot.angelo.blunobasicdemo.graphData;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

/**
 * Created by Andrew on 3/14/2017.
 * tutorial: http://www.survivingwithandroid.com/2015/05/android-parcelable-tutorial-list-class-2.html
 * also good: https://guides.codepath.com/android/using-parcelable
 */

public class DataTime implements Parcelable {

    private long currTime;
    private float vitalVal;
    private String vitalMsg;    //ex: "20 breaths per min as of 12:00 Jan 1"

    // get and set methods
    public long getCurrTime(){return currTime;}
    public float getVitalVal(){return vitalVal;}
    public String getVitalMsg(){return vitalMsg;}

    public DataTime(long curr, float vVal, String vMsg){
        currTime = curr;
        vitalVal = vVal;
        vitalMsg = vMsg;
    }
    // De-parcel object. Must be in same order that we wrote the data
    private DataTime(Parcel in) {
        currTime =  in.readLong();
        vitalVal = in.readFloat();
        vitalMsg = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(currTime);
        dest.writeFloat(vitalVal);
        dest.writeString(vitalMsg);
    }

    // Creator
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataTime createFromParcel(Parcel in) {
            return new DataTime(in);
        }

        public DataTime[] newArray(int size) {
            return new DataTime[size];
        }
    };


}
