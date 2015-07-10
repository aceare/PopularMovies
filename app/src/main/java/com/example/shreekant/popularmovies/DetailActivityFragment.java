package com.example.shreekant.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
                reviewStr[1] = review.optString("url");
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
                videoStr[0] = video.optString("name") + " /" + video.optString("size");
                videoStr[1] = "https://www.youtube.com/watch?v=" + video.optString("key");
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

    private void inflateVideoPaths(LayoutInflater inflater, View rootView, ArrayList<String[]> movieVideoPaths) {

        LinearLayout list = (LinearLayout) rootView.findViewById(R.id.movie_video_list);
        for ( int i=0; i < movieVideoPaths.size(); i++ ) {
            View vi = inflater.inflate(R.layout.movie_video_link, null);
            Log.v(LOG_TAG, "videoPaths[" + i + "][0]: " + movieVideoPaths.get(i)[0]);
            ((TextView)vi.findViewById(R.id.movie_video_link_textview))
                    .setText(movieVideoPaths.get(i)[0]);


            Button playButton = (Button) vi.findViewById(R.id.movie_video_play_button);
            playButton.setTag(movieVideoPaths.get(i)[1]); // Save/attach youtube uri, which would be used onClick.
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uriString = (String) v.getTag();
                    Log.v(LOG_TAG, "Media playback: " + uriString);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(uriString));
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.msg_no_media_app_found), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            list.addView(vi);
        }
    }

    private void inflateMovieReviews(LayoutInflater inflater, View rootView, ArrayList<String[]> movieReviews) {

        LinearLayout list = (LinearLayout) rootView.findViewById(R.id.movie_video_list);
        list = (LinearLayout) rootView.findViewById(R.id.movie_review_list);
        for ( int i=0; i < movieReviews.size(); i++ ) {
            View vi = inflater.inflate(R.layout.movie_review_link, null);
//            Log.v(LOG_TAG, "movieReviews[" + i + "][1]: " + movieReviews.get(i)[1]);
            String text = movieReviews.get(i)[0]; // + ": " + movieReviews.get(i)[1];
            ((TextView)vi.findViewById(R.id.movie_review_link_textview))
                    .setText(text);
// doesn't work
//            String text = "<a href='" + movieReviews.get(i)[1] + "'> " + movieReviews.get(i)[0] + " </a>";
//                    .setText(Html.fromHtml(text));

            Button readButton = (Button) vi.findViewById(R.id.movie_review_read_button);
            readButton.setTag(movieReviews.get(i)[1]); // Save/attach review uri, which would be used onClick.
            readButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uriString = (String) v.getTag();
                    Log.v(LOG_TAG, "Review url: " + uriString);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(uriString));
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.msg_no_media_app_found), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            list.addView(vi);
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
                .placeholder(R.raw.hourglass_image)
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
                .appendPath("videos")
                .appendQueryParameter("api_key", MovieData.getApiKey())
                .build();
        responseStr = GetJson.asString(uri);
        ArrayList<String[]> movieVideoPaths = getMovieVideoPaths(responseStr); // movieData.setVideoPaths(getMovieVideoPaths(responseStr));
        if (movieVideoPaths != null) {
//        Log.v(LOG_TAG, "movieVideoPaths[0][0]: " + movieVideoPaths.get(0)[0]);
            inflateVideoPaths(inflater, rootView, movieVideoPaths);
        }

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
        if (movieReviews != null) {
//        Log.v(LOG_TAG, "movieReviews[0][0]: " + movieReviews.get(0)[0]);
            inflateMovieReviews(inflater, rootView, movieReviews);
        }

        return rootView;
    }

}
