package com.thisobeystudio.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thisobeystudio on 7/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

public class Step implements Parcelable {

    private final int mStepId;
    private final String mStepShortDescription;
    private final String mStepDescription;
    private final String mStepVideoURL;
    private final String mStepThumbnailURL;

    public Step(int mStepId,
                String mStepShortDescription,
                String mStepDescription,
                String mStepVideoURL,
                String mStepThumbnailURL) {
        this.mStepId = mStepId;
        this.mStepShortDescription = mStepShortDescription;
        this.mStepDescription = mStepDescription;
        this.mStepVideoURL = mStepVideoURL;
        this.mStepThumbnailURL = mStepThumbnailURL;
    }

    private Step(Parcel in) {
        this.mStepId = in.readInt();
        this.mStepShortDescription = in.readString();
        this.mStepDescription = in.readString();
        this.mStepVideoURL = in.readString();
        this.mStepThumbnailURL = in.readString();
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel source) {
            return new Step(source);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStepId);
        dest.writeString(this.mStepShortDescription);
        dest.writeString(this.mStepDescription);
        dest.writeString(this.mStepVideoURL);
        dest.writeString(this.mStepThumbnailURL);
    }

    public int getStepId() {
        return mStepId;
    }

    public String getStepShortDescription() {
        return mStepShortDescription;
    }

    public String getStepVideoURL() {
        return mStepVideoURL;
    }

    public String getStepThumbnailURL() {
        return mStepThumbnailURL;
    }
}