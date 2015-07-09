package com.example.shreekant.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    public DetailActivityFragment() {
    }

    private ArrayList<String[]> getMovieReviews(String jsonString) {

        ArrayList<String[]> reviewList = new ArrayList<String[]>();
        JSONObject jsonObject;
        int i = 0;

        try {
            jsonObject = new JSONObject(jsonString);
            JSONArray reviews = jsonObject.optJSONArray("results");
            while (true) {
                JSONObject review = reviews.optJSONObject(i);
                if (review == null)
                    break;
                String[] reviewStr = new String[2];
                reviewStr[0] = review.optString("author");
                reviewStr[1] = review.optString("content");
                reviewList.add(reviewStr);
                i++;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        } finally {
            if (i == 0)
                return null;
//            Log.v(LOG_TAG, "reviewList: " + reviewList.toString());
            return reviewList;
        }
    }

    private ArrayList<String[]> getMovieVideoPaths(String jsonString) {

        ArrayList<String[]> videoList = new ArrayList<String[]>();
        JSONObject jsonObject;
        int i = 0;

        try {
            jsonObject = new JSONObject(jsonString);
            JSONArray videos = jsonObject.optJSONArray("results");
            while (true) {
                JSONObject video = videos.optJSONObject(i);
                if (video == null)
                    break;
                String[] videoStr = new String[2];
                videoStr[0] = video.optString("name") + "-" + video.optString("size");
                videoStr[1] = "https://www.youtube.com/?v=" + video.optString("key");
                videoList.add(videoStr);
                i++;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        } finally {
            if (i == 0)
                return null;
//            Log.v(LOG_TAG, "videoList: " + videoList.toString());
            return videoList;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String responseStr;
        Uri uri;

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        MovieData movieData = intent.getParcelableExtra(String.valueOf(R.string.movie_data_key));

        ((TextView) rootView.findViewById(R.id.movie_original_title))
                            .setText(movieData.getOriginalTitle());

        ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_poster);
        String url = movieData.getPosterPath();
        Picasso.with(rootView.getContext())
                .load(url)
                .placeholder(R.raw.image_placeholder)
                .into(imageView);

        ((TextView) rootView.findViewById(R.id.movie_release_date))
                            .setText(movieData.getReleaseDate());
        ((TextView) rootView.findViewById(R.id.movie_vote_average))
                            .setText(movieData.getVoteAverage());
        ((TextView) rootView.findViewById(R.id.movie_overview))
                            .setText(movieData.getOverview());


        // http://api.themoviedb.org/3/movie/135397/reviews?api_key=631e0443b035045177f280222421ecd1
        uri = new Uri.Builder()
                .scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieData.getId())
                .appendPath("reviews")
                .appendQueryParameter("api_key", MovieData.getApiKey())
                .build();
        responseStr = GetJson.asString(uri);
        ArrayList<String[]> movieReviews = getMovieReviews(responseStr); // movieData.setReviews(getMovieReviews(responseStr));

        // http://api.themoviedb.org/3/movie/135397/reviews?api_key=631e0443b035045177f280222421ecd1
        uri = new Uri.Builder()
                .scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieData.getId())
                .appendPath("videos")
                .appendQueryParameter("api_key", MovieData.getApiKey())
                .build();
        responseStr = GetJson.asString(uri);
        ArrayList<String[]> movieVideoPaths = getMovieVideoPaths(responseStr); // movieData.setVideoPaths(getMovieVideoPaths(responseStr));

        LinearLayout list = (LinearLayout) rootView.findViewById(R.id.movie_video_list);
//        Log.v(LOG_TAG, "videoPaths[0][0]: " + movieVideoPaths.get(0)[0]);

        for ( int i=0; i < movieVideoPaths.size(); i++ ) {
            View vi = inflater.inflate(R.layout.movie_video_link, null);
            Log.v(LOG_TAG, "videoPaths[" + i + "][0]: " + movieVideoPaths.get(i)[0]);
            ((TextView)vi.findViewById(R.id.movie_video_link_textview))
                                        .setText(movieVideoPaths.get(i)[0]);
            list.addView(vi);
        }
        return rootView;
    }
}
