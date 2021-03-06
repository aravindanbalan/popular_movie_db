package udacity.com.popularmoviedb.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by arbalan on 8/13/16.
 */

public class Movie implements Parcelable {
    private String mId;
    private String mTitle;
    private String mPosterUrl;
    private String mMovieOverview;
    private double mVoteAverage;
    private double mPopularity;
    private String mMovieReleaseDate;

    public Movie(){
    }

    public Movie(Parcel source) {
        mId = source.readString();
        mTitle = source.readString();
        mPosterUrl = source.readString();
        mMovieOverview = source.readString();
        mVoteAverage = source.readDouble();
        mPopularity = source.readDouble();
        mMovieReleaseDate = source.readString();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        mPosterUrl = posterUrl;
    }

    public String getMovieOverview() {
        return mMovieOverview;
    }

    public void setMovieOverview(String movieOverview) {
        mMovieOverview = movieOverview;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.mVoteAverage = voteAverage;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public void setPopularity(double popularity) {
        mPopularity = popularity;
    }

    public String getMovieReleaseDate() {
        return mMovieReleaseDate;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        mMovieReleaseDate = movieReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mPosterUrl);
        dest.writeString(mMovieOverview);
        dest.writeDouble(mVoteAverage);
        dest.writeDouble(mPopularity);
        dest.writeString(mMovieReleaseDate);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
