package com.thisobeystudio.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by thisobeystudio on 7/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

public class Recipe implements Parcelable {

    private final Integer mRecipeId;
    private final String mRecipeName;
    private ArrayList<Ingredient> mRecipeIngredients = null;
    private ArrayList<Step> mRecipeSteps = null;
    private final Integer mRecipeServings;
    private final int mRecipeImage;
    private final String mRecipeImageUrl;

    public Recipe(Integer mRecipeId,
                  String mRecipeName,
                  ArrayList<Ingredient> mRecipeIngredients,
                  ArrayList<Step> mRecipeSteps,
                  Integer mRecipeServings,
                  int mRecipeImage,
                  String mRecipeImageUrl) {
        this.mRecipeId = mRecipeId;
        this.mRecipeName = mRecipeName;
        this.mRecipeIngredients = mRecipeIngredients;
        this.mRecipeSteps = mRecipeSteps;
        this.mRecipeServings = mRecipeServings;
        this.mRecipeImage = mRecipeImage;
        this.mRecipeImageUrl = mRecipeImageUrl;
    }

    private Recipe(Parcel in) {
        this.mRecipeId = in.readInt();
        this.mRecipeName = in.readString();
        this.mRecipeIngredients = new ArrayList<>();
        in.readList(this.mRecipeIngredients, Ingredient.class.getClassLoader());
        this.mRecipeSteps = new ArrayList<>();
        in.readList(this.mRecipeSteps, Step.class.getClassLoader());
        this.mRecipeServings = in.readInt();
        this.mRecipeImage = in.readInt();
        this.mRecipeImageUrl = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mRecipeId);
        parcel.writeString(this.mRecipeName);
        parcel.writeList(this.mRecipeIngredients);
        parcel.writeList(this.mRecipeSteps);
        parcel.writeInt(this.mRecipeServings);
        parcel.writeInt(this.mRecipeImage);
        parcel.writeString(this.mRecipeImageUrl);
    }

    public String getRecipeName() {
        return mRecipeName;
    }

    public ArrayList<Ingredient> getRecipeIngredients() {
        return mRecipeIngredients;
    }

    public ArrayList<Step> getRecipeSteps() {
        return mRecipeSteps;
    }

    public Integer getRecipeServings() {
        return mRecipeServings;
    }

    public Integer getRecipeImage() {
        return mRecipeImage;
    }

    public String getRecipeImageUrl() {
        return mRecipeImageUrl;
    }
}
