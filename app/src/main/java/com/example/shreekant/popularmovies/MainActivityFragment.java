package com.example.shreekant.popularmovies;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
//    private ArrayAdapter<String> mAdapter;
    private ImageAdapter mAdapter;

    public MainActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<MovieData> initialList = new ArrayList<MovieData>();
//        ArrayList<String> initialList = new ArrayList<String>();
//        String[] initialArray = { "line0", "line1", "line2", "line3", "line4", "line5", "line6", "line7", "line8", "line9"};
//        ArrayList<String> initialList = new ArrayList<String>(Arrays.asList(initialArray));

//        mAdapter = new ArrayAdapter(getActivity(), R.layout.list_item_movie, R.id.list_item_movie_textview, initialList);
//        ListView listView = (ListView) rootView.findViewById(R.id.listview_movies);
//        listView.setAdapter(mAdapter);

/*
    // ??? This layout/view doesn't seem to make much difference as even TextView also shows gridview images!!
    // May be that's because of below overridden getView()???
        mAdapter = new ImageAdapter(getActivity(), R.layout.list_item_movie, R.id.list_item_movie_textview, initialList);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mAdapter);
*/
        mAdapter = new ImageAdapter(getActivity(), R.layout.movie_item_image, R.id.movie_item_imageview, initialList);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MovieData movieData = mAdapter.getItem(position);
                Intent detailActivityIntent = new Intent(getActivity(), DetailActivity.class);
                detailActivityIntent.putExtra(String.valueOf(R.string.movie_data_key), movieData);
                startActivity(detailActivityIntent);
            }
        });

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

    private class FetchMovieListTask extends AsyncTask<String, Void, ArrayList<MovieData>> {
        private final String LOG_TAG = FetchMovieListTask.class.getSimpleName();

        private ArrayList<MovieData> getMovieList(String jsonString) {

            ArrayList<MovieData> movieList = new ArrayList<MovieData>();

            JSONObject jsonObject;
            int i = 0;

            try {
                jsonObject = new JSONObject(jsonString);
                JSONArray movies = jsonObject.optJSONArray("results");
                while (true) {
                    JSONObject movie = movies.optJSONObject(i);
                    if (movie == null)
                        break;
                    MovieData movieData = new MovieData(
                            movie.optString("id"),
                            movie.optString("original_title"),
                            movie.optString("poster_path"),
                            movie.optString("release_date"),
                            movie.optString("vote_average"),
                            movie.optString("overview"));
                    //movieData.setReviews(null);
                    //movieData.setVideoPaths(null);

                    movieList.add(movieData);
                    i++;
                }

            /*
            String[] moviesInfoArray = {};
            moviesInfoArray = moviesInfo.toArray(moviesInfoArray);
//            Log.v(LOG_TAG, moviesInfoArray);
            return moviesInfoArray;
            */
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            } finally {
                if (i == 0)
                    return null;
                return movieList;
            }
        }

        @Override
        protected ArrayList<MovieData> doInBackground(String... params) {

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

            // Will contain the raw JSON response as a string.
            String responseStr = null;

            // http://api.themoviedb.org/3/discover/movie?api_key=631e0443b035045177f280222421ecd1&sort_by=popularity.desc
            // http://api.themoviedb.org/3/discover/movie?api_key=631e0443b035045177f280222421ecd1&sort_by=vote_average.desc
            Uri uri = new Uri.Builder()
                    .scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("api_key", MovieData.getApiKey())
                    .appendQueryParameter("sort_by", sortByQueryValue)
                    .build();
            responseStr = GetJson.asString(uri);
            if (responseStr == null) {
                return null;
            }

            return getMovieList(responseStr);

            /*
            String[] initialArray = {"line00", "line01", "line02", "line03", "line04", "line05", "line06", "line07", "line08", "line09",
                    "line10", "line11", "line12", "line13", "line14", "line15", "line16", "line17", "line18", "line19"};
            return initialArray;
            */
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> movieList) {
            super.onPostExecute(movieList);
            if (movieList == null) {
                Toast.makeText(getActivity(), getString(R.string.msg_no_internet_connection), Toast.LENGTH_SHORT).show();
            }else {
                mAdapter.clear();
                for (MovieData movieData : movieList) {
                    mAdapter.add(movieData);
                }
            }
        }
    }


    private class ImageAdapter extends ArrayAdapter<MovieData> {
        private final String LOG_TAG = ImageAdapter.class.getSimpleName();

        public ImageAdapter(Context context, int resource, int textViewResourceId, ArrayList<MovieData> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, initialize some attributes
                imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new GridView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                imageView = (ImageView) convertView;
            }

            // Get the image URL for the current position.
            // String url = "http://image.tmdb.org/t/p/w92" + getItem(position);
            MovieData movieData = getItem(position);
            String url = movieData.getPosterPath();
            // "http://image.tmdb.org/t/p/w92" + movieData.getPosterName();
            // "http://image.tmdb.org/t/p/w185" + getItem(position);
            Log.v(LOG_TAG, position + ": " + url);

            // Trigger the download of the URL asynchronously into the image view.
            // Takes care of downloading & showing it in sync with UI thread, as well as caching images:
            Picasso.with(getContext())
                    .load(url)
                    .placeholder(R.raw.hourglass_image)
                    .into(imageView);
            /*
            Picasso.with(mContext) //
                    .load(url) //
                    .placeholder(R.drawable.placeholder) //
                    .error(R.drawable.error) //
                    .fit() //
                    .tag(context) //
                    .into(view);
            */
            return imageView;
        }
    }
}
