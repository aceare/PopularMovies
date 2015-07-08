package com.example.shreekant.popularmovies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String API_KEY = "631e0443b035045177f280222421ecd1";
    private ArrayAdapter<String> mAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> initialList = new ArrayList<String>();
//        String[] initialArray = { "line0", "line1", "line2", "line3", "line4", "line5", "line6", "line7", "line8", "line9"};
//        ArrayList<String> initialList = new ArrayList<String>(Arrays.asList(initialArray));

//        mAdapter = new ArrayAdapter(getActivity(), R.layout.list_item_movie, R.id.list_item_movie_textview, initialList);
//        ListView listView = (ListView) rootView.findViewById(R.id.listview_movies);
//        listView.setAdapter(mAdapter);

        mAdapter = new ArrayAdapter(getActivity(), R.layout.list_item_movie, R.id.list_item_movie_textview, initialList);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mAdapter);

/*        mAdapter = new ArrayAdapter(getActivity(), R.layout.movie_item_image, R.id.movie_item_imageview, initialList);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mAdapter);
*/
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchMovieList();
    }

    private void fetchMovieList() {
/*
        String[] initialArray = { "line0", "line1", "line2", "line3", "line4", "line5", "line6", "line7", "line8", "line9",
                                "line10", "line11", "line12", "line13", "line14", "line15", "line16", "line17", "line18", "line19"};
//        ArrayList<String> initialList = new ArrayList<String>(Arrays.asList(initialArray));

        mAdapter.clear();
        for(String str : initialArray) {
            mAdapter.add(str);
        }
*/
        new FetchMovieListTask().execute("dummy");

    }

    private class FetchMovieListTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchMovieListTask.class.getSimpleName();

        private String[] getMovieList(String json)
            throws JSONException {

            ArrayList<String> moviesInfo = new ArrayList<String>();

            JSONObject jsonObject;
            jsonObject = new JSONObject(json);
            JSONArray movies = jsonObject.optJSONArray("results");
            int i = 0;
            while (true) {
                JSONObject movie = movies.optJSONObject(i);
                if (movie == null)
                    break;
                String movieInfo = i + ": "
                                + movie.optString("id") + ", "
                                + movie.optString("title") + ", "
                                + movie.optString("poster_path");
//                Log.v(LOG_TAG, movieInfo);
                moviesInfo.add(movieInfo);
                i++;
            }

            String[] moviesInfoArray = {};
            moviesInfoArray = moviesInfo.toArray(moviesInfoArray);
//            Log.v(LOG_TAG, moviesInfoArray);
            return moviesInfoArray;
        }

        @Override
        protected String[] doInBackground(String... params) {

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String prefSortBy = sharedPref.getString(getString(R.string.pref_sort_by_key),
                    getString(R.string.pref_sort_by_default_value));
            String sortByQueryValue;

            if (prefSortBy.equals(getString(R.string.pref_sort_by_popularity_value))) {
                sortByQueryValue = "popularity.desc";
            }
            else { // if (prefSortBy.equals(getString(R.string.pref_sort_by_ratings_value))) {
                sortByQueryValue = "vote_average.desc";
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String responseStr = null;

            try {
                // http://api.themoviedb.org/3/discover/movie?api_key=631e0443b035045177f280222421ecd1&sort_by=popularity.desc
                // http://api.themoviedb.org/3/discover/movie?api_key=631e0443b035045177f280222421ecd1&sort_by=vote_average.desc
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("api_key", API_KEY)
                        .appendQueryParameter("sort_by", sortByQueryValue)
                        .build();
                Log.v(LOG_TAG, uri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) new URL(uri.toString()).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                responseStr = buffer.toString();

//                Log.v(LOG_TAG, "Response: " + responseStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieList(responseStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;

            /*
            String[] initialArray = {"line00", "line01", "line02", "line03", "line04", "line05", "line06", "line07", "line08", "line09",
                    "line10", "line11", "line12", "line13", "line14", "line15", "line16", "line17", "line18", "line19"};
            return initialArray;
            */
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (strings != null) {
                mAdapter.clear();
                for (String str : strings) {
                    mAdapter.add(str);
                }
            }
        }
    }
}
