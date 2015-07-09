package com.example.shreekant.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shreekant on 7/9/2015.
 */
public class MovieData implements Parcelable {
    public String getId() {
        return mId;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    // No need to expose getPosterName()
    public String getPosterPath() {
        return "http://image.tmdb.org/t/p/w92" + mPosterName;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public String getOverview() {
        return mOverview;
    }

    private String mId;
    private String mOriginalTitle;
    private String mPosterName;
    private String mReleaseDate;
    private String mVoteAverage;
    private String mOverview;

    MovieData(String id, String originalTitle, String posterName, String releaseDate, String voteAverage, String overview){
        mId             = id;
        mOriginalTitle  = originalTitle;
        mPosterName     = posterName;
        mReleaseDate    = releaseDate;
        mVoteAverage    = voteAverage;
        mOverview       = overview;
    }


    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mId);
        out.writeString(mOriginalTitle);
        out.writeString(mPosterName);
        out.writeString(mReleaseDate);
        out.writeString(mVoteAverage);
        out.writeString(mOverview);
    }

    private MovieData(Parcel in) {
        mId             = in.readString();
        mOriginalTitle  = in.readString();
        mPosterName     = in.readString();
        mReleaseDate    = in.readString();
        mVoteAverage    = in.readString();
        mOverview       = in.readString();
    }

    public static final Parcelable.Creator<MovieData> CREATOR
            = new Parcelable.Creator<MovieData>() {
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    public int describeContents() {
        return 0;
    }

}
